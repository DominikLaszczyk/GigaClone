package project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.resources.Strings;

import java.io.File;
import java.net.URL;

public class Initialize extends Application {

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

//        String css = this.getClass().getResource("resources/stylesheets/main.css").toExternalForm();
//        mainScene.getStylesheets().add(css);
        //mainScene.getStylesheets().add(getClass().getResource("/resources/stylesheets/main.css").toExternalForm());

        URL url1 = new File("src/main/java/project/Views/FileChooser.fxml").toURI().toURL();
        URL url2 = new File("src/main/java/project/Views/CloneVis.fxml").toURI().toURL();
        fileChooserAnchorPane = FXMLLoader.load(url1);
        cloneVisAnchorPane = FXMLLoader.load(url2);

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
