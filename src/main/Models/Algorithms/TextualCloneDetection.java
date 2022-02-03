package main.Models.Algorithms;

import javafx.collections.ObservableList;
import main.ANTLR.Java.Java8Parser;
import main.Models.CloneDetection;
import main.Models.FileExtended;
import main.Models.Method;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class TextualCloneDetection extends CloneDetection {

    ObservableList<FileExtended> files;

    public TextualCloneDetection(ObservableList<FileExtended> files) {
        this.files = files;
    }

    @Override
    public void detectClones() {
        for(int i=0; i<this.files.size(); i++) {
            for(int j=i+1; j<this.files.size()-1; j++) {

                Set<Method> methods1 = new HashSet<>(this.files.get(i).getMethods());
                Set<Method> methods2 = new HashSet<>(this.files.get(j).getMethods());

                methods1.retainAll( methods2 );

                if(methods1.size()>0) {
                    System.out.println(methods1);
                }

            }
        }

    }

    private void test() {
        System.out.println("test");
    }
}





