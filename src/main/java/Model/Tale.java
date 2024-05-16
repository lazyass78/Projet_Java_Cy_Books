package Model;

import java.util.List;

public class Tale extends Document {
    public Tale(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        super(name, author, year, editor, ISBN, stock, topics);
    }


    @Override
    public int getStock() {
        return super.getStock();
    }

    @Override
    public int getYear() {
        return super.getYear();
    }

    @Override
    public String getAuthor() {
        return super.getAuthor();
    }

    @Override
    public String getEditor() {
        return super.getEditor();
    }

    @Override
    public String getISBN() {
        return super.getISBN();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public List<Topic> getTopics() {
        return super.getTopics();
    }

    @Override
    public void setAuthor(String author) {
        super.setAuthor(author);
    }

    @Override
    public void setEditor(String editor) {
        super.setEditor(editor);
    }

    @Override
    public void setISBN(String ISBN) {
        super.setISBN(ISBN);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setStock(int stock) {
        super.setStock(stock);
    }

    @Override
    public void setTopics(List<Topic> topics) {
        super.setTopics(topics);
    }

    @Override
    public void setYear(int year) {
        super.setYear(year);
    }
}
