package main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import main.resources.Strings;

public class Main extends Application {

    private static final int MIN_WINDOW_WIDTH = 1100;
    private static final int MIN_WINDOW_HEIGHT = 800;

    private static final int PREF_WINDOW_WIDTH = 1200;
    private static final int PREF_WINDOW_HEIGHT = 800;

    //root pane
    private static final BorderPane root = new BorderPane();

    @FXML
    private static AnchorPane fileChooserAnchorPane;
    @FXML
    private static AnchorPane cloneVisAnchorPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Scene mainScene = new Scene(root);

        //linking style sheets
        mainScene.getStylesheets().add(getClass().getResource("resources/stylesheets/main.css").toExternalForm());

        fileChooserAnchorPane = FXMLLoader.load(getClass().getResource("Views/FileChooser.fxml"));
        cloneVisAnchorPane = FXMLLoader.load(getClass().getResource("Views/CloneVis.fxml"));

        root.setCenter(fileChooserAnchorPane);

        primaryStage.setTitle(Strings.PROGRAM_NAME);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);
        primaryStage.setWidth(PREF_WINDOW_WIDTH);
        primaryStage.setHeight(PREF_WINDOW_HEIGHT);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void switchToFileChooser() {
        root.setCenter(fileChooserAnchorPane);
    }

    public static void switchToCloneVis() {
        root.setCenter(cloneVisAnchorPane);
    }
}
