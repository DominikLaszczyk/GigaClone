package main.resources;

import java.util.HashMap;
import java.util.Map;

public class Strings {
    public static final String PROGRAM_NAME = "GigaClone";
    public static final String DIRECTORY_CHOOSER_NAME = "Choose a directory";
    public static final String CHOSEN_DIRECTORY = "Chosen Directory: ";
    public static final Map<String, String> LANGUAGES_WITH_EXT = Map.of("Java","java","Python","py","C++","cpp");


    //---------------------------ALERTS----------------------------//
    public static final String NO_DIR_OR_LANG_CHOSEN_HEADER_TEXT = "The Directory or Language not selected!";
    public static final String NO_DIR_OR_LANG_CHOSEN_CONTENT_TEXT = "Please choose a Directory and a Language.";

    public static final String NO_FILES_INCLUDED_HEADER_TEXT = "No files selected!";
    public static final String NO_FILES_INCLUDED_CONTENT_TEXT = "Please include at least two files.";

    public static final String NO_CLONE_DETECTION_ALGORITHM_SELECTED_HEADER_TEXT = "No Algorithm selected!";
    public static final String NO_CLONE_DETECTION_ALGORITHM_SELECTED_CONTENT_TEXT = "Please select a Clone Detection Algorithm.";


}
