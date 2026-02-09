package server;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Explicit Logic Errors (404, 422, etc. thrown via ResponseStatusException)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex) {
        return buildResponse(ex.getStatusCode(), ex.getReason());
    }

    // 2. Handle 404 Not Found (Wrong URL)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "The requested endpoint does not exist.");
    }

    // 3. Handle 405 Method Not Allowed (e.g. POST instead of GET)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, 
            "Method " + ex.getMethod() + " is not supported for this path.");
    }

    // 4.1 Handle 400 Bad Request (Missing ?query=... or invalid Enum)
    @ExceptionHandler({MissingServletRequestParameterException.class,})
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request: " + ex.getMessage());
    }

    // 4.2 Handle with better clarity an error in an enum argument (e.g. type=INVALID instead of type=DEPARTURE)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' has an invalid value.", ex.getName());
        
        // Check if the failure was specifically an Enum conversion
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            message = String.format("Invalid value for '%s'. Accepted values are: %s", 
                ex.getName(), 
                java.util.Arrays.toString(ex.getRequiredType().getEnumConstants()));
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 5. Handle Validation Errors (Min/Max/NotNull)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Object> handleValidation(HandlerMethodValidationException ex) {
        String msg = ex.getAllValidationResults().stream()
                .flatMap(res -> res.getResolvableErrors().stream())
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + msg);
    }

    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
    }

    // 6. FINAL SAFETY NET (The 500 Handler)
    @ExceptionHandler(Exception.class)
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        // IMPORTANT: Log this so you can debug!
        ex.printStackTrace(); 
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occurred.");
    }

    // Helper 
    private ResponseEntity<Object> buildResponse(org.springframework.http.HttpStatusCode status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}