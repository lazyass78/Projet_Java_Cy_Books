package classes;


import java.util.List;

/**
 * a document from the library, it's an abstract class because we are going to make more precise document, like a novel ...
 */
public abstract class Document {
    public String name;
    public String author;
    public int year;
    public String editor;
    public String ISBN;
    public int stock;
    public List<Sujet> topics;
}