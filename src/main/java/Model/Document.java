package Model;

import javafx.scene.control.Button;

import java.util.List;

/**
 * A document from the library, it's an abstract class because we are going to make more precise document, like a novel ...
 */
public class Document {
    private String title;
    private String author;
    private String year;
    private String id;

    private Button borrowButton;

    /**
     * Constructs a Document object with the specified attributes.
     *
     * @param title   the name of the document
     * @param author the author of the document
     * @param year   the year of publication
     * @param id   the id of the document
     */
    public Document(String title, String author, String year, String id) {
        this.title = title;
        this.id = id;
        this.author = author;
        this.year = year;
        this.borrowButton = new Button("Borrow");
    }
    /**
     * Returns the name of the document.
     * @return the name of the document
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Returns the author of the document.
     *
     * @return the author of the document
     */
    public String getAuthor() {
        return this.author;
    }
    /**
     * Returns the year of publication of the document.
     *
     * @return the year of publication of the document
     */
    public String getYear(){
        return this.year;
    }
    /**
     * Returns the id of the document.
     *
     * @return the id of the document
     */
    public String getId() {
        return this.id;
    }

    public void setBorrowButton(Button borrowButton) {
        this.borrowButton = borrowButton;
    }
}