package project.Models.Algorithms;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import project.Controllers.FileController;
import project.Models.CloneClass;
import project.Models.ClonePair;
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

    private int maxFileHierarchySize = 0;

    protected ObservableList<FileExtended> files;
    protected Set<CloneClass> cloneClasses;
    protected StringBuilder finalClonesSB;

    protected final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    protected final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();

    public abstract void detectClones() throws IOException;

    public CloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
        this.cloneClasses = new HashSet<>();
        this.maxFileHierarchySize = 0;
    }

    public void calculateClonePercentage() {
        Set<Method> methodsType1 = new HashSet<>();
        Set<Method> methodsType2 = new HashSet<>();
        Set<Method> methodsType3 = new HashSet<>();
        Set<Method> allMethods = new HashSet<>();
        Set<FileExtended> filesWithClones = new HashSet<>();

        //calculate number of methods for every clone type
        for(CloneClass cc : cloneClasses) {
            switch(cc.getType()) {
                case ONE:
                    methodsType1.addAll(cc.getClones());
                    break;
                case TWO:
                    methodsType2.addAll(cc.getClones());
                    break;
                case THREE:
                    methodsType3.addAll(cc.getClones());
                    break;
            }

            for(Method clone : cc.getClones()) {
                filesWithClones.add(clone.getFile());
            }
        }

        //calculate number of methods in the whole system
        for(FileExtended file : files) {
            allMethods.addAll(file.getMethods());
        }

        System.out.println("Type 1 %: " + (double)methodsType1.size()/(double)allMethods.size()*100.0);
        System.out.println("Type 2 %: " + (double)methodsType2.size()/(double)allMethods.size()*100.0);
        System.out.println("Type 3 %: " + (double)methodsType3.size()/(double)allMethods.size()*100.0);
        System.out.println("Files with clones %: " + (double)filesWithClones.size()/(double)files.size()*100.0);
    }

    protected ArrayList<String> getTokenSymbolicNameList(Lexer lexer) {
        ArrayList<String> tokens = new ArrayList<>();
        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            String symbolicName;
            symbolicName = lexer.getVocabulary().getSymbolicName(token.getType());
            tokens.add(symbolicName);
        }
        return tokens;
    }

    protected Double calculateJaccardScore(Method method1, Method method2) {
        int startIndex1 = method1.getParserRuleContext().start.getStartIndex();
        int endIndex1 = method1.getParserRuleContext().stop.getStartIndex();
        int startIndex2 = method2.getParserRuleContext().start.getStartIndex();
        int endIndex2 = method2.getParserRuleContext().stop.getStartIndex();

        Interval interval1 = new Interval(startIndex1, endIndex1);
        Interval interval2 = new Interval(startIndex2, endIndex2);

        String methodContent1 = method1.getCharStream().getText(interval1);
        String methodContent2 = method2.getCharStream().getText(interval2);

        String[] locMethod1Array = methodContent1.split("\n");
        String[] locMethod2Array = methodContent2.split("\n");

        Set<String> locMethod1 = new HashSet<>(Arrays.asList(locMethod1Array));
        Set<String> locMethod2 = new HashSet<>(Arrays.asList(locMethod2Array));

        locMethod1.removeIf(String::isEmpty);
        locMethod2.removeIf(String::isEmpty);

        Set<String> finalLocMethod1 = new HashSet<>();
        Set<String> finalLocMethod2 = new HashSet<>();
        for(String loc : locMethod1)
            finalLocMethod1.add(loc.replaceAll("\\s+",""));

        for(String loc : locMethod2)
            finalLocMethod2.add(loc.replaceAll("\\s+",""));


        Set<String> intersection = new HashSet<>(finalLocMethod1);
        intersection.retainAll(finalLocMethod2);


        return (double)intersection.size()/(double)Math.max(finalLocMethod1.size(), finalLocMethod2.size());
    }

    protected String arrayCloneBuilder(Set<CloneClass> cloneClasses) {

        StringBuilder finalClones = new StringBuilder("data = [");
        StringBuilder labels = new StringBuilder("labels = [");
        Integer id = 0;
        HashMap<Integer, File> cloneFilesIds = new HashMap<>();
        HashMap<File, Integer> cloneFilesIdsRev = new HashMap<>();
        ArrayList<ClonePair> clonePairs = new ArrayList<>();

        for(CloneClass cc : cloneClasses) {
            for(Method clone : cc.getClones()) {
                if(!cloneFilesIds.containsValue(clone.getFile())) {
                    cloneFilesIds.put(id, clone.getFile());
                    cloneFilesIdsRev.put(clone.getFile(), id);
                    id++;
                }
            }
        }

        for(int k = 0; k<id; k++) {
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
                        ClonePair cp = new ClonePair(
                                k,
                                cloneFilesIdsRev.get(clone.getFile()),
                                currentCloneFile.getName(),
                                clone.getFile().getName(),
                                Integer.parseInt(cc.getType().toString()),
                                clonesInCurrentCC.size()
                        );

                        clonePairs.add(cp);
                    }
                }
            }
        }

        ArrayList<Integer> clonePairsToDelete = new ArrayList<>();
        for(int i=0; i<clonePairs.size()-1; i++) {
            for(int j=i+1; j<clonePairs.size(); j++) {
                if(clonePairs.get(i).sameFiles(clonePairs.get(j))) {
                    clonePairs.get(i).setSize(clonePairs.get(i).getSize() + clonePairs.get(j).getSize());
                    Integer clonePairIndex = j;
                    clonePairsToDelete.add(clonePairIndex);
                }
            }
        }

        for(int i=clonePairs.size()-1; i>=0; i--) {
            if(clonePairsToDelete.contains(i)) {
                clonePairs.remove(i);
            }
        }

        int maxSize = 0;
        for(ClonePair cp : clonePairs) {
            finalClones.append(cp.toJSON()).append(",\n");
            if(cp.getSize()>maxSize) maxSize = cp.getSize();
        }


        if(finalClones.charAt(finalClones.length()-1) == ',') { finalClones.setLength(finalClones.length() - 1); }
        finalClones.append("]");

        if(labels.charAt(labels.length()-1) == ',') { labels.setLength(labels.length() - 1); }
        labels.append("]");

        String maxSizeString = "maxSize = " + maxSize + "\n";

        String highestFileIndex = "highestFileIndex = " + id + "\n";

        return highestFileIndex + maxSizeString + labels + "\n" + finalClones;
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
                    radialTreeCloneBuilder(child, finalClones, cloneClasses);
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
                    System.out.println(this.fileCounter + "/" + FileController.numOfFiles);
                }

                finalClones.append("},");
            }
            finalClones.append("\n]\n");
        }

        String maxSize = "maxSize = " + maxFileHierarchySize;

        return finalClones.toString() + "\n};" + maxSize;
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public ReadOnlyStringWrapper messageProperty() {
        return message;
    }


}
