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
    public String detectClones() throws FileNotFoundException {
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

//        finalClonesSB = new StringBuilder("<!DOCTYPE html>\n" +
//                "\n" +
//                "<html lang=\"\">\n" +
//                "    <head>\n" +
//                "        <link rel=\"stylesheet\" href=\"../resources/stylesheets/main.css\">\n" +
//                "        <script src=\"https://d3js.org/d3.v6.min.js\"></script>\n" +
//                "        <script src=\"RadialTree.js\"></script>\n" +
//                "        <title></title>\n" +
//                "    </head>\n" +
//                "\n" +
//               // "    <body onload=\"printRadialTree();\">\n" +
//                "    <body>\n" +
//                "\n" +
//                "    <main class=\"content\">\n" +
//                "        <svg id=\"chart\" width=\"750\" height=\"750\" viewBox=\"0 0 750 750\"\n" +
//                "             preserveAspectRatio=\"xMidYMid meet\"></svg>\n" +
//                "    </main>\n" +
//                "\n" +
//                "    <script>\n");

        finalClonesSB = new StringBuilder();

        finalClonesSB.append("data = {\n");

        String finalClones = CloneDetection.radialTreeCloneBuilder(
            FileController.getChosenDirectory(),
            FileController.getFileModel().getFinalFileList(),
                finalClonesSB
        );

        finalClones += "\n};";

//        finalClones +=
//                "    </script>\n" +
//                "\n" +
//                "\n" +
//                "    </body>\n" +
//                "</html>";

        try (PrintWriter out = new PrintWriter("src/main/Data/textClones.js")) {
            out.println(finalClones);
        }

        return finalClones;
    }

    @Override
    protected Set<CloneClass> convertPairsToClasses(Set<ClonePair> clonePairs) {
        return null;
    }


}





