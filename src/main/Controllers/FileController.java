package main.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.Main;
import main.Models.Alerts;
import main.Models.FileExtended;
import main.Models.FileModel;
import main.resources.Strings;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FileController implements Initializable {

    FileModel fileModel = new FileModel();
    File chosenDirectory;
    String chosenLanguage;
    ObservableList<FileExtended> tableFiles = FXCollections.observableArrayList();

    @FXML
    private BorderPane fileOptionsBorderPane;
    @FXML
    private SplitPane fileChooserSplitPane;
    @FXML
    private ComboBox<String> chooseLangComboBox;
    @FXML
    private CheckBox allIncludedCheckBox;
    @FXML
    private Label directoryChosenLabel;

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

        if(selectedTableFiles.size() >= 2) {
            Main.switchToCloneVis();
        }
        else {
            Alerts.getNoFilesIncludedAlert().showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //limit moving the split pane divider
        fileOptionsBorderPane.maxWidthProperty().bind(fileChooserSplitPane.widthProperty().multiply(0.2));
        fileOptionsBorderPane.minWidthProperty().bind(fileChooserSplitPane.widthProperty().multiply(0.1));

        //initialise the languages combo box
        ArrayList<String> languagesAL = new ArrayList<>(Strings.LANGUAGES_WITH_EXT.keySet());
        ObservableList<String> languages = FXCollections.observableArrayList(languagesAL);
        chooseLangComboBox.setItems(languages);

        //set up the table view for files
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
