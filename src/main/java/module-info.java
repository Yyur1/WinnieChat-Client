module com.github.desperateyuri.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.github.desperateyuri.client to javafx.fxml, com.fasterxml.jackson.databind, javafx.controls;

    exports com.github.desperateyuri.client;
}