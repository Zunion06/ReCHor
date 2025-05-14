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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import static java.nio.file.Files.readAllLines;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

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
        ObservableValue<Integer> depStationIdO = new ObservableValueBase<Integer>() {
            @Override
            public Integer getValue() {
                return stationId(stations, queryUI.depStopO().getValue());
            }
        };
        ObservableValue<Integer> arrStationIdO = new ObservableValueBase<Integer>() {
            @Override
            public Integer getValue() {
                return stationId(stations, queryUI.arrStopO().getValue());
            }
        };

//        ObservableValue<Object> l = Bindings.createObjectBinding(
//                () -> queryUI.depStopO().getValue(),queryUI.arrStopO().getValue(), queryUI.dateO().getValue(),queryUI.timeO().getValue()
//        );


        Router router = new Router(timeTable);
        Profile profile =
                null;
                //router.profile(queryUI.dateO().getValue(), arrStationIdO.getValue());
        List<Journey> journeys =
                null;
//                JourneyExtractor
//                .journeys(profile, depStationIdO.getValue());
        ObservableValue<List<Journey>> journeysO = new SimpleObjectProperty<>(List.of());
//        ChangeListener<String> changeListener =
//                x-> {
//                    journeysO.getValue().clear();
//                    journeysO.getValue().addAll(JourneyExtractor.journeys(profile, depStationIdO.getValue()));
//                };
        ObservableValue<LocalTime> depTimeO =
                new SimpleObjectProperty<>(queryUI.timeO().getValue());
        SummaryUI summaryUI = SummaryUI.create(journeysO, depTimeO);

        //DetailUI
        DetailUI detailUI = DetailUI.create(summaryUI.selectedJourneyO());

        SplitPane splitPane = new SplitPane(summaryUI.rootNode(),detailUI.rootNode());
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(queryUI.rootNode());
        borderPane.setCenter(splitPane);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.show();

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
