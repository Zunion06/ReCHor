package ch.epfl.rechor.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.rechor.StopIndex;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;

/**
 * A UI component that wraps a text field with dynamic stop name suggestions.
  * It displays a popup list of recommended stop names as the user types and updates
 * the selected stop value when a valid name is confirmed.
 *
 * @param textField the input field for typing a stop name
 * @param stopO an observable value representing the currently selected stop name
 * @author Guillaume Pommier (401065)
 */
public record StopField(TextField textField, ObservableValue<String> stopO) {
    private static final int LIST_VIEW_MAX_HEIGHT = 240;
    private static final int MAX_NUMBER_OF_NAMES = 50;

    /**
     * Creates a new StopField with autocomplete functionality based on a given StopIndex.
     * Displays a popup with a list of matching stop names as the user types. The list
     * updates dynamically and supports selection via keyboard or mouse.
     *
     * @param stopIndex the class used to suggest and validate stop names
     * @return a StopField instance with autocomplete behavior
     */
    public static StopField create(StopIndex stopIndex) {
        Popup popup = new Popup();
        popup.setHideOnEscape(false);

        ListView<String> listView = new ListView<>();
        listView.setFocusTraversable(false);
        listView.setMaxHeight(LIST_VIEW_MAX_HEIGHT);
        popup.getContent().add(listView);

        TextField textField = new TextField();
        ObjectProperty<String> stopO = new SimpleObjectProperty<>("");
        textField.focusedProperty().subscribe(
                newVal -> {
                    if (newVal) {
                        Bounds bounds = textField.localToScreen(textField.boundsInLocalProperty().getValue());
                        popup.show(textField, bounds.getMinX(), bounds.getMaxY());
                    }
                    else {
                        popup.hide();
                        String text = textField.getText();
                        if (listView.getItems().stream().noneMatch(text::equals))
                            textField.setText("");
                        else
                            stopO.set(text);
                    }
                }
        );

        List<String> result = new ArrayList<>(stopIndex.stopsMatching(stopO.getValue(), MAX_NUMBER_OF_NAMES));

        ObservableList<String> recommendedStops = FXCollections.observableList(result);

        textField.textProperty().subscribe(
                nV ->
                    recommendedStops.setAll(
                            stopIndex.stopsMatching(textField.getText(), MAX_NUMBER_OF_NAMES)
                    )

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
                            int newSelectionIndex = selection.getSelectedIndex() - 1;
                            selection.select(newSelectionIndex);
                            listView.scrollTo(newSelectionIndex);
                            x.consume();
                        }
                        case KeyCode.DOWN -> {
                            SelectionModel<String> selection = listView.getSelectionModel();
                            int newSelectionIndex = selection.getSelectedIndex() + 1;
                            selection.select(newSelectionIndex);
                            listView.scrollTo(newSelectionIndex);
                            x.consume();
                        }
                        case ENTER -> {
                            String selectedText = listView.getSelectionModel().getSelectedItem();
                            if (selectedText!=null) {
                                textField.setText(selectedText);
                                recommendedStops.setAll(stopIndex.stopsMatching(selectedText, MAX_NUMBER_OF_NAMES));
                            }
                            x.consume();
                        }
                        case TAB -> {
                            String selectedText = listView.getSelectionModel().getSelectedItem();
                            if (selectedText!=null) {
                                textField.setText(selectedText);
                                recommendedStops.setAll(stopIndex.stopsMatching(selectedText, MAX_NUMBER_OF_NAMES));
                            }
                        }
                        default -> {}
                    }
                }
        );
        listView.setItems(recommendedStops);
        return new StopField(textField,stopO);
    }

    /**
     * Set the text in the TextField and the value of stopO to the argument
     * @param stopName the stop name to set
     */
    public void setTo(String stopName) {
        textField.setText(stopName);
        if (stopO instanceof ObjectProperty<String> objectProperty)
            objectProperty.set(stopName);
    }
}
