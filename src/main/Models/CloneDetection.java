package main.Models;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.Controllers.FileController;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CloneDetection {
    public enum Algorithm {
        TEXT("Textual Clone Detection"),
        TOKEN("Token-based Clone Detection"),
        AST("AST-based Clone Detection");

        private final String name;

        Algorithm(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return  name;
        }
    }

    protected abstract void detectClones() throws FileNotFoundException;

    protected abstract Set<CloneClass> convertPairsToClasses(Set<ClonePair> clonePairs);

    protected static String radialTreeCloneBuilder(File chosenDirectory, ObservableList<FileExtended> filesExtended, StringBuilder finalClones) {
        TreeItem<File> treeRoot = new TreeItem<>(chosenDirectory);
        File[] children = chosenDirectory.listFiles();
        List<String> paths = new ArrayList<>();

        for(FileExtended fileExtended : filesExtended) {
            paths.add(fileExtended.getPath());
        }

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            String dirName = chosenDirectory.getName().replace("'", "");
            finalClones.append("name: '").append("").append("',\n");
            finalClones.append("children: [\n");
            for(File child: children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    radialTreeCloneBuilder(child, filesExtended, finalClones);
                }
                //else if (paths.contains(child.getPath())){
                else {
                    String fileName = child.getName().replace("'", "");
                    finalClones.append("name: '").append("").append("'\n");
                }
                //}

                finalClones.append("},");
            }
            finalClones.append("\n],\n");
        }



        return finalClones.toString();
    }

}
