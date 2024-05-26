package Model;

import javafx.scene.control.Button;

import java.util.List;

/**
 * A document/book from the library
 */
public class Document {
    private String title;
    private String author;
    private String year;
    private String id;


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

    /**
     * Sets the title of the object.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the author of the object.
     * @param author The author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the year of the object.
     * @param year The year to set.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Sets the ID of the object.
     * @param id The ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

}