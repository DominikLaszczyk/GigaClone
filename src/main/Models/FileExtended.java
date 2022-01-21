package main.Models;

import javafx.scene.control.CheckBox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileExtended extends File{
    private String name;
    private final long loc;
    private String path;
    private CheckBox included;

    public FileExtended(File file, Boolean included) throws IOException {
        super(file.getPath());

        this.name = file.getName();
        this.loc = Files.lines(Paths.get(file.getPath()), StandardCharsets.ISO_8859_1).count();
        this.path = file.getPath();
        this.included = new CheckBox();
        this.included.setSelected(included);
    }

    public long getLoc() {
        return this.loc;
    }

    public CheckBox getIncluded() {
        return this.included;
    }

}
