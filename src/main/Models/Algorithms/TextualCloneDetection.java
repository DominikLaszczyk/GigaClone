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

    public TextualCloneDetection(ObservableList<FileExtended> files) {
        super(files);
    }

    @Override
    public void detectClones() throws IOException {
        this.cloneClasses.clear();

        double progressIterator = 0.0;

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

            progressIterator++;
            progress.set(progressIterator/allMethods.size());
        }

        for(CloneClass cc : tempCloneClasses) {
            if(cc.getClones().size()>1) {
                cc.computeHighestPath();
                this.cloneClasses.add(cc);
            }
        }

        message.set("Constructing clone file 1...");

        finalClonesSB = new StringBuilder("data = {\n");

        String hierarchyClones = this.radialTreeCloneBuilder(
            FileController.getChosenDirectory(),
            finalClonesSB,
            this.cloneClasses
        );

        hierarchyClones += "\n};";

        try (PrintWriter out = new PrintWriter("src/main/Data/textClonesHierarchy.js")) {
            out.println(hierarchyClones);
        }

        message.set("Constructing clone file 2...");

        String arrayClones = this.arrayCloneBuilder(this.cloneClasses);

        try (PrintWriter out = new PrintWriter("src/main/Data/textClonesArray.js")) {
            out.println(arrayClones);
        }

        message.set("Done!");
    }
}





