package main.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.resources.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileModel {
    private ArrayList<FileExtended> fileList = new ArrayList<>();
    private ObservableList<FileExtended> finalFileList = FXCollections.observableArrayList();


    //fill fileList with files based on given directory and language
    public Boolean loadFiles(File rootDirectory, String language, Boolean allIncluded) throws IOException {
        TreeItem<File> treeRoot = new TreeItem<>(rootDirectory);
        File[] children = rootDirectory.listFiles();

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            for(File child: children) {
                String fileName = child.getName();

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    loadFiles(child, language, allIncluded);
                }
                else {
                    String fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
                    if(Strings.LANGUAGES_WITH_EXT.get(language).equals(fileExt)) {
                        fileList.add(new FileExtended(child, allIncluded));
                    }
                }
            }
        }

        return fileList.size() != 0;
    }

    public ArrayList<FileExtended> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<FileExtended> fileList) {
        this.fileList = fileList;
    }

    public void setFinalFileList(ObservableList<FileExtended> finalFileList) {
        this.finalFileList = finalFileList;
    }

    public ObservableList<FileExtended> getFinalFileList() {
        return this.finalFileList;
    }
}
