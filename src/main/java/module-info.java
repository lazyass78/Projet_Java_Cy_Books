module Controller {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    opens Controller to javafx.fxml;

    exports Controller;
    exports Test;

}

