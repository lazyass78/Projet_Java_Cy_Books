package classes;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Background extends Application {

    public void start(Stage stage) {
        // Loading the background image
        Image image = new Image("file:src/backgroundBNF.jpg");
        ImageView mv = new ImageView(image);

        // Width of the upper rectangle
        double desiredWidth = 1900;

        // Creating the upper band rectangle
        Rectangle bandeSuperieure = new Rectangle(desiredWidth, 250);
        bandeSuperieure.setFill(Color.LIGHTGRAY);

        // Code to add a text ("CY BOOKS")
        Text text = new Text("CY BOOKS");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        text.setFill(Color.BLUE);

        // Positioning the text
        text.setX(desiredWidth / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setY(100);

        // Creating the group to contain the image, text, and rectangle
        Group root = new Group();
        root.getChildren().addAll(mv, bandeSuperieure, text);

        // Adjusting the scene size according to the desired size of the image and the upper band
        Scene scene = new Scene(root, desiredWidth, 250); // Fixing the height to the height of the upper band
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
