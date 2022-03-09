package main.Models.Algorithms;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.Controllers.FileController;
import main.Models.CloneClass;
import main.Models.ClonePair;
import main.Models.Method;

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

    public static Double fileCounter = 0.0;

    protected abstract void detectClones() throws IOException;

    protected static String radialTreeCloneBuilder(
            File chosenDirectory,
            StringBuilder finalClones,
            Set<CloneClass> cloneClasses,
            ReadOnlyDoubleWrapper progress) throws IOException {

        TreeItem<File> treeRoot = new TreeItem<>(chosenDirectory);
        File[] children = chosenDirectory.listFiles();

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            String dirName = chosenDirectory.getName().replace("'", "");
            finalClones.append("name: '").append(dirName).append("',\n");

            boolean areRelated = false;
            boolean isRootCloneDirectory = false;
            CloneClass.Type dirCloneType = null;
            StringBuilder ccSizes = new StringBuilder("[");
            for(CloneClass cc : cloneClasses) {
                //check if current directory is the root clone directory
                if(chosenDirectory.getCanonicalPath().equals(cc.getHighestPath())) {
                    isRootCloneDirectory = true;
                    dirCloneType = cc.getType();
                    ccSizes.append(cc.getClones().size()).append(",");
                }

                for(Method clone : cc.getClones()) {
                    //check if current directory is part of clone directory
                    if((clone.getFile().getCanonicalPath().contains(chosenDirectory.getCanonicalPath() + File.separator)) &&
                       (chosenDirectory.getCanonicalPath().contains(cc.getHighestPath() + File.separator))){
                        areRelated = true;
                        dirCloneType = cc.getType();
                        ccSizes.append(cc.getClones().size()).append(",");
                    }
                }
            }
            if(ccSizes.charAt(ccSizes.length()-1) == ',') {
                ccSizes.setLength(ccSizes.length() - 1);
            }
            ccSizes.append("]");

            if(areRelated || isRootCloneDirectory) {
                finalClones.append("type: '").append(dirCloneType).append("',\n");
                finalClones.append("sizes: ").append(ccSizes).append(",\n");
            }

            if(areRelated) {
                finalClones.append("isClone: '").append("1").append("',\n");
            }

            if(isRootCloneDirectory) {
                finalClones.append("isRootCloneDir: '").append("1").append("',\n");
            }


            finalClones.append("children:\n");
            finalClones.append("[\n");
            for(File child : children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    radialTreeCloneBuilder(child, finalClones, cloneClasses, progress);
                }
                else if(child.getName().contains(FileController.chosenLanguage.getExtension())){

                    boolean isClone = false;
                    CloneClass.Type cloneType = null;
                    StringBuilder fileCcSizes = new StringBuilder("[");
                    for(CloneClass cc : cloneClasses) {
                        for(Method clone : cc.getClones()) {
                            if (clone.getFile().equals(child)) {
                                isClone = true;
                                cloneType = cc.getType();
                                fileCcSizes.append(cc.getClones().size()).append(",");
                            }
                        }
                    }
                    if(fileCcSizes.charAt(fileCcSizes.length()-1) == ',') {
                        fileCcSizes.setLength(fileCcSizes.length() - 1);
                    }
                    fileCcSizes.append("]");


                    if(isClone) {
                        String fileName = child.getName().replace("'", "");
                        finalClones.append("name: '").append(fileName).append("',\n");
                        finalClones.append("type: '").append(cloneType).append("',\n");
                        finalClones.append("sizes: ").append(fileCcSizes).append(",\n");
                        finalClones.append("isFile: '").append("1").append("',\n");
                        finalClones.append("isClone: '").append("1").append("'\n");
                    }
                    else {
                        finalClones.append("name: '").append("").append("',\n");
                        finalClones.append("isFile: '").append("0").append("',\n");
                        finalClones.append("isClone: '").append("0").append("'\n");
                    }
                }

                if(!child.isDirectory()) {
                    fileCounter++;
                    progress.set(fileCounter/FileController.numOfFiles);
                    System.out.println(fileCounter + "/" + FileController.numOfFiles);
                }

                finalClones.append("},");
            }
            finalClones.append("\n],\n");
        }



        return finalClones.toString();
    }

}
