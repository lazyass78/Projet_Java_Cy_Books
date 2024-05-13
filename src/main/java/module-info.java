module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    exports classes;
    opens classes to javafx.fxml;
    exports applications;
    opens applications to javafx.fxml;

    opens org.example.test to javafx.fxml;
    exports org.example.test;

}
