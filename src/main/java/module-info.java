module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    opens classes to javafx.fxml;
    exports classes;
    exports applications;
    opens applications to javafx.fxml;
}
