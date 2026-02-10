package ch.epfl.rechor.gui_debug;

import java.nio.charset.StandardCharsets;
import static java.nio.file.Files.readAllLines;
import java.nio.file.Path;
import java.util.List;

import ch.epfl.rechor.gui.QueryUI;
import ch.epfl.rechor.StopIndex;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MyQueryUITest extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> list = List.copyOf(readAllLines(Path.of("timetable").resolve("strings.txt"),
                StandardCharsets.ISO_8859_1))
                .stream()
                .filter(s -> s.chars().noneMatch(Character::isDigit))
                .filter(s -> s.chars().anyMatch(Character::isLowerCase))
                .toList();
//        System.out.println(list);
        QueryUI queryUI = QueryUI.create(new StopIndex(list,null));
        Pane root = new BorderPane(queryUI.rootNode());
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(100);
        primaryStage.show();
    }
}
