module org.example.test {
    requires javafx.controls;
    requires javafx.fxml;

    opens applications to javafx.fxml;

    opens org.example.test to javafx.fxml;
}
