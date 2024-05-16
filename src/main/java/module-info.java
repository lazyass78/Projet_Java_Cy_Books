module Controller {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.net.http;

    opens Controller to javafx.fxml;

    exports Controller;
    exports Test;

}

