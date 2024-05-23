package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CYBooksHome extends Application {

    /**
     * This method is called when the JavaFX application is launched.
     * It loads the "CYBooks_Home.fxml" file using FXMLLoader,
     * creates a new Scene with the loaded content, and displays it on the stage.
     * @param stage The primary stage of the JavaFX application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CYBooksHome.class.getResource("CYBooks_Home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);

        stage.setTitle("CYBooks"); // Sets the title of the stage
        stage.setScene(scene);  // Sets the scene to be displayed on the stage
        stage.show(); // Shows the stage
    }

    /**
     * The main entry point for the JavaFX application.
     * It launches the JavaFX application by calling the `launch()` method.
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(); // Launches the JavaFX application
    }

}
