package ch.epfl.rechor.gui;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

import static java.nio.file.Files.readAllLines;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static ObservableValue<Profile> profileO;
    private static ObservableValue<List<Journey>> journeysO ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //QueryUI
        Path timetablePath = Path.of("timetable");
        List<String> list = List.copyOf(readAllLines(timetablePath.resolve("strings.txt"),
                        StandardCharsets.ISO_8859_1))
                .stream()
                .filter(s -> s.chars().noneMatch(Character::isDigit))
                .filter(s -> s.chars().anyMatch(Character::isLowerCase))
                .toList();
        QueryUI queryUI = QueryUI.create(new StopIndex(list,null));

        //SummaryUI
        TimeTable timeTable = new CachedTimeTable(
                FileTimeTable.in(timetablePath));
        Stations stations = timeTable.stations();

        Router router = new Router(timeTable);

        queryUI.arrStopO().addListener(
                ((observable, oldValue, newValue) -> System.out.println("arr : "+oldValue+" -> "+newValue))
        );
        profileO = Bindings.createObjectBinding(
                () -> {
                    System.out.println("profileO");
                    if (queryUI.arrStopO().getValue().isEmpty()) {
                        System.out.println("profile is nuull confirmed");
                        return null;
                    }
                    else {
                        System.out.println("profile0 changed");
                        return router.profile(queryUI.dateO().getValue(), stationId(stations, queryUI.arrStopO().getValue()));
                    }
                },
                queryUI.dateO(),queryUI.arrStopO()
        );
        journeysO = Bindings.createObjectBinding(
                () -> {
                    System.out.println("journeyO");
                    if (!queryUI.depStopO().getValue().isEmpty() || profileO.getValue()!=null){
                        System.out.println("profile is null : "+profileO.getValue()==null);
                        return JourneyExtractor.journeys(profileO.getValue(), stationId(stations, queryUI.depStopO().getValue()));
                    }
                    else
                        return List.of();
                },
                queryUI.depStopO(),profileO
        );

        SummaryUI summaryUI = SummaryUI.create(journeysO, queryUI.timeO());


        //DetailUI
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        SplitPane splitPane = new SplitPane(summaryUI.rootNode(),detailUI.rootNode());
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(queryUI.rootNode());
        borderPane.setCenter(splitPane);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("ReCHor");
        primaryStage.show();

        Platform.runLater(() ->
                scene.lookup("#depStop").requestFocus());

    }

    static int stationId(Stations stations, String stationName) {
        for (int i = 0; i < stations.size(); i++) {
            if (stations.name(i).equals(stationName)) {
                return i;
            }
        }
        return -1;
    }
}
