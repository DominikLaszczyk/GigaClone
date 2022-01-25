package main.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import main.Models.CloneGraph;
import main.resources.Strings;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    private WebEngine engine;

    @FXML
    private ComboBox<CloneGraph.Type> cloneGraphTypeComboBox = new ComboBox<>();

    public void showGraph(){
        if(cloneGraphTypeComboBox.getValue() == CloneGraph.Type.RADIALTREE) {
            loadRadialTree();
        }
        else if(cloneGraphTypeComboBox.getValue() == CloneGraph.Type.HEB) {
            loadHEB();
        }
        else if(cloneGraphTypeComboBox.getValue() == CloneGraph.Type.SCATTER) {
            loadScatter();
        }
    }



    private void loadRadialTree() {
        //run radial tree script after the html file is loaded
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // new page has loaded, process:
                engine.executeScript("printRadialTree()");
            }
        });

        //load html to webview
        URL url = this.getClass().getResource("../CloneGraphs/index.html");
        engine.load(url.toString());
    }

    private void loadHEB() {

    }

    private void loadScatter() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numOfGraphs = CloneVisController.getNumOfGraphs();
        WebView cloneGraphWebView = CloneVisController.getCloneGraphWebView();
        engine = cloneGraphWebView.getEngine();

        //initialize clone graph types in combobox
        ObservableList<CloneGraph.Type> cloneGraphTypes = FXCollections.observableArrayList();
        System.out.println(CloneGraph.Type.RADIALTREE.getName());
        cloneGraphTypes.add(CloneGraph.Type.RADIALTREE);
        cloneGraphTypes.add(CloneGraph.Type.HEB);
        cloneGraphTypes.add(CloneGraph.Type.SCATTER);
        cloneGraphTypeComboBox.setItems(cloneGraphTypes);
    }
}
