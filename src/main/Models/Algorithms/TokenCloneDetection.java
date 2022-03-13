package main.Models.Algorithms;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import main.ANTLR.Java.Java8Lexer;
import main.Controllers.FileController;
import main.Models.CloneClass;
import main.Models.FileExtended;
import main.Models.Language;
import main.Models.Method;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenCloneDetection extends CloneDetection {

    public TokenCloneDetection(ObservableList<FileExtended> files) {
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

            boolean belongsToCCType2 = false;
            //check if current method belongs to an existing clone class
            for(CloneClass currentCloneClass : tempCloneClasses) {
                //get one of the methods in the clone class
                Method methodInCC = currentCloneClass.getCloneMethod();

                //type 2 check
                CharStream charStream1 = CharStreams.fromString(currentMethod.toString());
                CharStream charStream2 = CharStreams.fromString(methodInCC.toString());
                if(FileController.chosenLanguage == Language.JAVA) {
                    Java8Lexer java8Lexer1 = new Java8Lexer(charStream1);
                    Java8Lexer java8Lexer2 = new Java8Lexer(charStream2);

                    ArrayList<String> tokenSymbolicNames1 = getTokenSymbolicNameList(java8Lexer1);
                    ArrayList<String> tokenSymbolicNames2 = getTokenSymbolicNameList(java8Lexer2);

                    if(tokenSymbolicNames1.equals(tokenSymbolicNames2)) {
                        if(!currentCloneClass.hasType()) {
                            currentCloneClass.setType(CloneClass.Type.TWO);
                        }
                        currentCloneClass.addClone(currentMethod);
                        belongsToCCType2 = true;
                    }
                }
            }

            if(!belongsToCCType2) {
                CloneClass newCloneClass = new CloneClass(currentMethod);
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

        message.set("Constructing clone file...");

        finalClonesSB = new StringBuilder("data = {\n");

        String finalClones = this.radialTreeCloneBuilder(
                FileController.getChosenDirectory(),
                finalClonesSB,
                this.cloneClasses
        );

        finalClones += "\n};";

        try (PrintWriter out = new PrintWriter("src/main/Data/tokenClones.js")) {
            out.println(finalClones);
        }

        message.set("Done!");
    }


    private ArrayList<String> getTokenSymbolicNameList(Lexer lexer) {
        ArrayList<String> tokens = new ArrayList<>();
        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            String symbolicName = "";
            symbolicName = lexer.getVocabulary().getSymbolicName(token.getType());
            tokens.add(symbolicName);
        }
        return tokens;
    }
}
