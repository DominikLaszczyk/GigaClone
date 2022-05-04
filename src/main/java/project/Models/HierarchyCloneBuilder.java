package project.Models;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.control.TreeItem;
import project.Controllers.FileController;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class HierarchyCloneBuilder extends JSONCloneBuilder {

    File chosenDirectory;
    StringBuilder finalClones;
    private int maxFileHierarchySize = 0;
    public Double fileCounter = 0.0;

    protected final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();

    public HierarchyCloneBuilder(Set<CloneClass> cloneClasses) {
        super(cloneClasses);

        chosenDirectory = FileController.getChosenDirectory();
        finalClones = new StringBuilder("data = {\n");
    }

    @Override
    public String buildClones(Set<CloneClass> cloneClasses) throws IOException {

        TreeItem<File> treeRoot = new TreeItem<>(chosenDirectory);
        File[] children = chosenDirectory.listFiles();

        //loop over all the nodes in the tree starting from given root
        if(children != null) {
            String dirName = chosenDirectory.getName().replace("'", "");

            finalClones.append("fullDirName: '").append(chosenDirectory.getCanonicalPath().replace("\\", "\\\\")).append("',\n");
            finalClones.append("name: '").append(dirName).append("',\n");

            boolean areRelated = false;
            boolean isRootCloneDirectory = false;
            StringBuilder ccSizes = new StringBuilder("[");
            int sizesSum = 0;
            StringBuilder dirCloneTypes = new StringBuilder("[");
            for(CloneClass cc : cloneClasses) {
                //check if current directory is the root clone directory
                if(chosenDirectory.getCanonicalPath().equals(cc.getHighestPath())) {
                    isRootCloneDirectory = true;
                    dirCloneTypes.append(cc.getType()).append(",");
                    ccSizes.append(cc.getClones().size()).append(",");
                    sizesSum += cc.getClones().size();
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
                finalClones.append("sizesSum: ").append(sizesSum).append(",\n");
            }

            if(areRelated) { finalClones.append("isClone: '").append("1").append("',\n"); }
            if(isRootCloneDirectory) { finalClones.append("isRootCloneDir: '").append("1").append("',\n"); }

            finalClones.append("children:\n");
            finalClones.append("[\n");

            if(sizesSum > maxFileHierarchySize) maxFileHierarchySize = sizesSum;

            for(File child : children) {

                finalClones.append("{");

                TreeItem<File> newNodeFile = new TreeItem<>(child);
                treeRoot.getChildren().add(newNodeFile);

                if(child.isDirectory()) {
                    chosenDirectory = child;
                    buildClones(cloneClasses);
                }
                else if(child.getName().contains(FileController.chosenLanguage.getExtension())){

                    boolean isClone = false;
                    StringBuilder fileCcSizes = new StringBuilder("[");
                    int fileSizesSum = 0;
                    StringBuilder fileCloneTypes= new StringBuilder("[");
                    for(CloneClass cc : cloneClasses) {
                        for(Method clone : cc.getClones()) {
                            if (clone.getFile().equals(child)) {
                                isClone = true;
                                fileCloneTypes.append(cc.getType()).append(",");
                                fileCcSizes.append(cc.getClones().size()).append(",");
                                fileSizesSum += cc.getClones().size();
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
                        finalClones.append("sizesSum: ").append(fileSizesSum).append(",\n");
                        finalClones.append("isFile: '").append("1").append("',\n");
                        finalClones.append("isClone: '").append("1").append("'\n");
                    }
                    else {
                        finalClones.append("name: '").append("").append("',\n");
                        finalClones.append("isFile: '").append("0").append("',\n");
                        finalClones.append("isClone: '").append("0").append("'\n");
                    }

                    if(sizesSum > maxFileHierarchySize) maxFileHierarchySize = sizesSum;
                }

                if(!child.isDirectory()) {
                    this.fileCounter++;
                    progress.set(this.fileCounter/FileController.numOfFiles);
                    //System.out.println(this.fileCounter + "/" + FileController.numOfFiles);
                }

                finalClones.append("},");
            }
            finalClones.append("\n]\n");
        }

        String maxSize = "maxSize = " + maxFileHierarchySize;

        return finalClones.toString() + "\n};" + maxSize;
    }
}
