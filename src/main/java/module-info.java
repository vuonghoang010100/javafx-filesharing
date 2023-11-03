module com.group2.fireshare {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.group2.fireshare to javafx.fxml;
    exports com.group2.fireshare;

    exports com.group2.fireshare.client;
    opens com.group2.fireshare.client to javafx.fxml;
    exports com.group2.fireshare.client.controller;
    opens com.group2.fireshare.client.controller to javafx.fxml;
    exports com.group2.fireshare.client.model;

    exports com.group2.fireshare.server;
    opens com.group2.fireshare.server to javafx.fxml;
    exports com.group2.fireshare.server.controller;
    opens com.group2.fireshare.server.controller to javafx.fxml;
    exports com.group2.fireshare.server.model;
}