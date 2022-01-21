package main.Controllers;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    private WebView cloneWebView;

    public void showGraph(){
        System.out.println(numOfGraphs);
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
