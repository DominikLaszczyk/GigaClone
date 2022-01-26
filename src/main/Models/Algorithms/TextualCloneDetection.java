package main.Models.Algorithms;

import javafx.collections.ObservableList;
import main.Models.CloneDetection;
import main.Models.FileExtended;

import java.util.ArrayList;

public class TextualCloneDetection extends CloneDetection {

    ObservableList<FileExtended> files;

    public TextualCloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
    }

    @Override
    protected void detectClones() {

    }
}
