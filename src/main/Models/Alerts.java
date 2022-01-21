package main.Models;

import javafx.scene.control.Alert;
import main.resources.Strings;

public class Alerts {
    private static final Alert noDirOrLangChosenAlert = new Alert(Alert.AlertType.ERROR);
    private static final Alert noFilesIncludedAlert = new Alert(Alert.AlertType.ERROR);

    public static Alert getNoDirOrLangChosenAlert() {
        noDirOrLangChosenAlert.setContentText(Strings.NO_DIR_OR_LANG_CHOSEN_CONTENT_TEXT);
        noDirOrLangChosenAlert.setHeaderText(Strings.NO_DIR_OR_LANG_CHOSEN_HEADER_TEXT);
        return noDirOrLangChosenAlert;
    }

    public static Alert getNoFilesIncludedAlert() {
        noFilesIncludedAlert.setContentText(Strings.NO_FILES_INCLUDED_CONTENT_TEXT);
        noFilesIncludedAlert.setHeaderText(Strings.NO_FILES_INCLUDED_HEADER_TEXT);
        return noFilesIncludedAlert;
    }
}
