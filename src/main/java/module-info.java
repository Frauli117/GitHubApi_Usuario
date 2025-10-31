module githubclient {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.base;

    opens githubclient.ui to javafx.fxml;
    opens githubclient.domain to javafx.base, com.fasterxml.jackson.databind;
    exports githubclient;
}
