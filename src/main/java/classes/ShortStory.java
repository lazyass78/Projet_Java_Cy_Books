package classes;

import java.util.List;

public class ShortStory extends Document {
    public ShortStory(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics ){
        super(name, author, year, editor, ISBN, stock, topics);
    }
}
