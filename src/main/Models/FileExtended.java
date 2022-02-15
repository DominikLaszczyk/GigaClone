package main.Models;


import javafx.scene.control.CheckBox;
import main.ANTLR.Java.Java8Lexer;
import main.ANTLR.Java.Java8Parser;
import main.ANTLR.Java.Java8ParserBaseListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileExtended extends File{
    private String name;
    private final long loc;
    private String path;
    private CheckBox included;
    private ArrayList<Method> methods = new ArrayList<>();

    public FileExtended(File file, Boolean included) throws IOException {
        super(file.getPath());

        this.name = file.getName();
        this.loc = Files.lines(Paths.get(file.getPath()), StandardCharsets.ISO_8859_1).count();
        this.path = file.getPath();
        this.included = new CheckBox();
        this.included.setSelected(included);
    }


    public void extractMethods(FileExtended file) throws IOException {
        String content = com.google.common.io.Files.asCharSource(file, StandardCharsets.ISO_8859_1).read();
        CharStream charStream = CharStreams.fromString(content);
        Java8Lexer java8Lexer = new Java8Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(java8Lexer);

        Java8Parser parser = new Java8Parser(commonTokenStream);
        Java8Parser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit

        JavaListener extractor = new JavaListener(file);
        ParseTreeWalker.DEFAULT.walk(extractor, tree);

        this.methods = extractor.methods;
    }

    static class JavaListener extends Java8ParserBaseListener {

        private final ArrayList<Method> methods = new ArrayList<>();
        private final FileExtended file;

        public JavaListener(FileExtended file ) {
            super();

            this.file = file;
        }

        @Override
        public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
            super.enterMethodDeclaration(ctx);

            if(ctx.start != null) {
                this.methods.add(new Method(ctx, this.file));
            }
        }
    }

    public long getLoc() {
        return this.loc;
    }

    public CheckBox getIncluded() {
        return this.included;
    }

    public ArrayList<Method> getMethods() {
        return this.methods;
    }

}
