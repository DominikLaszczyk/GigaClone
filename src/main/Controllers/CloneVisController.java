package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CloneVisController implements Initializable {

    private static int numOfGraphs = 1;
    private static WebView cloneGraphWebView;

    @FXML
    private SplitPane cloneVisSplitPane;
    private GridPane singleCloneGraphGridPane;
    private GridPane multipleCloneGraphsGridPane;


    private WebView firstCloneGraphWebView;
    private GridPane firstCloneGraphMenuGridPane;

    public void addGraph() throws IOException {

        //creating a web view for displaying clone graphs
        cloneGraphWebView = new WebView();
        GridPane.setVgrow(cloneGraphWebView, Priority.ALWAYS);

        //load graph options
        GridPane cloneGraphMenuGridPane = FXMLLoader.load(getClass().getResource("../Views/CloneGraphMenu.fxml"));

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
            multipleCloneGraphsGridPane.getColumnConstraints().clear();
            for(int i=0; i<numOfGraphs; i++) {
                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(100.0/numOfGraphs);
                multipleCloneGraphsGridPane.getColumnConstraints().add(col);
            }

            //adding new web view to the first row of new column
            multipleCloneGraphsGridPane.add(cloneGraphWebView, numOfGraphs-1, 0);
            multipleCloneGraphsGridPane.add(cloneGraphMenuGridPane, numOfGraphs-1, 1);
        }

        numOfGraphs++;
    }

    public void removeGraph() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private static void setupSingleCloneGraphGridPane(GridPane clonePane) {
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
        //2 rows, 1 col
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        ColumnConstraints col1 = new ColumnConstraints();

        row1.setPercentHeight(80.0); //15% height for clone graph
        row2.setPercentHeight(20.0); //15% height for clone menu

        clonePane.getRowConstraints().addAll(row1, row2);
        clonePane.getColumnConstraints().add(col1);
    }

    public static int getNumOfGraphs() {
        return numOfGraphs;
    }

    public static WebView getCloneGraphWebView() {
        return cloneGraphWebView;
    }
}