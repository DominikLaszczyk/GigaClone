package project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import project.Models.*;

import project.Models.Algorithms.ParseTreeCloneDetection;
import project.Models.Algorithms.TextualCloneDetection;
import project.Models.Algorithms.CloneDetection;
import project.Models.Algorithms.TokenCloneDetection;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import java.awt.image.BufferedImage;
import java.util.Set;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

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
    private CheckBox colourCodeCloneSizeCheckBox;
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

    public void killDetectClonesTask() {
        detectClonesTask.cancel();
    }

    public void detectClones() {
        //get selected clone detection algorithm
        CloneDetection.Algorithm cloneDetectionAlgorithm = cloneDetectionAlgorithmComboBox.getValue();
        ObservableList<FileExtended> files = FileController.getFileModel().getFinalFileList();

        detectClonesTask = new Task<>() {
            @Override
            public Void call() throws Exception {
                if (cloneDetectionAlgorithm == null) {
                    Alerts.getNoCloneDetectionAlgorithmSelectedAlert().showAndWait();
                }
                else {
                    CloneDetection cloneDetection = null;

                    if (cloneDetectionAlgorithm == CloneDetection.Algorithm.TEXT) {
                        cloneDetection = new TextualCloneDetection(files);

                    } else if (cloneDetectionAlgorithm == CloneDetection.Algorithm.TOKEN) {
                        cloneDetection = new TokenCloneDetection(files);

                    } else if (cloneDetectionAlgorithm == CloneDetection.Algorithm.PARSE_TREE) {
                        cloneDetection = new ParseTreeCloneDetection(files);
                    }

                    assert cloneDetection != null;
                    cloneDetection.progressProperty().addListener((obs, oldProgress, newProgress) ->
                            updateProgress(newProgress.doubleValue(), 1));
                    cloneDetection.messageProperty().addListener((obs, oldMessage, newMessage) ->
                            updateMessage(newMessage));

                    Set<CloneClass> cloneClasses = cloneDetection.detectClones();
                    cloneDetection.calculateClonePercentage();
                    ArrayCloneBuilder arrayCloneBuilder = new ArrayCloneBuilder(cloneClasses);
                    HierarchyCloneBuilder hierarchyCloneBuilder = new HierarchyCloneBuilder(cloneClasses);

                    cloneDetection.setMessage("Constructing clone file 1...");
                    String hierarchyClones = hierarchyCloneBuilder.buildClones(cloneClasses);
                    try (PrintWriter out = new PrintWriter("src/main/java/project/Data/tokenClonesHierarchy.js")) {
                        out.println(hierarchyClones);
                    }

                    cloneDetection.setMessage("Constructing clone file 2...");
                    String arrayClones = arrayCloneBuilder.buildClones(cloneClasses);
                    try (PrintWriter out = new PrintWriter("src/main/java/project/Data/tokenClonesArray.js")) {
                        out.println(arrayClones);
                    }



                    cloneDetection.setMessage("Done!");

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
        CloneDetection.Algorithm cloneDetectionAlgorithm = cloneDetectionAlgorithmComboBox.getValue();
        if(cloneDetectionAlgorithm == null) {
            Alerts.getNoCloneDetectionAlgorithmSelectedAlert().showAndWait();
        }
        else if(cloneGraphType == null) {
            Alerts.noCloneGraphTypeSelectedAlert().showAndWait();
        }
        else {
            String script = "";
            String cloneGraphAlgorithm = null;
            String data = null;
            try {
                switch(cloneGraphType) {
                    case RADIALTREE:
                        cloneGraphAlgorithm = Files.readString(Paths.get("src/main/java/project/CloneGraphs/RadialTree.js"));
                        script += "printRadialTree(";
                        switch(cloneDetectionAlgorithm) {
                            case TEXT:
                                data = Files.readString(Paths.get("src/main/java/project/Data/textClonesHierarchy.js"));
                                break;
                            case TOKEN:
                                data = Files.readString(Paths.get("src/main/java/project/Data/tokenClonesHierarchy.js"));
                                break;
                        }
                        break;
                    case SCATTERPLOT:
                        cloneGraphAlgorithm = Files.readString(Paths.get("src/main/java/project/CloneGraphs/ScatterPlot.js"));
                        script += "printScatterPlot(";
                        switch(cloneDetectionAlgorithm) {
                            case TEXT:
                                data = Files.readString(Paths.get("src/main/java/project/Data/textClonesArray.js"));
                                break;
                            case TOKEN:
                                data = Files.readString(Paths.get("src/main/java/project/Data/tokenClonesArray.js"));
                                break;
                        }
                        break;
                    case TREEMAP:
                        cloneGraphAlgorithm = Files.readString(Paths.get("src/main/java/project/CloneGraphs/TreeMap.js"));
                        script += "printTreeMap(";
                        switch(cloneDetectionAlgorithm) {
                            case TEXT:
                                data = Files.readString(Paths.get("src/main/java/project/Data/textClonesHierarchy.js"));
                                break;
                            case TOKEN:
                                data = Files.readString(Paths.get("src/main/java/project/Data/tokenClonesHierarchy.js"));
                                break;
                        }
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            engine.executeScript(cloneGraphAlgorithm);
            engine.executeScript(data);

            script +=
                    colourCodeCloneSizeCheckBox.isSelected() + "," +
                    displayDirNamesCheckBox.isSelected() + "," +
                    displayNodesCheckBox.isSelected() + "," +
                    displayFileNamesCheckBox.isSelected() + "," +
                    cloneType1CheckBox.isSelected() + "," +
                    cloneType2CheckBox.isSelected() + "," +
                    cloneType3CheckBox.isSelected() + "," +
                    "\"" + ccSizeMoreLessComboBox.getValue() + "\"" + "," +
                    (int)ccSizeSlider.getValue() +
                    ")";

            engine.executeScript(script);


        }
    }

    public void saveCloneGraphAsPng() {
        File destFile = new File("clone_graph.png");
        WritableImage image = cloneGraphWebView.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numOfGraphs = CloneVisController.getNumOfGraphs();
        cloneGraphWebView = CloneVisController.getCloneGraphWebView();
        engine = cloneGraphWebView.getEngine();

        URL url3 = null;
        try {
            url3 = new File("src/main/java/project/CloneGraphs/index.html").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert url3 != null;
        engine.load(url3.toString());

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
