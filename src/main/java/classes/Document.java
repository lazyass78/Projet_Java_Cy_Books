package classes;


import java.util.List;

/**
 * a document from the library, it's an abstract class because we are going to make more precise document, like a novel ...
 */
public abstract class Document {
    private String name;
    private String author;
    private int year;
    private String editor;
    private String ISBN;
    private int stock;
    private List<Topic> topics;

    public Document(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        this.name = name;
        this.ISBN = ISBN;
        this.author = author;
        this.stock = stock;
        this.year = year;
        this.topics = topics;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }
    public int getYear(){
        return this.year;
    }

    public String getEditor() {
        return this.editor;
    }

    public String getISBN() {
        return this.ISBN;
    }

    public int getStock() {
        return this.stock;
    }

    public List<Topic> getTopics() {
        return this.topics;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}