package classes;

import java.util.List;

public class Tale extends Document{
    public Tale(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        super(name, author, year, editor, ISBN, stock, topics);
    }
}
