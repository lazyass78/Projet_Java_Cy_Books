package classes;


import java.util.List;

/**
 * a document from the library
 */
public class Document {
    public String name;
    public String author;
    public int year;
    public String editor;
    public String ISBN;
    public int stock;
    public List<Sujet> topics;

    public Document(String name,String author,int year,String editor,String ISBN,int stock,List<Sujet> topics){
        this.name=name;
        this.author=author;
        this.year=year;
        this.editor=editor;
        this.ISBN=ISBN;
        this.topics=topics;

    }
    public String getName() {
        return name;
    }
    public String getAuthor() {
        return author;
    }
    public int getYear() {
        return year;
    }
    public String getEditor() {
        return editor;
    }
    public String getISBN() {
        return ISBN;
    }
    public List<Sujet> getTopics() {
        return topics;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}