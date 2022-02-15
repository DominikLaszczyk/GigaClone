package main.Models;

import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;

public class Method extends ParserRuleContext {
    private ParserRuleContext parserRuleContext;
    private FileExtended file;

    public Method(ParserRuleContext parserRuleContext, FileExtended file) {
        this.parserRuleContext = parserRuleContext;
        this.file = file;
    }

    @Override
    public String toString() {
        return this.parserRuleContext.getText();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Method && this.parserRuleContext.getText().equals(((Method)object).getParserRuleContext().getText());
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

    public void setFile(FileExtended file) {
        this.file = file;
    }
}
