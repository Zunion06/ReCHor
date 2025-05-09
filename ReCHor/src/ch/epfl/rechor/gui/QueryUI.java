package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record QueryUI(Node rootNode,
                      ObservableValue<String> depStopO,
                      ObservableValue<String> arrStopO,
                      ObservableValue<LocalDate> dateO,
                      ObservableValue<LocalTime> timeO
){
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
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("H:mm", Locale.FRANCE);
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

        ObservableValue<LocalDate> dateO = new ObservableValueBase<>() {
            @Override
            public LocalDate getValue() {
                return datePicker.getValue();
            }
        };
        ObservableValue<LocalTime> timeO = new ObservableValueBase<>() {
            @Override
            public LocalTime getValue() {
                return LocalTime.parse(timeTextField.getText(),displayFormatter);
            }
        };

        mainBox.isFocused();
        return new QueryUI(mainBox,depStopField.stopO(),arrStopField.stopO(),dateO,timeO);
    }
}
