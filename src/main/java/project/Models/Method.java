package project.Models;

import org.antlr.v4.runtime.CharStream;
import project.Models.Algorithms.ParseTreeCloneDetection;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class Method extends ParserRuleContext {
    private ParserRuleContext parserRuleContext;
    private FileExtended file;
    private List<ParseTreeCloneDetection.ParseTreeExtended> subTrees;
    private int numOfParseTreeNodes;
    private CharStream charStream;

    public Method(ParserRuleContext parserRuleContext, FileExtended file, CharStream charStream) {
        this.parserRuleContext = parserRuleContext;
        this.file = file;
        this.charStream = charStream;
    }

    @Override
    public String toString() {
        return this.parserRuleContext.getText();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Method &&
                this.parserRuleContext.getText().equals(((Method)object).getParserRuleContext().getText()) &&
                this.file == ((Method) object).file;
    }

    @Override
    public int hashCode() {
        //System.out.println("hashcode");
        return this.parserRuleContext.getText().hashCode();
    }

    public ParserRuleContext getParserRuleContext() {
        return this.parserRuleContext;
    }

    public FileExtended getFile() {
        return this.file;
    }

    public List<ParseTreeCloneDetection.ParseTreeExtended> getSubTrees() {
        return this.subTrees;
    }

    public void setFile(FileExtended file) {
        this.file = file;
    }

    public void setSubTrees(List<ParseTreeCloneDetection.ParseTreeExtended> subTrees) {
        this.subTrees = subTrees;
        numOfParseTreeNodes = 0;
        calculateNumOfNodes(this.parserRuleContext);
    }

    private void calculateNumOfNodes(ParseTree treeRoot) {
        for(int i=0; i<treeRoot.getChildCount(); i++) {
            if(treeRoot.getChild(i).getChildCount() > 0) {
                calculateNumOfNodes(treeRoot.getChild(i));
            }
            else {
                this.numOfParseTreeNodes++;
            }
        }
    }

    public int getNumOfParseTreeNodes() {
        return this.numOfParseTreeNodes;
    }

    public CharStream getCharStream() {
        return this.charStream;
    }
}
