module com.quartyom.lab6 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires slf4j.api;

    opens com.quartyom.lab6 to javafx.fxml;
    exports com.quartyom.lab6;
    exports com.quartyom.lab6.SceneControllers;
    opens com.quartyom.lab6.SceneControllers to javafx.fxml;
    exports com.quartyom.lab6.NetworkDevices;
    opens com.quartyom.lab6.NetworkDevices to javafx.fxml;
    exports com.quartyom.lab6.Database;
    opens com.quartyom.lab6.Database to javafx.fxml;
}