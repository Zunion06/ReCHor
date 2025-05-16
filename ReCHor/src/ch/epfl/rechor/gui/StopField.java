package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public record StopField(TextField textField, ObservableValue<String> stopO) {

    public static StopField create(StopIndex stopIndex) {
        Popup popup = new Popup();
        popup.setHideOnEscape(false);

        ListView<String> listView = new ListView<>();
        listView.setFocusTraversable(false);
        listView.setMaxHeight(240);
        popup.getContent().add(listView);

        TextField textField = new TextField();
        ObjectProperty<String> stopO = new SimpleObjectProperty<>("");
        textField.focusedProperty().addListener(
                (obs,oldVal,newVal) ->
                {
                    if (newVal) {
                        Bounds bounds = textField.localToScreen(textField.boundsInLocalProperty().getValue());
                        popup.show(textField, bounds.getMinX(), bounds.getMaxY());
                    }
                    else {
                        popup.hide();
                        if (isStopValid(stopIndex,listView.getItems(),textField.getText()))
                            textField.setText("");
                        else
                            stopO.set(textField.getText());
                    }
                }
        );

        List<String> result = new ArrayList<>(stopIndex.stopsMatching(stopO.getValue(),50));

        ObservableList<String> recommendedStops = FXCollections.observableList(result);

        textField.textProperty().addListener(
                (obs,oldVal,newVal) -> {
                    recommendedStops.setAll(stopIndex.stopsMatching(textField.getText(), 50));
                }
        );

        listView.setOnMouseClicked(
                x-> {
                    if (x.getClickCount()==2) {
                        textField.setText(listView.getSelectionModel().getSelectedItem());
                        popup.hide();
                    }
                }
        );

        textField.addEventHandler(
                KeyEvent.KEY_PRESSED,
                x -> {
                    switch (x.getCode()) {
                        case KeyCode.UP -> {
                            SelectionModel<String> selection = listView.getSelectionModel();
                            selection.select(selection.getSelectedIndex()-1);
                            x.consume();
                        }
                        case KeyCode.DOWN -> {
                            SelectionModel<String> selection = listView.getSelectionModel();
                            selection.select(selection.getSelectedIndex()+1);
                            x.consume();
                        }
                        case ENTER -> {
                            String selectedText = listView.getSelectionModel().getSelectedItem();
                            if (selectedText!=null)
                                textField.setText(selectedText);
                            popup.hide();
                            x.consume();
                        }
                    }
                }
        );



        listView.setItems(recommendedStops);
        return new StopField(textField,stopO);
    }

    public void setTo(String stopName) {
        textField.setText(stopName);
        if (stopO instanceof ObjectProperty<String>)
            ((ObjectProperty<String>) stopO).set(stopName);
    }

    private static boolean isStopValid(StopIndex stopIndex,List<String> list, String name){
        return list.stream().noneMatch(name::equals);
    }
}
