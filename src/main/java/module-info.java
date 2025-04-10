module com.example.teamalfred {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.teamalfred to javafx.fxml;
    exports com.example.teamalfred;
    exports com.example.teamalfred.controllers;
    opens com.example.teamalfred.controllers to javafx.fxml;
    exports com.example.teamalfred.database;
    opens com.example.teamalfred.database to javafx.fxml;
}