package main.Models.Algorithms;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import main.ANTLR.Java.Java8Parser;
import main.Controllers.FileController;
import main.Models.*;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class TextualCloneDetection extends CloneDetection {

    ObservableList<FileExtended> files;
    Set<CloneClass> cloneClasses;
    Set<ClonePair> clonePairs;

    StringBuilder finalClonesSB;

    public TextualCloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
        this.cloneClasses = new HashSet<>();
        this.clonePairs = new HashSet<>();
    }

    @Override
    public void detectClones() throws FileNotFoundException {
        this.cloneClasses.clear();
        this.clonePairs.clear();

        Set<CloneClass> tempCloneClasses = new HashSet<>();
        List<Method> allMethods = new ArrayList<>();

        for (FileExtended file : this.files) {
            allMethods.addAll(file.getMethods());
        }

        //loop through all methods
        for(Method currentMethod : allMethods) {

            boolean belongsToCC = false;
            //check if current method belongs to an existing clone class
            for(CloneClass currentCloneClass : tempCloneClasses) {
                //get one of the methods in the clone class
                Method methodInCC = currentCloneClass.getCloneMethod();
                if(currentMethod.equals(methodInCC)) {
                    //System.out.println("BOI");
                    currentCloneClass.addClone(currentMethod);
                    belongsToCC = true;
                    break;
                }
            }

            if(!belongsToCC) {
                CloneClass newCloneClass = new CloneClass(currentMethod);
                newCloneClass.addClone(currentMethod);
                tempCloneClasses.add(newCloneClass);
            }
        }

        for(CloneClass cc : tempCloneClasses) {
            if(cc.getClones().size()>1) {
                this.cloneClasses.add(cc);
            }
        }

//        StringBuilder finalClone = new StringBuilder("var data = {name: 'flare',\n" +
//                "    children: [\n");
//
//        int cloneClassCounter = 1;
//
//        for(CloneClass cc : this.cloneClasses) {
//            finalClone.append("{name: '").append(cloneClassCounter).append("',children: [");
//            for(Method clone : cc.getClones()) {
//                //{ name: 'AgglomerativeCluster', value: 3938 },
//                finalClone.append("{ name: '")
//                        .append(clone.getFile().getName())
//                        .append("'},");
//
//                System.out.println(clone.getFile().getName());
//            }
//            finalClone.append("],},");
//            cloneClassCounter++;
//        }
//
//        finalClone.append("],};");
//
//        try (PrintWriter out = new PrintWriter("textClones.js")) {
//            out.println(finalClone);
//        }

        finalClonesSB = new StringBuilder("var data = {\n");

        String finalClones = CloneDetection.radialTreeCloneBuilder(
            FileController.getChosenDirectory(),
            FileController.getFileModel().getFinalFileList(),
                finalClonesSB
        );

        finalClones += "\n};";

        try (PrintWriter out = new PrintWriter("src/main/Data/textClones.js")) {
            out.println(finalClones);
        }
    }

    @Override
    protected Set<CloneClass> convertPairsToClasses(Set<ClonePair> clonePairs) {
        return null;
    }


}





