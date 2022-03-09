package main.Models.Algorithms;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import main.Controllers.FileController;
import main.Models.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TextualCloneDetection extends CloneDetection {

    ObservableList<FileExtended> files;
    Set<CloneClass> cloneClasses;
    Set<ClonePair> clonePairs;

    StringBuilder finalClonesSB;



    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();

    public TextualCloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
        this.cloneClasses = new HashSet<>();
        this.clonePairs = new HashSet<>();
    }

    @Override
    public void detectClones() throws IOException {
        this.cloneClasses.clear();
        this.clonePairs.clear();

        double iterator = 0.0;


        Set<CloneClass> tempCloneClasses = new HashSet<>();
        List<Method> allMethods = new ArrayList<>();

        for (FileExtended file : this.files) {
            allMethods.addAll(file.getMethods());
        }

        message.set("Detecting clones...");

        //loop through all methods
        for(Method currentMethod : allMethods) {

            boolean belongsToCC = false;
            //check if current method belongs to an existing clone class
            for(CloneClass currentCloneClass : tempCloneClasses) {
                //get one of the methods in the clone class
                Method methodInCC = currentCloneClass.getCloneMethod();
                if(currentMethod.equals(methodInCC)) {
                    currentCloneClass.addClone(currentMethod);
                    belongsToCC = true;
                    break;
                }
            }

            if(!belongsToCC) {
                CloneClass newCloneClass = new CloneClass(currentMethod, CloneClass.Type.ONE);
                newCloneClass.addClone(currentMethod);
                tempCloneClasses.add(newCloneClass);
            }

            iterator++;
            progress.set(iterator/allMethods.size());
        }

        for(CloneClass cc : tempCloneClasses) {
            if(cc.getClones().size()>1) {
                cc.computeHighestPath();
                this.cloneClasses.add(cc);

                System.out.println(cc);
            }
        }

        message.set("Constructing clone file...");

        finalClonesSB = new StringBuilder();
        finalClonesSB.append("data = {\n");

        String finalClones = CloneDetection.radialTreeCloneBuilder(
            FileController.getChosenDirectory(),
            finalClonesSB,
            this.cloneClasses,
            progress
        );

        finalClones += "\n};";

        try (PrintWriter out = new PrintWriter("src/main/Data/textClones.js")) {
            out.println(finalClones);
        }

        message.set("Done!");
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public ReadOnlyStringWrapper messageProperty() {
        return message;
    }



}





