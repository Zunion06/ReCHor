package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent the interface of the list of journeys in the final product
 * @param rootNode Node at the root of the heritage
 * @param selectedJourneyO current selected journey whose information will be shown
 * @author Matthias Gibon (397827)
 */
public record SummaryUI(Node rootNode, ObservableValue<Journey> selectedJourneyO) {

    public static SummaryUI create(ObservableValue<List<Journey>> journeyList,
                                   ObservableValue<LocalTime> journeyTimes){

        ListView<Journey> journeyView = new ListView<>();
        journeyView.getStylesheets().add("summary.css");

        journeyView.getItems().addAll(journeyList.getValue());
        journeyView.setCellFactory(x->new JourneyListCell());

        journeyView.getSelectionModel().select(findFirstJourneySelect(journeyList,journeyTimes.getValue()));
        journeyView.scrollTo(findFirstJourneySelect(journeyList,journeyTimes.getValue()));
        journeyView.scrollTo(findFirstJourneySelect(journeyList,journeyTimes.getValue()));


        journeyTimes.addListener((obs, oldValue, newValue) -> {
            journeyView.getSelectionModel().select(findFirstJourneySelect(journeyList,newValue));
            journeyView.scrollTo(findFirstJourneySelect(journeyList,newValue));
        });

        return new SummaryUI(journeyView,
                new SimpleObjectProperty<>(journeyView.getSelectionModel().getSelectedItem()));
    }

    /**
     * gives an updated version of the Journey List with only journeys after a given time
     * @param journeyList list of all journey of the day
     * @param journeyTimes minimum departing time of the journeys searched
     * @return a copy of journeyList without journey happening before JourneyTimes
     */
    private static Journey findFirstJourneySelect(ObservableValue<List<Journey>> journeyList,
                                            LocalTime journeyTimes){

        for(Journey journey : journeyList.getValue()){
            if(journeyTimes.isBefore(journey.depTime().toLocalTime())){
                return journey;
            }
        }
        return journeyList.getValue().getLast();
    }

    /**
     * Extends ListCell<T>, used to represent journey in a cell of a ListView.
     * @author Matthias Gibon (397827)
     */
    static final private class JourneyListCell extends ListCell<Journey> {
        final private BorderPane borderPane;
        final private Text depTimeText;
        final private ImageView transportImageView;
        final private Text routeText;
        final private Text arrTimeText;
        final private Text durationText;
        final private Line centerLine;
        final private Group centerGroup;
        final private Circle startCircle;
        final private Circle stopCircle;
        final private List<Circle> intermediateCircleList;

        public JourneyListCell(){

            borderPane = new BorderPane();
            borderPane.getStyleClass().add("journey");

            //left
            depTimeText = new Text();
            depTimeText.getStyleClass().add("departure");

            //top
            HBox routeBox = new HBox();
            routeBox.getStyleClass().add("route");
            transportImageView = new ImageView();
            routeText = new Text();

            //right
            arrTimeText = new Text();

            //bottom
            HBox durationBox = new HBox();
            durationBox.getStyleClass().add("duration");
            durationText = new Text();

            //center
            centerLine = new Line();
            centerGroup = new Group();
            startCircle = new Circle(3);
            stopCircle = new Circle(3);
            startCircle.getStyleClass().add("dep-arr");
            stopCircle.getStyleClass().add("dep-arr"); // only for first and last circle
            intermediateCircleList=new ArrayList<>();

            Pane centerPane = new Pane() {
                //Override layoutChildren to place the line and circles where we want them without being resized
                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                    setPrefSize(0, 0);

                    double lineHeight = getHeight() / 2;
                    double lineStart = 5;
                    double lineEnd = getWidth() - 5;
                    double lineLength = lineEnd - lineStart;

                    //line position
                    centerLine.setStartY(lineHeight);
                    centerLine.setEndY(lineHeight);
                    centerLine.setStartX(lineStart);
                    centerLine.setEndX(lineEnd);

                    //start and end circle position
                    startCircle.setCenterX(lineStart);
                    stopCircle.setCenterX(lineEnd);
                    startCircle.setCenterY(lineHeight);
                    stopCircle.setCenterY(lineHeight);

                    //intermediate circle position
                    for (Circle circle : intermediateCircleList) {
                        if (circle.getUserData() != null) {
                            double position = (double) circle.getUserData(); //set in updateItem()
                            circle.setCenterY(lineHeight);
                            circle.setCenterX(+(lineLength * position));
                        }
                    }

                }
            };

            //position nodes in borderPane
            borderPane.setLeft(depTimeText);
            borderPane.setTop(routeBox);
            borderPane.setRight(arrTimeText);
            borderPane.setBottom(durationBox);
            borderPane.setCenter(centerPane);

            //heritage
            routeBox.getChildren().addAll(transportImageView,routeText);
            durationBox.getChildren().add(durationText);
            centerPane.getChildren().addAll(centerLine,centerGroup);
            centerGroup.getChildren().addAll(startCircle,stopCircle);
        }

        @Override
        protected void updateItem(Journey journey, boolean empty){
            super.updateItem(journey,empty);
            if(empty || journey==null){
                setText(null);
                setGraphic(null);
            }
            else{
                depTimeText.setText(FormatterFr.formatTime(journey.depTime()));
                arrTimeText.setText(FormatterFr.formatTime(journey.arrTime()));
                durationText.setText(FormatterFr.formatDuration(journey.duration()));
                transportImageView.setImage(VehicleIcons.iconFor
                        (getFirstTransportLeg(journey).vehicle()));
                transportImageView.setFitWidth(31);
                transportImageView.setFitHeight(31);
                routeText.setText(FormatterFr.formatRouteDestination(getFirstTransportLeg(journey)));


                //Create the circles representing the changes of the journey.
                long journeyDuration = journey.duration().toMinutes();
//                for(int i=1;i<journey.legs().size();i++){ //starts at 1 to avoid the first leg
//                    Journey.Leg leg=journey.legs().get(1);
//                    boolean isNotLastLeg = i!=journey.legs().size()-1;
//                    if(leg instanceof Journey.Leg.Foot && isNotLastLeg){
//
//                        Circle intermediateCircle = new Circle(3);
//                        intermediateCircle.getStyleClass().add("transfer");
//
//                        double position = ((double) Duration.between(journey.depTime(),leg.depTime())
//                                .toMinutes()) / journeyDuration; //ratio on the line where the circle will be
//
//                        intermediateCircle.setUserData(position);
//
//                        intermediateCircleList.add(intermediateCircle);
//                        centerGroup.getChildren().add(intermediateCircle);
//                    }
//                }


                setGraphic(borderPane);

            }
        }

        /**
         * finds the first transport leg of the journey (no journey has none)
         * @param journey Journey we're studying
         * @return the first transport leg of the journey
         */
        private Journey.Leg.Transport getFirstTransportLeg(Journey journey){
            Journey.Leg firstLeg;
            if((firstLeg = journey.legs().getFirst()) instanceof Journey.Leg.Transport){
                return (Journey.Leg.Transport)firstLeg;
            }
            else{
                return (Journey.Leg.Transport)journey.legs().get(1);
            }
        }
    }
}
