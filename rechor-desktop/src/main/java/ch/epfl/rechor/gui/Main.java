package ch.epfl.rechor.gui;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * the class to launch the interface of ReCHor
 * @author Guillaume Pommier (401065)
 */
public final class Main extends Application {

    private static final String TITLE = "ReCHor";
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    /**
     * Launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private ObservableValue<Profile> profileO;
    private ObservableValue<List<Journey>> journeysO ;

    /**
     * Sets up and shows the main JavaFX UI for the route planner application.
     * Loads timetable data, initializes station and alias information, sets up
     * the queryUI, summaryUI, and detailUI. Binds UI components
     * so that journey results update automatically based on user input.
     *
     * @param primaryStage the main application window
     * @throws Exception if an error occurs during UI setup or data loading
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //QueryUI
        String dataPath = System.getProperty("RESOURCES_PATH");
        Path timetablePath = Path.of(dataPath+"/timetable");
        TimeTable timeTable = new CachedTimeTable(
                FileTimeTable.in(timetablePath));
        Stations stations = timeTable.stations();
        StationAliases stationAliases = timeTable.stationAliases();

        List<String> nameList = IntStream.range(0,stations.size())
                .mapToObj(stations::name)
                .toList();
        Map<String,String> alternativeName = new HashMap<>();
        for (int i = 0; i < stationAliases.size(); i++) {
            alternativeName.put(stationAliases.alias(i),stationAliases.stationName(i));
        }
        QueryUI queryUI = QueryUI.create(new StopIndex(nameList,alternativeName));

        //SummaryUI
        Router router = new Router(timeTable);

        profileO = Bindings.createObjectBinding(
                () -> {
                    if (queryUI.arrStopO().getValue().isEmpty())
                        return null;
                    return router.profile(queryUI.dateO().getValue(), nameList.indexOf(queryUI.arrStopO().getValue()));
                },
                queryUI.dateO(),queryUI.arrStopO()
        );
        journeysO = Bindings.createObjectBinding(
                () -> {
                    Profile profile = profileO.getValue();
                    if (!queryUI.depStopO().getValue().isEmpty() && profile!=null)
                        return JourneyExtractor.journeys(profile, nameList.indexOf(queryUI.depStopO().getValue()));
                    return List.of();
                },
                queryUI.depStopO(),profileO
        );

        SummaryUI summaryUI = SummaryUI.create(journeysO, queryUI.timeO());


        //DetailUI
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        //The Interface
        SplitPane splitPane = new SplitPane(summaryUI.rootNode(),detailUI.rootNode());
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(queryUI.rootNode());
        borderPane.setCenter(splitPane);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.show();

        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());
    }
}
