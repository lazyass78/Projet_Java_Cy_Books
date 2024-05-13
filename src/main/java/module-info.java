module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    exports classes;
    exports applications;
    opens applications to javafx.fxml;

    opens org.example.test to javafx.fxml;
    exports org.example.test;
    exports;
    opens to
    exports;
    exports;
    exports;
    exports;
    opens to
}
