package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CloneVisController implements Initializable {

    private static int numOfGraphs = 0;
    private static WebView cloneWebView;

    @FXML
    private GridPane cloneGraphsGridPane;

    public void addGraph() throws IOException {
        numOfGraphs++;
        //creating a web view for displaying clone graphs
        cloneWebView = new WebView();

        //load graph options
        GridPane cloneGraphMenuGridPane = FXMLLoader.load(getClass().getResource("../Views/CloneGraphMenu.fxml"));

        //setting width of every column to be the same
        cloneGraphsGridPane.getColumnConstraints().clear();
        for(int i=0; i<numOfGraphs; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0/numOfGraphs);
            cloneGraphsGridPane.getColumnConstraints().add(col);
        }

        //adding new web view to the first row of new column
        cloneGraphsGridPane.add(cloneWebView, numOfGraphs-1, 0);
        cloneGraphsGridPane.add(cloneGraphMenuGridPane, numOfGraphs-1, 1);
    }

    public void removeGraph() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public static int getNumOfGraphs() {
        return numOfGraphs;
    }

    public static WebView getCloneWebView() {
        return cloneWebView;
    }
}
