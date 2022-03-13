package main.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import main.Models.Alerts;
import main.Models.Algorithms.TextualCloneDetection;
import main.Models.Algorithms.CloneDetection;
import main.Models.Algorithms.TokenCloneDetection;
import main.Models.CloneGraph;
import main.Models.FileExtended;
import main.Models.Language;

import java.io.*;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class CloneGraphMenuController implements Initializable {

    private int numOfGraphs;
    WebView cloneGraphWebView;
    private WebEngine engine;
    Task<Void> detectClonesTask;

    @FXML
    private ComboBox<CloneGraph.Type> cloneGraphTypeComboBox = new ComboBox<>();
    @FXML
    private ComboBox<CloneDetection.Algorithm> cloneDetectionAlgorithmComboBox = new ComboBox<>();
    @FXML
    private ComboBox<String> ccSizeMoreLessComboBox = new ComboBox<>();

    @FXML
    private ProgressBar cloneDetectionProgressBar;
    @FXML
    private Label cloneDetectionProgressLabel;
    @FXML
    private CheckBox displayDirNamesCheckBox;
    @FXML
    private CheckBox displayNodesCheckBox;
    @FXML
    private CheckBox displayFileNamesCheckBox;
    @FXML
    private Label ccSizeLabel;
    @FXML
    private Slider ccSizeSlider;

    @FXML
    private CheckBox cloneType1CheckBox;
    @FXML
    private CheckBox cloneType2CheckBox;
    @FXML
    private CheckBox cloneType3CheckBox;

    public void detectClones() {
        //get selected clone detection algorithm
        CloneDetection.Algorithm cloneDetectionAlgorithm = cloneDetectionAlgorithmComboBox.getValue();
        ObservableList<FileExtended> files = FileController.getFileModel().getFinalFileList();

        detectClonesTask = new Task<>() {
            @Override
            public Void call() throws Exception {
                if (cloneDetectionAlgorithm == null) {
                    Alerts.getNoCloneDetectionAlgorithmSelectedAlert().showAndWait();
                } else if (cloneDetectionAlgorithm == CloneDetection.Algorithm.TEXT) {
                    TextualCloneDetection textCloneDetection = new TextualCloneDetection(files);
                    textCloneDetection.progressProperty().addListener((obs, oldProgress, newProgress) ->
                            updateProgress(newProgress.doubleValue(), 1));
                    textCloneDetection.messageProperty().addListener((obs, oldMessage, newMessage) ->
                            updateMessage(newMessage));
                    textCloneDetection.detectClones();
                } else if (cloneDetectionAlgorithm == CloneDetection.Algorithm.TOKEN) {
                    TokenCloneDetection tokenCloneDetection = new TokenCloneDetection(files);
                    tokenCloneDetection.progressProperty().addListener((obs, oldProgress, newProgress) ->
                            updateProgress(newProgress.doubleValue(), 1));
                    tokenCloneDetection.messageProperty().addListener((obs, oldMessage, newMessage) ->
                            updateMessage(newMessage));
                    tokenCloneDetection.detectClones();
                } else if (cloneDetectionAlgorithm == CloneDetection.Algorithm.AST) {

                }

                return null;
            }
        };

        cloneDetectionProgressBar.progressProperty().bind(detectClonesTask.progressProperty());
//        detectClonesTask.messageProperty().addListener((obs, oldMessage, newMessage) ->
//                mainController.postMessage(newMessage));
        cloneDetectionProgressLabel.textProperty().bind(detectClonesTask.messageProperty());
        new Thread(detectClonesTask).start();
    }

    public void showGraph() {
        CloneGraph.Type cloneGraphType = cloneGraphTypeComboBox.getValue();
        if(cloneDetectionAlgorithmComboBox.getValue() == null) {
            Alerts.getNoCloneDetectionAlgorithmSelectedAlert().showAndWait();
        }
        else if(cloneGraphType == null) {
            Alerts.noCloneGraphTypeSelectedAlert().showAndWait();
        }
        else if(cloneGraphType == CloneGraph.Type.RADIALTREE) {
            loadRadialTree();
        }
        else if(cloneGraphType == CloneGraph.Type.HEB) {
            loadHEB();
        }
        else if(cloneGraphType == CloneGraph.Type.SCATTER) {
            loadScatter();
        }
    }



    private void loadRadialTree() {
        String data = null;
        String radialTree = null;
        try {
            if(cloneDetectionAlgorithmComboBox.getValue() == CloneDetection.Algorithm.TEXT) {
                data = Files.readString(Paths.get("src/main/Data/textClones.js"));
            }
            else if(cloneDetectionAlgorithmComboBox.getValue() == CloneDetection.Algorithm.TOKEN) {
                data = Files.readString(Paths.get("src/main/Data/tokenClones.js"));
            }

            radialTree = Files.readString(Paths.get("src/main/CloneGraphs/RadialTree.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // new page has loaded, process:
        engine.executeScript(radialTree);
        engine.executeScript(data);

        String script = "printRadialTree(" +
                displayDirNamesCheckBox.isSelected() + "," +
                displayNodesCheckBox.isSelected() + "," +
                displayFileNamesCheckBox.isSelected() + "," +
                cloneType1CheckBox.isSelected() + "," +
                cloneType2CheckBox.isSelected() + "," +
                cloneType3CheckBox.isSelected() + "," +
                "\"" + ccSizeMoreLessComboBox.getValue() + "\"" + "," +
                (int)ccSizeSlider.getValue() +
                ")";

        System.out.println(script);

        engine.executeScript(script);

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

        engine.setOnAlert((WebEvent<String> wEvent) -> {
            System.out.println("JS alert() : " + wEvent.getData() );
        });

        //initialise d3
        String d3 = null;
        try {
            d3 = Files.readString(Paths.get("lib/d3.min.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        engine.executeScript(d3);


        //initialize clone detection algorithm types in combobox
        ObservableList<CloneDetection.Algorithm> cloneDetectionAlgorithms = FXCollections.observableArrayList();
        cloneDetectionAlgorithms.addAll(CloneDetection.Algorithm.values());
        cloneDetectionAlgorithmComboBox.setItems(cloneDetectionAlgorithms);

        //initialize clone graph types in combobox
        ObservableList<CloneGraph.Type> cloneGraphTypes = FXCollections.observableArrayList();
        cloneGraphTypes.addAll(CloneGraph.Type.values());
        cloneGraphTypeComboBox.setItems(cloneGraphTypes);

        //initialise clone class size restraint combobox
        ObservableList<String> MoreLessValues = FXCollections.observableArrayList();
        MoreLessValues.addAll("More than", "Less than");
        ccSizeMoreLessComboBox.setItems(MoreLessValues);
        ccSizeMoreLessComboBox.getSelectionModel().select(0);

        ccSizeLabel.textProperty().bind(ccSizeSlider.valueProperty().asString("%.0f clones"));
    }
}
