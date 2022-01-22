package main.Controllers;

import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;

import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    private WebView cloneGraphWebView;

    public void showGraph(){
        loadRadialTree();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numOfGraphs = CloneVisController.getNumOfGraphs();
        cloneGraphWebView = CloneVisController.getCloneGraphWebView();

        //loadRadialTree();
        URL temp = this.getClass().getResource("../CloneGraphs/CloneGraph.html");
        cloneGraphWebView.getEngine().load(temp.toString());
    }

    private void loadRadialTree() {
        cloneGraphWebView.getEngine().executeScript("printRadialTree()");
    }

}
