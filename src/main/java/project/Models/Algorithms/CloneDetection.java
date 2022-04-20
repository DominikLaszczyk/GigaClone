package project.Models.Algorithms;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import project.Controllers.FileController;
import project.Models.CloneClass;
import project.Models.FileExtended;
import project.Models.Method;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class CloneDetection {
    public enum Algorithm {
        TEXT("Textual Clone Detection"),
        TOKEN("Token-based Clone Detection"),
        PARSE_TREE("Parse tree-based Clone Detection");

        private final String name;

        Algorithm(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return  name;
        }
    }

    public Double fileCounter = 0.0;

    protected ObservableList<FileExtended> files;
    protected Set<CloneClass> cloneClasses;
    protected StringBuilder finalClonesSB;

    protected final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    protected final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();

    public abstract void detectClones() throws IOException;

    public CloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
        this.cloneClasses = new HashSet<>();
    }

    protected String arrayCloneBuilder(Set<CloneClass> cloneClasses) {

        StringBuilder finalClones = new StringBuilder("data = [");
        StringBuilder labels = new StringBuilder("labels = [");
        Integer id = 0;
        HashMap<Integer, File> cloneFilesIds = new HashMap<>();
        HashMap<File, Integer> cloneFilesIdsRev = new HashMap<>();

        for(CloneClass cc : cloneClasses) {
            for(Method clone : cc.getClones()) {
                if(!cloneFilesIds.containsValue(clone.getFile())) {
                    cloneFilesIds.put(id, clone.getFile());
                    cloneFilesIdsRev.put(clone.getFile(), id);
                    id++;
                }
            }
        }

        for(Integer k=0; k<id; k++) {
            File currentCloneFile = cloneFilesIds.get(k);

            labels
                .append("\"")
                .append(currentCloneFile.getName())
                .append("\",");

            for(CloneClass cc : cloneClasses) {
                List<Method> clonesInCurrentCC = cc.getClones();

                boolean isInCC = false;
                for(Method clone : clonesInCurrentCC) {
                    if(clone.getFile().equals(currentCloneFile)) {
                        isInCC = true;
                        break;
                    }
                }

                if(isInCC) {
                    for(Method clone : clonesInCurrentCC) {
                        finalClones
                                .append("{")
                                .append("fileIndex1: ").append(k).append(",")
                                .append("fileIndex2: ").append(cloneFilesIdsRev.get(clone.getFile())).append(",")
                                .append("fileName1: '").append(currentCloneFile.getName()).append("',")
                                .append("fileName2: '").append(clone.getFile().getName()).append("',")
                                .append("type: ").append(cc.getType()).append(", ")
                                .append("size: ").append(clonesInCurrentCC.size())
                                .append("},\n");
                    }
                }
            }
        }

        if(finalClones.charAt(finalClones.length()-1) == ',') { finalClones.setLength(finalClones.length() - 1); }
        finalClones.append("]");

        if(labels.charAt(labels.length()-1) == ',') { labels.setLength(labels.length() - 1); }
        labels.append("]");

        return labels + "\n" + finalClones;
    }

    protected String radialTreeCloneBuilder(
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
            StringBuilder ccSizes = new StringBuilder("[");
            StringBuilder dirCloneTypes = new StringBuilder("[");
            for(CloneClass cc : cloneClasses) {
                //check if current directory is the root clone directory
                if(chosenDirectory.getCanonicalPath().equals(cc.getHighestPath())) {
                    isRootCloneDirectory = true;
                    dirCloneTypes.append(cc.getType()).append(",");
                    ccSizes.append(cc.getClones().size()).append(",");
                }

                for(Method clone : cc.getClones()) {
                    //check if current directory is part of clone directory
                    if((clone.getFile().getCanonicalPath().contains(chosenDirectory.getCanonicalPath() + File.separator)) &&
                       (chosenDirectory.getCanonicalPath().contains(cc.getHighestPath() + File.separator))){
                        areRelated = true;
                        dirCloneTypes.append(cc.getType()).append(",");
                        ccSizes.append(cc.getClones().size()).append(",");
                    }
                }
            }
            if(ccSizes.charAt(ccSizes.length()-1) == ',') { ccSizes.setLength(ccSizes.length() - 1); }
            ccSizes.append("]");

            if(dirCloneTypes.charAt(dirCloneTypes.length()-1) == ',') { dirCloneTypes.setLength(dirCloneTypes.length() - 1); }
            dirCloneTypes.append("]");

            if(areRelated || isRootCloneDirectory) {
                finalClones.append("types: ").append(dirCloneTypes).append(",\n");
                finalClones.append("sizes: ").append(ccSizes).append(",\n");
            }

            if(areRelated) { finalClones.append("isClone: '").append("1").append("',\n"); }
            if(isRootCloneDirectory) { finalClones.append("isRootCloneDir: '").append("1").append("',\n"); }

            finalClones.append("children:\n");
            finalClones.append("[\n");
            for(File child : children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    radialTreeCloneBuilder(child, finalClones, cloneClasses);
                }
                else if(child.getName().contains(FileController.chosenLanguage.getExtension())){

                    boolean isClone = false;
                    StringBuilder fileCcSizes = new StringBuilder("[");
                    StringBuilder fileCloneTypes= new StringBuilder("[");
                    for(CloneClass cc : cloneClasses) {
                        for(Method clone : cc.getClones()) {
                            if (clone.getFile().equals(child)) {
                                isClone = true;
                                fileCloneTypes.append(cc.getType()).append(",");
                                fileCcSizes.append(cc.getClones().size()).append(",");
                            }
                        }
                    }
                    if(fileCcSizes.charAt(fileCcSizes.length()-1) == ',') { fileCcSizes.setLength(fileCcSizes.length() - 1); }
                    fileCcSizes.append("]");
                    if(fileCloneTypes.charAt(fileCloneTypes.length()-1) == ',') { fileCloneTypes.setLength(fileCloneTypes.length() - 1); }
                    fileCloneTypes.append("]");


                    if(isClone) {
                        String fileName = child.getName().replace("'", "");
                        finalClones.append("name: '").append(fileName).append("',\n");
                        finalClones.append("types: ").append(fileCloneTypes).append(",\n");
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
                    this.fileCounter++;
                    progress.set(this.fileCounter/FileController.numOfFiles);
                    System.out.println(this.fileCounter + "/" + FileController.numOfFiles);
                }

                finalClones.append("},");
            }
            finalClones.append("\n],\n");
        }



        return finalClones.toString();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public ReadOnlyStringWrapper messageProperty() {
        return message;
    }


}
