package main.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.Models.Alerts;
import main.Models.Algorithms.TextualCloneDetection;
import main.Models.CloneDetection;
import main.Models.CloneGraph;
import main.Models.FileExtended;
import main.resources.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    WebView cloneGraphWebView;
    private WebEngine engine;

    @FXML
    private ComboBox<CloneGraph.Type> cloneGraphTypeComboBox = new ComboBox<>();
    @FXML
    private ComboBox<CloneDetection.Algorithm> cloneDetectionAlgorithmComboBox = new ComboBox<>();
    @FXML
    private Button detectClonesButton;


    public void showGraph(){
        CloneGraph.Type cloneGraphType = cloneGraphTypeComboBox.getValue();
        if(cloneGraphType == CloneGraph.Type.RADIALTREE) {
            loadRadialTree();
        }
        else if(cloneGraphType == CloneGraph.Type.HEB) {
            loadHEB();
        }
        else if(cloneGraphType == CloneGraph.Type.SCATTER) {
            loadScatter();
        }
    }

    public void detectClones() throws FileNotFoundException {
        //get selected clone detection algorithm
        CloneDetection.Algorithm cloneDetectionAlgorithm = cloneDetectionAlgorithmComboBox.getValue();
        ObservableList<FileExtended> files = FileController.getFileModel().getFinalFileList();

        if(cloneDetectionAlgorithm == null) {
            Alerts.getNoCloneDetectionAlgorithmSelectedAlert().showAndWait();
        }
        else if(cloneDetectionAlgorithm == CloneDetection.Algorithm.TEXT) {
            TextualCloneDetection textCloneDetection = new TextualCloneDetection(files);
            textCloneDetection.detectClones();
        }
        else if(cloneDetectionAlgorithm == CloneDetection.Algorithm.TOKEN) {

        }
        else if(cloneDetectionAlgorithm == CloneDetection.Algorithm.AST) {

        }
    }



    private void loadRadialTree() {
        //clear cache
        //engine.load("about:blank");


        //load html to webview

        engine.reload();
        URL url2 = this.getClass().getResource(CloneGraph.getIndexPage());

        engine.load(url2.toString());

        //run radial tree script after the html file is loaded
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // new page has loaded, process:
                engine.executeScript("printRadialTree()");
            }
        });
    }


    private void loadHEB() {

    }

    private void loadScatter() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numOfGraphs = CloneVisController.getNumOfGraphs();
        cloneGraphWebView = CloneVisController.getCloneGraphWebView();
        engine = cloneGraphWebView.getEngine();
        URL url2 = this.getClass().getResource(CloneGraph.getIndexPage());

        engine.load(url2.toString());


        //initialize clone detection algorithm types in combobox
        ObservableList<CloneDetection.Algorithm> cloneDetectionAlgorithms = FXCollections.observableArrayList();
        cloneDetectionAlgorithms.addAll(CloneDetection.Algorithm.values());
        cloneDetectionAlgorithmComboBox.setItems(cloneDetectionAlgorithms);

        //initialize clone graph types in combobox
        ObservableList<CloneGraph.Type> cloneGraphTypes = FXCollections.observableArrayList();
        cloneGraphTypes.addAll(CloneGraph.Type.values());
        cloneGraphTypeComboBox.setItems(cloneGraphTypes);
    }
}
