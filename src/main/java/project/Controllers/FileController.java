package project.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import project.Initialize;
import project.Models.Alerts;
import project.Models.FileExtended;
import project.Models.FileModel;
import project.Models.Language;
import project.resources.Strings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FileController implements Initializable {

    private static FileModel fileModel = new FileModel();
    static File chosenDirectory;
    public static Language chosenLanguage;
    ObservableList<FileExtended> tableFiles = FXCollections.observableArrayList();
    public static int numOfFiles;

    @FXML
    private BorderPane fileOptionsBorderPane;
    @FXML
    private SplitPane fileChooserSplitPane;
    @FXML
    private ComboBox<Language> chooseLangComboBox;
    @FXML
    private CheckBox allIncludedCheckBox;
    @FXML
    private Label directoryChosenLabel;
    @FXML
    private ProgressBar processFilesProgressBar;

    //table view for choosing files
    @FXML
    private TableView<FileExtended> filesTableView;
    @FXML
    private TableColumn<FileExtended, String> includeColumn;
    @FXML
    private TableColumn<FileExtended, String> fileNameColumn;
    @FXML
    private TableColumn<FileExtended, String> locColumn;
    @FXML
    private TableColumn<FileExtended, String> filePathColumn;

    Task<Void> extractMethodsTask;

    public void chooseDirectory() {
        //create new window for choosing the project root directory
        Window stage = new Stage();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(Strings.DIRECTORY_CHOOSER_NAME);

        //store selected root directory as File
        chosenDirectory = directoryChooser.showDialog(stage);
        if(chosenDirectory != null) {
            directoryChosenLabel.setTextFill(Color.GREEN);
            directoryChosenLabel.setText(Strings.CHOSEN_DIRECTORY + chosenDirectory.getName());
        }

    }

    public void chooseLanguage() {
        //save chosen language
        chosenLanguage = chooseLangComboBox.getValue();
    }

    public void loadFilesInTableView() throws IOException {
        numOfFiles = 0;
        if(chosenDirectory != null && chosenLanguage != null) {
            fileModel.getFileList().clear();

            //get all the files in the chosen directory, that were written in the chosen language
            Boolean filesExist = fileModel.loadFiles(chosenDirectory, chosenLanguage, allIncludedCheckBox.isSelected());

            //load files to table view
            if (filesExist) {
                //clear table
                tableFiles.clear();
                filesTableView.getItems().clear();

                //load table view with new files
                tableFiles.addAll(fileModel.getFileList());
                filesTableView.setItems(tableFiles);
            }
        }
        else {
            Alerts.getNoDirOrLangChosenAlert().showAndWait();
        }
    }

    public void proceed() {
        //filter only the selected files from the table view
        ObservableList<FileExtended> selectedTableFiles =
                FXCollections.observableList(tableFiles.stream().filter(file ->
                        file.getIncluded().isSelected()).collect(Collectors.toList())
                );

        //if(selectedTableFiles.size() >= 2) {
        if(true) {
            extractMethodsTask = new Task<>() {
                @Override
                public Void call() throws Exception {
                    double iterator = 0.0;
                    fileModel.setFinalFileList(selectedTableFiles);
                    ObservableList<FileExtended> files = fileModel.getFinalFileList();
                    for(FileExtended file : files) {
                        if(isCancelled()) {
                            updateProgress(0.0, 100.0);
                            break;
                        }
                        file.extractMethods(file, chosenLanguage);
                        iterator++;
                        updateProgress((iterator/files.size())*100.0, 100.0);
                    }
                    return null ;
                }
            };

            extractMethodsTask.setOnSucceeded(event -> {
                Initialize.switchToCloneVis();
            });

            processFilesProgressBar.progressProperty().bind(extractMethodsTask.progressProperty());
            new Thread(extractMethodsTask).start();
        }
        else {
            Alerts.getNoFilesIncludedAlert().showAndWait();
        }
    }

    public void killProceedTask() {
        extractMethodsTask.cancel();
    }

    public static FileModel getFileModel() {
        return fileModel;
    }

    public static File getChosenDirectory() {
        return chosenDirectory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //limit moving the split pane divider
        fileOptionsBorderPane.maxWidthProperty().bind(fileChooserSplitPane.widthProperty().multiply(0.2));
        fileOptionsBorderPane.minWidthProperty().bind(fileChooserSplitPane.widthProperty().multiply(0.1));

        //initialise the languages combo box

        //ArrayList<String> languagesAL = new ArrayList<>(Strings.LANGUAGES_WITH_EXT.keySet());
//        ArrayList<String> languagesAL = new ArrayList<>(Language.getNames());
//        ObservableList<String> languages = FXCollections.observableArrayList(languagesAL);
        ObservableList<Language> languages = FXCollections.observableArrayList(Language.values());
        chooseLangComboBox.setItems(languages);

        //set up the table view for files
        filesTableView.setPlaceholder(new Label("No files match the current query."));
        includeColumn.setCellValueFactory(new PropertyValueFactory<>("included"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locColumn.setCellValueFactory(new PropertyValueFactory<>("loc"));
        filePathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        //set widths of table columns
        filesTableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        includeColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 5 ); // 5% width
        fileNameColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 20 ); // 20% width
        locColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 5 ); // 5% width
        filePathColumn.setMaxWidth( 1f * Integer.MAX_VALUE * 70 ); // 70% width
    }
}
