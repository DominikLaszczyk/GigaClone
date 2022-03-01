package main.Models;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.Controllers.FileController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
            StringBuilder finalClones,
            Set<CloneClass> cloneClasses) throws IOException {

        TreeItem<File> treeRoot = new TreeItem<>(chosenDirectory);
        File[] children = chosenDirectory.listFiles();

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            String dirName = chosenDirectory.getName().replace("'", "");
            finalClones.append("name: '").append(dirName).append("',\n");

            boolean areRelated = false;
            boolean isRootCloneDirectory = false;
            CloneClass.Type dirCloneType = null;
            Integer ccSize = null;
            for(CloneClass cc : cloneClasses) {
                ccSize = cc.getClones().size();

                //check if current directory is the root clone directory
                if(chosenDirectory.getCanonicalPath().equals(cc.getHighestPath())) {
                    isRootCloneDirectory = true;
                    dirCloneType = cc.getType();
                    break;
                }

                for(Method clone : cc.getClones()) {
                    //check if current directory is part of clone directory
                    if((clone.getFile().getCanonicalPath().contains(chosenDirectory.getCanonicalPath() + File.separator)) &&
                       (chosenDirectory.getCanonicalPath().contains(cc.getHighestPath() + File.separator))){
                        areRelated = true;
                        dirCloneType = cc.getType();
                        break;
                    }


                }

                if(areRelated) { break; }
            }

            if(areRelated) {
                finalClones.append("type: '").append(dirCloneType).append("',\n");
                finalClones.append("size: '").append(ccSize).append("',\n");
                finalClones.append("isClone: '").append("1").append("',\n");
            }


            if(isRootCloneDirectory) {
                finalClones.append("type: '").append(dirCloneType).append("',\n");
                finalClones.append("size: '").append(ccSize).append("',\n");
                finalClones.append("isRootCloneDir: '").append("1").append("',\n");
            }


            finalClones.append("children:\n");
            finalClones.append("[\n");
            for(File child : children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    radialTreeCloneBuilder(child, finalClones, cloneClasses);
                }
                else {

                    boolean isClone = false;
                    CloneClass.Type cloneType = null;
                    Integer fileCcSize = null;
                    for(CloneClass cc : cloneClasses) {
                        for(Method clone : cc.getClones()) {
                            if (clone.getFile().equals(child)) {
                                isClone = true;
                                cloneType = cc.getType();
                                fileCcSize = cc.getClones().size();
                                break;
                            }
                        }
                    }


                    if(isClone) {
                        String fileName = child.getName().replace("'", "");
                        finalClones.append("name: '").append(fileName).append("',\n");
                        finalClones.append("type: '").append(cloneType).append("',\n");
                        finalClones.append("size: '").append(fileCcSize).append("',\n");
                        finalClones.append("isFile: '").append("1").append("',\n");
                        finalClones.append("isClone: '").append("1").append("'\n");
                    }
                    else {
                        finalClones.append("name: '").append("").append("',\n");
                        finalClones.append("isFile: '").append("0").append("',\n");
                        finalClones.append("isClone: '").append("0").append("'\n");
                    }

                }

                finalClones.append("},");
            }
            finalClones.append("\n],\n");
        }



        return finalClones.toString();
    }

}
