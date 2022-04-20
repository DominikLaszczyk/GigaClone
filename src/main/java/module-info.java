module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires antlr;
    requires javafx.web;
    requires javafx.swing;
    requires com.google.common;

    exports project.Controllers;

    opens project;
    opens project.Controllers;
    opens project.Models;
}