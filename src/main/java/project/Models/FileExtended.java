package project.Models;


import javafx.scene.control.CheckBox;
import project.ANTLR.Cpp.CPP14Lexer;
import project.ANTLR.Cpp.CPP14Parser;
import project.ANTLR.Cpp.CPP14ParserBaseListener;
import project.ANTLR.Java.Java8Lexer;
import project.ANTLR.Java.Java8Parser;
import project.ANTLR.Java.Java8ParserBaseListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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


    public void extractMethods(FileExtended file, Language language) throws IOException {
        String content = com.google.common.io.Files.asCharSource(file, StandardCharsets.ISO_8859_1).read();
        CharStream charStream = CharStreams.fromString(content);

        if(language.equals(Language.JAVA)) {
            //System.out.println("JAVA");
            Java8Lexer java8Lexer = new Java8Lexer(charStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(java8Lexer);

            Java8Parser parser = new Java8Parser(commonTokenStream);
            Java8Parser.CompilationUnitContext tree = parser.compilationUnit(); // parse a compilationUnit
            JavaListener extractor = new JavaListener(file, charStream);

            ParseTreeWalker.DEFAULT.walk(extractor, tree);

            this.methods = extractor.methods;
        }
        else if(language.equals(Language.CPP)) {
            //System.out.println("CPP");
            CPP14Lexer cpp14Lexer = new CPP14Lexer(charStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(cpp14Lexer);

            CPP14Parser parser = new CPP14Parser(commonTokenStream);
            //CPP14Parser.TranslationUnitContext = parser.translationUnit(); // parse a compilationUnit
            CppListener extractor = new CppListener(file, charStream);


            ParseTreeWalker.DEFAULT.walk(extractor, parser.translationUnit());

            this.methods = extractor.methods;
        }



    }

    static class JavaListener extends Java8ParserBaseListener {

        private final ArrayList<Method> methods = new ArrayList<>();
        private final FileExtended file;
        private final CharStream charStream;

        public JavaListener(FileExtended file, CharStream charStream) {
            super();
            this.charStream = charStream;
            this.file = file;
        }

        @Override
        public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
            super.enterMethodDeclaration(ctx);

            if(ctx.start != null) {
                this.methods.add(new Method(ctx, this.file, charStream));
            }
        }
    }

    static class CppListener extends CPP14ParserBaseListener {

        private final ArrayList<Method> methods = new ArrayList<>();
        private final FileExtended file;
        private final CharStream charStream;

        public CppListener(FileExtended file, CharStream charStream) {
            super();
            this.charStream = charStream;
            this.file = file;
        }

        @Override
        public void enterFunctionDefinition(CPP14Parser.FunctionDefinitionContext ctx) {
            super.enterFunctionDefinition(ctx);
            if(ctx.start != null) {
                this.methods.add(new Method(ctx, this.file,charStream));
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
