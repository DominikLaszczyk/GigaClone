package main.Controllers;

import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;

import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    private WebView cloneWebView;

    public void showGraph(){
        loadRadialTree();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numOfGraphs = CloneVisController.getNumOfGraphs();
        cloneWebView = CloneVisController.getCloneWebView();

        //loadRadialTree();
        URL temp = this.getClass().getResource("../CloneGraphs/CloneGraph.html");
        cloneWebView.getEngine().load(temp.toString());
    }

    private void loadRadialTree() {
        cloneWebView.getEngine().executeScript("printRadialTree()");
    }

}
