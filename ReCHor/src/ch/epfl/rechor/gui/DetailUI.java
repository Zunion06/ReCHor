package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record DetailUI(Node rootNode) {

    public static DetailUI create(ObservableValue<Journey> observableJourney) {

        ScrollPane scroll = new ScrollPane();
        scroll.getStylesheets().add("detail.css");
        scroll.setId("detail");
        StackPane stack1 = new StackPane();
        VBox emptyVBox = new VBox();
        emptyVBox.setId("no-journey");
        VBox vBox = new VBox();
        Text noJourneyText = new Text("Aucun Voyage");
        StackPane stack2 = new StackPane();
        Pane pane = new Pane();
        Line line = new Line();
        GridPane legGrid = new GridPane();
        legGrid.setId("legs");
        HBox hBox = new HBox();
        hBox.setId("buttons");
        Button calendarButton = new Button("calendrier");
        Button mapButton = new Button("Carte");
        pane.setId("annotations");

        scroll.setContent(stack1);
        stack1.getChildren().addAll(emptyVBox, vBox);
        vBox.getChildren().addAll(stack2, legGrid);
        stack2.getChildren().addAll(pane, legGrid);
        pane.getChildren().add(line);
        hBox.getChildren().addAll(calendarButton, mapButton);
        emptyVBox.getChildren().add(noJourneyText);

        Scene scene = new Scene(scroll);

        if (!observableJourney.getValue().legs().isEmpty()) {
            emptyVBox.setVisible(false);
        }
        for (int i = 0; i < observableJourney.getValue().legs().size(); i++) {
            Journey.Leg leg = observableJourney.getValue().legs().get(i);

            if (leg instanceof Journey.Leg.Foot) {
                legGrid.add(new Text(FormatterFr.formatLeg((Journey.Leg.Foot) leg)), 1,
                        legGrid.getChildren().size(),
                        2,
                        1);
            } else {

                //Intermediate Stop grid:

                int index = legGrid.getChildren().size();
                Text depTime = new Text(FormatterFr.formatTime(leg.depTime()));
                depTime.getStyleClass().add("departure");
                legGrid.add(depTime,
                        0,
                        index);
                legGrid.add(new Circle(3),
                        1,
                        index);
                legGrid.add(new Text(leg.depStop().name()),
                        2,
                        index);
                Text depPlatform = new Text(FormatterFr.formatPlatformName(leg.depStop()));
                depPlatform.getStyleClass().add("departure");
                legGrid.add(depPlatform,
                        3,
                        index);
                index++;
                legGrid.add(new ImageView(VehicleIcons.iconFor(((Journey.Leg.Transport) leg).vehicle())),
                        0,
                        index, 1, (leg.intermediateStops().isEmpty() ? 1 : 2));
                legGrid.add(new Text(FormatterFr.formatRouteDestination((Journey.Leg.Transport) leg)),
                        2,
                        index,
                        2,
                        1);
                index++;
                if (!leg.intermediateStops().isEmpty()) {
                    legGrid.add(new Accordion(), 2, index, 2, 1);
                    index++;
                }
                legGrid.add(new Text(FormatterFr.formatTime(leg.arrTime())), 0, index);
                legGrid.add(new Circle(3), 1, index);
                legGrid.add(new Text(leg.arrStop().name()), 2, index);
                legGrid.add(new Text(FormatterFr.formatPlatformName(leg.arrStop())), 3, index);
            }
        }

        return new DetailUI(scroll);
    }
}
