package project.Models.Algorithms;

import javafx.collections.ObservableList;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import project.ANTLR.Java.Java8Lexer;
import project.Controllers.FileController;
import project.Models.CloneClass;
import project.Models.FileExtended;
import project.Models.Language;
import project.Models.Method;

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

                Double jScore = calculateJaccardScore(currentMethod, methodInCC);

                if(jScore > 0.7) {
                    currentCloneClass.addClone(currentMethod);
                    belongsToCC = true;
                    break;
                }
            }

            if(!belongsToCC) {
                CloneClass newCloneClass = new CloneClass(currentMethod, CloneClass.Type.THREE);
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

        for(CloneClass cc : this.cloneClasses) {
            boolean isType1 = true;
            List<Method> clones = cc.getClones();
            for(int i=0; i<clones.size()-1; i++) {
                for(int j=i+1; j<clones.size(); j++) {
                    if(!clones.get(i).equals(clones.get(j))) {
                        isType1 = false;
                        break;
                    }
                }
            }

            if(isType1) { cc.setType(CloneClass.Type.ONE); }
        }

        message.set("Constructing clone file 1...");

        finalClonesSB = new StringBuilder("data = {\n");

        String hierarchyClones = this.radialTreeCloneBuilder(
            FileController.getChosenDirectory(),
            finalClonesSB,
            this.cloneClasses
        );

        try (PrintWriter out = new PrintWriter("src/main/java/project/Data/textClonesHierarchy.js")) {
            out.println(hierarchyClones);
        }

        message.set("Constructing clone file 2...");

        String arrayClones = this.arrayCloneBuilder(this.cloneClasses);

        try (PrintWriter out = new PrintWriter("src/main/java/project/Data/textClonesArray.js")) {
            out.println(arrayClones);
        }

        message.set("Done!");
    }


}





