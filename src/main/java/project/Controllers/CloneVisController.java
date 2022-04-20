package project.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import project.Initialize;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CloneVisController implements Initializable {
    private static int numOfGraphs = 0;
    private static WebView cloneGraphWebView;

    @FXML
    private SplitPane cloneVisSplitPane;
    private GridPane singleCloneGraphGridPane;
    private GridPane multipleCloneGraphsGridPane;

    private WebView firstCloneGraphWebView;
    private GridPane firstCloneGraphMenuGridPane;

    public void addGraph() throws IOException {
        numOfGraphs++;
        //creating a web view for displaying clone graphs
        cloneGraphWebView = new WebView();
        GridPane.setVgrow(cloneGraphWebView, Priority.ALWAYS);

        //load graph options
        URL url = new File("src/main/java/project/Views/CloneGraphMenu.fxml").toURI().toURL();
        GridPane cloneGraphMenuGridPane = FXMLLoader.load(url);
        GridPane.setVgrow(cloneGraphMenuGridPane, Priority.SOMETIMES);

        if(numOfGraphs == 1) {
            //save first clone graph and clone menu
            firstCloneGraphWebView = cloneGraphWebView;
            firstCloneGraphMenuGridPane = cloneGraphMenuGridPane;

            singleCloneGraphGridPane = new GridPane();

            //setup single graph grid pane
            setupSingleCloneGraphGridPane(singleCloneGraphGridPane);

            //add the first clone graph and graph menu to first row
            singleCloneGraphGridPane.add(cloneGraphWebView, 1, 0);
            singleCloneGraphGridPane.add(cloneGraphMenuGridPane, 0, 0);

            //add single graph grid pane
            cloneVisSplitPane.getItems().add(singleCloneGraphGridPane);
        }
        else {
            if(numOfGraphs == 2) {
                //remove single graph grid pane
                cloneVisSplitPane.getItems().remove(1);

                multipleCloneGraphsGridPane = new GridPane();

                //setup multi graph grid pane
                setupMultipleCloneGraphsGridPane(multipleCloneGraphsGridPane);

                //add the first clone graph and graph menu to first column
                multipleCloneGraphsGridPane.add(firstCloneGraphWebView, 0, 0);
                multipleCloneGraphsGridPane.add(firstCloneGraphMenuGridPane, 0, 1);

                //add multi graph grid pane
                cloneVisSplitPane.getItems().add(multipleCloneGraphsGridPane);
            }

            //setting width of every column to be the same
            distributeCloneGraphWidth(multipleCloneGraphsGridPane, numOfGraphs);

            //adding new web view to the first row of new column
            multipleCloneGraphsGridPane.add(cloneGraphWebView, numOfGraphs-1, 0);
            multipleCloneGraphsGridPane.add(cloneGraphMenuGridPane, numOfGraphs-1, 1);
        }
    }

    public void removeGraph() {
        if(numOfGraphs == 1) {
            //remove single clone graph grid pane
            cloneVisSplitPane.getItems().remove(1);
        }
        else if(numOfGraphs == 2) {
            //remove multiple graphs grid pane
            cloneVisSplitPane.getItems().remove(1);

            //setup single graph grid pane
            setupSingleCloneGraphGridPane(singleCloneGraphGridPane);

            //add the first clone graph and graph menu to first row
            singleCloneGraphGridPane.add(firstCloneGraphWebView, 1, 0);
            singleCloneGraphGridPane.add(firstCloneGraphMenuGridPane, 0, 0);

            //add single graph grid pane
            cloneVisSplitPane.getItems().add(singleCloneGraphGridPane);
        }
        else {
            //remove last graph grid pane
            multipleCloneGraphsGridPane.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == numOfGraphs-1);
            distributeCloneGraphWidth(multipleCloneGraphsGridPane,numOfGraphs-1);
        }

        numOfGraphs--;
    }

    private static void setupSingleCloneGraphGridPane(GridPane clonePane) {
        clonePane.getRowConstraints().clear();
        clonePane.getColumnConstraints().clear();

        //1 row, 2 cols
        RowConstraints row1 = new RowConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();

        col1.setPercentWidth(15.0); //15% width for clone menu
        col2.setPercentWidth(85.0); //85% width for clone graph

        clonePane.getRowConstraints().add(row1);
        clonePane.getColumnConstraints().addAll(col1, col2);
    }

    private static void setupMultipleCloneGraphsGridPane(GridPane clonePane) {
        clonePane.getRowConstraints().clear();
        clonePane.getColumnConstraints().clear();

        clonePane.setMinHeight(10);

        //2 rows, 1 col
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        ColumnConstraints col1 = new ColumnConstraints();

        row2.setMinHeight(row2.getPrefHeight());

        row1.setPercentHeight(70.0); //80% height for clone graph
        row2.setPercentHeight(30.0); //20% height for clone menu

        clonePane.getRowConstraints().addAll(row1, row2);
        clonePane.getColumnConstraints().add(col1);
    }

    private static void distributeCloneGraphWidth(GridPane clonePane, int numOfCells) {
        clonePane.getColumnConstraints().clear();
        for(int i=0; i<numOfCells; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0/numOfCells);
            clonePane.getColumnConstraints().add(col);
        }
    }

    public void goBack() {
        Initialize.switchToFileChooser();
    }

    public static int getNumOfGraphs() {
        return numOfGraphs;
    }

    public static WebView getCloneGraphWebView() {
        return cloneGraphWebView;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
