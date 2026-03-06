# ReCHor 

Semester project for the **CS-108 – Object-Oriented Programming (Pratique de la programmation orientée-objet)** course at **EPFL**.

## Description

A Java application similar to the SBB itinerary planning app.

(SBB is the Swiss public transportation company for trains, buses, and other transit services.)

## ReCHor Web Server

### Deployment

*(Status as of 06/03/2026)*

The application is currently deployed and accessible at:

- **Main web application:**  
  https://rechor.pommier.dev/  
  *(Fallback access via the AWS public endpoint: http://ec2-44-211-229-81.compute-1.amazonaws.com/)*

- **API documentation (Swagger UI):**  
  https://rechor.pommier.dev/swagger-ui/index.html

### Current configuration

To stay within the limits of the AWS Free Tier, the server is currently running in a "minimal mode".

In this mode, only two dates are available for testing:
- 30/05/2025  
- 31/05/2025  

This helps reduce storage usage while still allowing the main features of the application to be tested.

## Authors

- Matthias Gibon
- Guillaume Pommier

## Timetable

Due to their large size, the main timetable files are not included in this repository.

However you can download them at https://cs108.epfl.ch/archive/25/p/

## Utilisation

Run the javaFX application: 
```bash
mvn javafx:run -pl rechor-desktop
```

Run the HTTP server:
```bash
mvn spring-boot:run -pl rechor-server
```
