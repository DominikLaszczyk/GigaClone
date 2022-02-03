package main.Models;

import org.antlr.v4.runtime.ParserRuleContext;

public class Method extends ParserRuleContext {
    private ParserRuleContext parserRuleContext;

    public Method(ParserRuleContext parserRuleContext) {
        this.parserRuleContext = parserRuleContext;
    }

    @Override
    public String toString() {
        return this.parserRuleContext.getText();
    }

    @Override
    public boolean equals(Object object) {
//        System.out.println("-----------------------------");
//        System.out.println(this.parserRuleContext.getText().getClass());
//        System.out.println(((ParserRuleContext)object).getText());
//        System.out.println(object.getClass());
//        System.out.println("-----------------------------");
        return this.parserRuleContext.getText().equals(((Method)object).getParserRuleContext().getText());
    }

    @Override
    public int hashCode() {
        //System.out.println("hashcode");
        return this.parserRuleContext.getText().hashCode();
    }

    public ParserRuleContext getParserRuleContext() {
        return this.parserRuleContext;
    }
}
