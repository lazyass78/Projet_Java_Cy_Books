package Model;

import java.util.List;

/**
 * A document from the library, it's an abstract class because we are going to make more precise document, like a novel ...
 */
public abstract class Document {
    private String name;
    private String author;
    private int year;
    private String editor;
    private String ISBN;
    private int stock;
    private List<Topic> topics;
    /**
     * Constructs a Document object with the specified attributes.
     *
     * @param name   the name of the document
     * @param author the author of the document
     * @param year   the year of publication
     * @param editor the editor of the document
     * @param ISBN   the ISBN (International Standard Book Number) of the document
     * @param stock  the stock quantity of the document
     * @param topics the list of topics associated with the document
     */
    public Document(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        this.name = name;
        this.ISBN = ISBN;
        this.author = author;
        this.stock = stock;
        this.year = year;
        this.topics = topics;
    }
    /**
     * Returns the name of the document.
     * @return the name of the document
     */
    public String getName() {
        return this.name;
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
    public int getYear(){
        return this.year;
    }
    /**
     * Returns the editor of the document.
     *
     * @return the editor of the document
     */
    public String getEditor() {
        return this.editor;
    }
    /**
     * Returns the ISBN (International Standard Book Number) of the document.
     *
     * @return the ISBN of the document
     */
    public String getISBN() {
        return this.ISBN;
    }

    /**
     * Returns the stock quantity of the document.
     *
     * @return the stock quantity of the document
     */
    public int getStock() {
        return this.stock;
    }

    /**
     * Returns the list of topics associated with the document.
     *
     * @return the list of topics associated with the document
     */
    public List<Topic> getTopics() {
        return this.topics;
    }

    /**
     * Sets the name of the document.
     *
     * @param name the name of the document
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Sets the author of the document.
     *
     * @param author the author of the document
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * Sets the editor of the document.
     *
     * @param editor the editor of the document
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }
    /**
     * Sets the ISBN (International Standard Book Number) of the document.
     *
     * @param ISBN the ISBN of the document
     */
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
    /**
     * Sets the stock quantity of the document.
     *
     * @param stock the stock quantity of the document
     */
    public void setStock(int stock) {
        this.stock = stock;
    }
    /**
     * Sets the year of publication of the document.
     *
     * @param year the year of publication of the document
     */
    public void setYear(int year) {
        this.year = year;
    }
    /**
     * Sets the list of topics associated with the document.
     *
     * @param topics the list of topics associated with the document
     */
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}