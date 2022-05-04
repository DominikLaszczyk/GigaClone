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
import java.text.DecimalFormat;
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



    protected ObservableList<FileExtended> files;
    protected Set<CloneClass> cloneClasses;
    protected StringBuilder finalClonesSB;

    protected final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper();
    protected final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();

    public abstract Set<CloneClass> detectClones() throws IOException;

    public CloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
        this.cloneClasses = new HashSet<>();
    }

    public void calculateClonePercentage() {
        Set<Method> methodsType1 = new HashSet<>();
        Set<Method> methodsType2 = new HashSet<>();
        Set<Method> methodsType3 = new HashSet<>();
        Set<Method> cloneMethods = new HashSet<>();
        Set<FileExtended> filesWithClones = new HashSet<>();
        int allMethodsCount = 0;
        long totalMethodLOC = 0;
        long clonedMethodLOC = 0;

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
            cloneMethods.addAll(cc.getClones());

            for(Method clone : cc.getClones()) {
                filesWithClones.add(clone.getFile());
            }
        }

        //calculate number of methods in the whole system
        for(FileExtended file : files) {
            allMethodsCount += file.getMethods().size();

            for(Method method : file.getMethods()) {
                totalMethodLOC += method.getLOC();
            }
        }

        for(Method clone : cloneMethods) {
            clonedMethodLOC += clone.getLOC();
        }

        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println("===================================");
        System.out.println("Total copied LOC %: " + df.format((double)clonedMethodLOC/(double)totalMethodLOC*100.0));
        System.out.println("-----------------------------------");
        System.out.println("All type methods %: " + df.format((double)cloneMethods.size()/(double)allMethodsCount*100.0));
        System.out.println("Type 1 methods %: " + df.format((double)methodsType1.size()/(double)allMethodsCount*100.0));
        System.out.println("Type 2 methods %: " + df.format((double)methodsType2.size()/(double)allMethodsCount*100.0));
        System.out.println("Type 3 methods %: " + df.format((double)methodsType3.size()/(double)allMethodsCount*100.0));
        System.out.println("Files with clones %: " + df.format((double)filesWithClones.size()/(double)files.size()*100.0));
        System.out.println("===================================");
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





    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public ReadOnlyStringWrapper messageProperty() {
        return message;
    }

    public void setMessage(String newMessage) {
        this.message.set(newMessage);
    }


}
