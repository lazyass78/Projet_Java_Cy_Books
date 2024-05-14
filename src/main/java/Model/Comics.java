package Model;

import java.util.List;

public class Comics extends Document {

    public Comics(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics) {
        super(name, author, year, editor, ISBN, stock, topics);
    }

}
