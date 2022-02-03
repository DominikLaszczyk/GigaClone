package main.Models;

import java.util.ArrayList;

public enum Language {
    JAVA("Java", ".java"),
    CPP("C++", ".cpp"),
    PYTHON("Python", ".py");

    private String name;
    private String extension;

    Language(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    @Override
    public String toString() {
        return  name;
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> languageNames = new ArrayList<String>();
        for(Language lang : Language.values()) {
            languageNames.add(lang.name);
        }
        return languageNames;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getName() {
        return this.name;
    }
}
