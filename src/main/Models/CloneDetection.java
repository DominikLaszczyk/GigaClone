package main.Models;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.Controllers.FileController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    protected abstract String detectClones() throws IOException;

    protected abstract Set<CloneClass> convertPairsToClasses(Set<ClonePair> clonePairs);

    protected static String radialTreeCloneBuilder(
            File chosenDirectory,
            ObservableList<FileExtended> filesExtended,
            StringBuilder finalClones,
            Set<CloneClass> cloneClasses) throws IOException {

        TreeItem<File> treeRoot = new TreeItem<>(chosenDirectory);
        File[] children = chosenDirectory.listFiles();
        List<Path> paths = new ArrayList<>();

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            String dirName = chosenDirectory.getName().replace("'", "");
            finalClones.append("name: '").append(dirName).append("',\n");

            boolean areRelated = false;


                for(CloneClass cc : cloneClasses) {
                    for(File file : cc.getFiles()) {
                        if(
                                (file.getCanonicalPath().contains(chosenDirectory.getCanonicalPath() + File.separator)) &&
                           (chosenDirectory.getCanonicalPath().contains(cc.getHighestPath() + File.separator))
                        ){
                            areRelated = true;
                            break;
                        }
                    }

                    if(areRelated) {
                        break;
                    }
                }



            if(areRelated) {
                finalClones.append("value: '").append("1").append("',\n");
            }
            else {
                finalClones.append("value: '").append("0").append("',\n");
            }

            finalClones.append("children:\n");
            finalClones.append("[\n");
            for(File child : children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    radialTreeCloneBuilder(child, filesExtended, finalClones, cloneClasses);
                }
                else {

                    boolean isClone = false;
                    for(CloneClass cc : cloneClasses) {
                        for(Method clone : cc.getClones()) {
                            if (clone.getFile().equals(child)) {
                                isClone = true;
                                break;
                            }
                        }
                    }


                    if(isClone) {
                        String fileName = child.getName().replace("'", "");
                        finalClones.append("name: '").append(fileName).append("',\n");
                        finalClones.append("value: '").append("1").append("'\n");
                    }
                    else {
                        finalClones.append("name: '").append("").append("',\n");
                        finalClones.append("value: '").append("0").append("'\n");
                    }

                }

                finalClones.append("},");
            }
            finalClones.append("\n],\n");
        }



        return finalClones.toString();
    }

}
