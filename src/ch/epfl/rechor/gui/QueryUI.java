package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The part of the interface that allows to create a journey
 * @param rootNode Node at the root of the heritage
 * @param depStopO current selected departure stop name for the journey
 * @param arrStopO current selected arriving stop name for the journey
 * @param dateO current selected departure date for the journey
 * @param timeO current selected departure time for the journey
 * @author Guillaume Pommier (401065)
 */
public record QueryUI(Node rootNode,
                      ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO,
                      ObservableValue<LocalDate> dateO,
                      ObservableValue<LocalTime> timeO
){

    /**
     * Creates an instance of QueryUI, used to select the starting point and end point of a journey
     * as well as the date and time
     * @param stopIndex the class used to suggest and validate stop names
     * @return an instance of QueryUI with the given properties
     */
    public static QueryUI create(StopIndex stopIndex) {
        VBox mainBox = new VBox();
        mainBox.getStylesheets().add("query.css");

        // Search for stops part
        HBox searchHBox = new HBox();
        Label depStopLabel = new Label("Départ\u202F:");
        StopField depStopField = StopField.create(stopIndex);
        TextField depStopTextField = depStopField.textField();
        depStopTextField.setId("depStop");
        Button changeButton = new Button("↔"); // <-- à changer ce n'est pas correcte
        Label arrStopLabel = new Label("Arrivée\u202F:");
        StopField arrStopField = StopField.create(stopIndex);
        TextField arrStopTextField = arrStopField.textField();
        changeButton.setOnAction(
                x -> {
                    String depStopName = depStopField.stopO().getValue();
                    depStopField.setTo(arrStopField.stopO().getValue());
                    arrStopField.setTo(depStopName);
                }
        );
        searchHBox.getChildren().addAll(
                depStopLabel,depStopTextField,
                changeButton,
                arrStopLabel,arrStopTextField
        );

        // Setting the date and time part
        HBox dateTimeHBox = new HBox();
        Label dateLabel = new Label("Date\u202F:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setId("date");
        Label timeLabel = new Label("Heure\u202F:");
        TextField timeTextField = new TextField();
        timeTextField.setId("time");

        // Formatter for displaying time in 24-hour format with leading zeros
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Formatter for parsing user input in 24-hour format, accepting single-digit hours,
        // using French locale
        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.FRANCE);

        // Converter to transform between LocalTime objects and strings using the defined display
        // and parse formats
        LocalTimeStringConverter converter = new LocalTimeStringConverter(displayFormatter, parseFormatter);

        TextFormatter<LocalTime> textFormatter = new TextFormatter<>(converter);
        timeTextField.setTextFormatter(textFormatter);
        LocalTime now = LocalTime.now();
        textFormatter.setValue(LocalTime.of(now.getHour(),now.getMinute()));
        dateTimeHBox.getChildren().addAll(
                dateLabel,datePicker,
                timeLabel,timeTextField
        );

        mainBox.getChildren().addAll(searchHBox,dateTimeHBox);

        ObjectProperty<LocalDate> dateO = new SimpleObjectProperty<>(datePicker.getValue());
        datePicker.valueProperty().subscribe(dateO::set);
        ObjectProperty<LocalTime> timeO = new SimpleObjectProperty<>(
                LocalTime.parse(timeTextField.getText(),displayFormatter)
        );
        timeTextField.addEventHandler(
                KeyEvent.KEY_PRESSED,
                x -> {
                    if (x.getCode().equals(KeyCode.ENTER)) {
                        mainBox.requestFocus();
                        timeO.set(LocalTime.parse(timeTextField.getText(), displayFormatter));
                        x.consume();
                    }
                }
        );
        return new QueryUI(mainBox,depStopField.stopO(),arrStopField.stopO(),dateO,timeO);
    }
}
