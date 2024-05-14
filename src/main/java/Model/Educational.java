package Model;

import java.util.List;

public class Educational extends Document{

    public Educational(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        super(name, author, year, editor, ISBN, stock, topics);
    }
}
