package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.RED;

public record DetailUI(Node rootNode) {
    private final static int RED_LINE_WIDTH = 2;

    /**
     * create the visual representation of the journey in the interface
     * @param observableJourney view on the information about a journeys
     * @return the Javafx representation that will be seen on the interface
     * @author Matthias Gibon (397827)
     */
    public static DetailUI create(ObservableValue<Journey> observableJourney) {

        //create all nodes of the interface
        ScrollPane scroll = new ScrollPane();
        scroll.getStylesheets().add("detail.css");
        scroll.setId("detail");
        StackPane stack1 = new StackPane();
        VBox emptyVBox = new VBox();
        emptyVBox.setId("no-journey");
        VBox vBox = new VBox();
        Text noJourneyText = new Text("Aucun Voyage");
        StackPane stack2 = new StackPane();
        Pane annotationPane = new Pane();
        HBox hBox = new HBox();
        hBox.setId("buttons");
        Button calendarButton = new Button("calendrier");
        Button mapButton = new Button("Carte");
        annotationPane.setId("annotations");

        //will be used later for the line
        List<Pair<Circle,Circle>> circleList = new ArrayList<>();

        //grid that will contain all the information on the legs
        GridPane legGrid = new GridPane(){
            /**
             * calls the method layoutChildren and updates the red lines in real time
             */
            @Override
            protected void layoutChildren(){
                super.layoutChildren();
                //add a line from the first circle to the last if there are any
                if(!circleList.isEmpty()){
                    annotationPane.getChildren().clear();
                    for(Pair<Circle,Circle> pair : circleList){
                        Line redCircleLine = new Line(pair.getKey().getBoundsInParent().getCenterX(),
                                pair.getKey().getBoundsInParent().getCenterY(),
                                pair.getValue().getBoundsInParent().getCenterX(),
                                pair.getValue().getBoundsInParent().getCenterY());

                        redCircleLine.setStrokeWidth(RED_LINE_WIDTH); // width of the line
                        redCircleLine.setStroke(RED); // color of the line
                        annotationPane.getChildren().add(redCircleLine);
                    }
                }
            }
        };
        legGrid.setId("legs");

        //attach the nodes to each other following the hierarchy
        scroll.setContent(stack1);
        stack1.getChildren().addAll(emptyVBox, vBox);
        vBox.getChildren().addAll(stack2, hBox);
        stack2.getChildren().addAll(annotationPane, legGrid);
        hBox.getChildren().addAll(calendarButton, mapButton);
        emptyVBox.getChildren().add(noJourneyText);

        //show "Aucun Voyage" if the journey is null
        BooleanBinding booleanBindValue = Bindings.createBooleanBinding(
                ()->!(observableJourney.getValue()==null),
                observableJourney);
        vBox.visibleProperty().bind(booleanBindValue);
        emptyVBox.visibleProperty().bind(booleanBindValue.not());

        //create the information of the journey everytime the given journey changes
        observableJourney.subscribe(newJourney->{

            //destroy the previous information:
            circleList.clear();
            calendarButton.cancelButtonProperty();
            mapButton.cancelButtonProperty();
            legGrid.getChildren().clear();
            annotationPane.getChildren().clear();


            if(newJourney!=null) {
                //number of legs in the journey
                int legsSize = newJourney.legs().size();

                //create the representation of each leg of the journey in the interface
                for (int i = 0; i < legsSize; i++) {
                    Journey.Leg leg = newJourney.legs().get(i);

                    if (leg instanceof Journey.Leg.Foot) { //if the leg is done by foot
                        legGrid.add(new Text(FormatterFr.formatLeg((Journey.Leg.Foot) leg)), 2,
                                legGrid.getRowCount(),
                                2,
                                1);
                    } else { //iff the leg is done via transports

                        //information on the start of the leg
                        int index = legGrid.getRowCount();
                        Text depTime = new Text(FormatterFr.formatTime(leg.depTime()));
                        depTime.getStyleClass().add("departure");
                        legGrid.add(depTime,
                                0,
                                index);
                        Circle depCircle = new Circle(3);
                        legGrid.add(depCircle,
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
                        ImageView icon = new ImageView(VehicleIcons.iconFor(((Journey.Leg.Transport) leg).vehicle()));
                        icon.setFitHeight(31);
                        icon.setFitWidth(31);
                        legGrid.add(icon,
                                0,
                                index, 1, (leg.intermediateStops().isEmpty() ? 1 : 2));
                        legGrid.add(new Text(FormatterFr.formatRouteDestination((Journey.Leg.Transport) leg)),
                                2,
                                index,
                                2,
                                1);
                        index++;

                        //add the accordion with intermediates stop if necessary
                        if (!leg.intermediateStops().isEmpty()) {
                            Accordion accordion = new Accordion();
                            GridPane intermediateStopGrid = new GridPane();
                            TitledPane accordionText = new TitledPane();

                            intermediateStopGrid.getStyleClass().add("intermediate-stops");
                            accordionText.setText(leg.intermediateStops().size() + " arrêts, "
                                    + FormatterFr.formatDuration(leg.duration()));
                            accordionText.setContent(intermediateStopGrid);
                            accordion.getPanes().add(accordionText);


                            int intermediateStopIndex = 0;
                            for (Journey.Leg.IntermediateStop intermediateStop : leg.intermediateStops()) {
                                intermediateStopGrid.add(new Text(FormatterFr.formatTime(intermediateStop.arrTime())),
                                        0,
                                        intermediateStopIndex);
                                intermediateStopGrid.add(new Text(FormatterFr.formatTime(intermediateStop.depTime())),
                                        1,
                                        intermediateStopIndex);
                                intermediateStopGrid.add(new Text(intermediateStop.stop().name()),
                                        2,
                                        intermediateStopIndex);
                                intermediateStopIndex++;
                            }
                            legGrid.add(accordion, 2, index, 2, 1);
                            index++;
                        }

                        //information on arrival of the leg
                        legGrid.add(new Text(FormatterFr.formatTime(leg.arrTime())), 0, index);
                        Circle arrCircle = new Circle(3);
                        legGrid.add(arrCircle, 1, index);
                        legGrid.add(new Text(leg.arrStop().name()), 2, index);
                        legGrid.add(new Text(FormatterFr.formatPlatformName(leg.arrStop())), 3, index);

                        //add the dep and arr circle to our list of pair for the futur line between them
                        circleList.add(new Pair<>(depCircle, arrCircle));
                    }
                }

                //button interactions:

                //Calendar
                calendarButton.setOnAction(x -> {
                    FileChooser fileChooser = new FileChooser();
                    String fileName = "voyage_" + DateTimeFormatter.ISO_DATE.
                            format(newJourney.depTime()) + ".ics";
                    fileChooser.setInitialFileName(fileName);
                    File file = fileChooser.showSaveDialog(null);
                    System.out.println(JourneyIcalConverter.toIcalendar(newJourney));
                    if (file != null) {
                        try {
                            Files.writeString(file.toPath(),
                                    JourneyIcalConverter.toIcalendar(newJourney));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }


                });

                //map:
                mapButton.setOnAction((x) -> {
                    if (Desktop.isDesktopSupported()) {
                        String url = "https://umap.osm.ch/fr/map/?data=" + JourneyGeoJsonConverter.toGeoJson(observableJourney.getValue());
                        System.out.println(JourneyGeoJsonConverter.toGeoJson(observableJourney.getValue()));
                        String encodedUrl = url.replace("\"", "%22").replace("[", "%5B").replace("]", "%5D").replace("{", "%7B").replace("}", "%7D");
                        try { //open a browser page from a given URI
                            Desktop.getDesktop().browse(new URI(encodedUrl));
                        } catch (Exception e) {
                            System.out.println("Il y a eu un problème");
                        }
                    }
                });
            }
        });


        return new DetailUI(scroll);
    }
}
