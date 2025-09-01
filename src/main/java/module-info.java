module com.brux.tris {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.brux.tris.controller to javafx.fxml; // per i controller FXML
    exports com.brux.tris.app;                     // per l’entry point App
}
