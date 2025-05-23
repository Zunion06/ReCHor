package ch.epfl.rechor.gui;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    /**
     * Creates an instance of SummaryUI, listing the journeys of journeyList and selecting the
     * first journey after journeyTimes or, if there are no such journey, the last journey of the
     * list.
     * @param journeyList view on the list of journey to be listed
     * @param journeyTimes view on the minimum time of the journey
     * @return an instance of SummaryUI with the given properties
     */
    public static SummaryUI create(ObservableValue<List<Journey>> journeyList,
                                   ObservableValue<LocalTime> journeyTimes){

        ListView<Journey> journeyView = new ListView<>();
        journeyView.getStylesheets().add("summary.css");

        journeyView.getItems().addAll(journeyList.getValue());
        journeyView.setCellFactory(x->new JourneyListCell());

        updateListView(journeyView,journeyList,journeyTimes.getValue());

        journeyList.subscribe(newVal -> {
            if (newVal != null) {
                journeyView.getItems().setAll(newVal);
                updateListView(journeyView,journeyList,journeyTimes.getValue());
            }
        });

        journeyTimes.subscribe((newValue) -> {
            updateListView(journeyView,journeyList,newValue);
        });

        //Let the selectedJourney always be the journey currently selected in the listView
        ReadOnlyObjectProperty<Journey> selectedJourneyO =
                journeyView.getSelectionModel().selectedItemProperty();

        return new SummaryUI(journeyView,
                selectedJourneyO);
    }

    /**
     * updates the selection of journey to the first journey after a given LocalTime, if no journey found, select the last journey in the list
     * @param listView javaFx view of the journey that updates the interface
     * @param journeyList list of all journey of the day
     * @param journeyTimes minimum departing time of the journeys searched
     */
    private static void updateListView(ListView<Journey> listView,ObservableValue<List<Journey>> journeyList,
                                            LocalTime journeyTimes){

        if(!journeyList.getValue().isEmpty()) {
            boolean journeyFound = false;
            for (Journey journey : journeyList.getValue()) {
                if (journeyTimes.isBefore(journey.depTime().toLocalTime())) {
                    listView.getSelectionModel().select(journey);
                    listView.scrollTo(journey);
                    journeyFound = true;
                    break;
                }
            }
            if (!journeyFound) {
                listView.getSelectionModel().select(journeyList.getValue().getLast());
                listView.scrollTo(journeyList.getValue().getLast());
            }
        }
    }

    /**
     * Extends ListCell<T>, used to represent journey in a cell of a ListView.
     * @author Matthias Gibon (397827)
     */
    static final private class JourneyListCell extends ListCell<Journey> {

        static final private int CIRCLE_RADIUS = 3;
        static final private int MARGIN = 5;
        static final private int IMAGE_SIZE = 31;

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

        private JourneyListCell(){

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
            startCircle = new Circle(CIRCLE_RADIUS);
            stopCircle = new Circle(CIRCLE_RADIUS);
            startCircle.getStyleClass().add("dep-arr");
            stopCircle.getStyleClass().add("dep-arr"); // only for first and last circle
            intermediateCircleList=new ArrayList<>();

            Pane centerPane = new Pane() {
                /**
                 * calls layoutChildren and place the line and circles where we want them without
                 * being resized by the original definition of the method
                 */
                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                    setPrefSize(0, 0);

                    double lineHeight = getHeight() / 2;
                    double lineStart = MARGIN;
                    double lineEnd = getWidth() - MARGIN;
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
                            circle.setCenterX(lineStart+(lineLength * position));
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

        /**
         * calls updateItem of the ListCell and updates the intermediate stops circles and line
         * @param journey The new item for the cell.
         * @param empty whether this cell represents data from the list. If it
         *        is empty, then it does not represent any domain data, but is a cell
         *        being used to render an "empty" row.
         */
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
                transportImageView.setFitWidth(IMAGE_SIZE);
                transportImageView.setFitHeight(IMAGE_SIZE);
                routeText.setText(FormatterFr.formatRouteDestination(getFirstTransportLeg(journey)));


                //Create the circles representing the changes of the journey.
                long journeyDuration = journey.duration().toMinutes();
                for(int i=1;i<journey.legs().size();i++){ //starts at 1 to avoid the first leg
                    Journey.Leg leg=journey.legs().get(1);
                    boolean isNotLastLeg = i!=journey.legs().size()-1; //ends 1 early to avoid the last leg
                    if(leg instanceof Journey.Leg.Foot && isNotLastLeg){

                        Circle intermediateCircle = new Circle(CIRCLE_RADIUS);
                        intermediateCircle.getStyleClass().add("transfer");

                        double position = ((double) Duration.between(journey.depTime(),leg.depTime())
                                .toMinutes()) / journeyDuration; //ratio on the line where the circle will be

                        intermediateCircle.setUserData(position);
                        //stores the information in the circle for when it will be updated and placed

                        intermediateCircleList.add(intermediateCircle);
                        centerGroup.getChildren().add(intermediateCircle);
                    }
                }
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
            else{ //a foot leg is always followed by a transport leg
                return (Journey.Leg.Transport)journey.legs().get(1);
            }
        }
    }
}
