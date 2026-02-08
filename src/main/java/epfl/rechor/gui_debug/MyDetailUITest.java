package epfl.rechor.gui_debug;

import epfl.rechor.gui.DetailUI;
import epfl.rechor.journey.Journey;
import epfl.rechor.journey.JourneyExtractor;
import epfl.rechor.journey.Profile;
import epfl.rechor.journey.Router;
import epfl.rechor.timetable.CachedTimeTable;
import epfl.rechor.timetable.Stations;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

public final class MyDetailUITest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(stationName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TimeTable timeTable = new CachedTimeTable(
                FileTimeTable.in(Path.of("timetable")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.MAY, 15);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);

        ObservableValue<Journey> journeyO =
                new SimpleObjectProperty<>(journey);
        ObservableValue<Journey> emptyJourney = new SimpleObjectProperty<>(null);
        DetailUI detailUI = DetailUI.create(emptyJourney);
        Pane root = new BorderPane(detailUI.rootNode());

        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
}