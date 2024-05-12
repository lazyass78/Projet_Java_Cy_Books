package classes;

import java.util.List;

public class Theater extends Document{
    public Theater(String name, String author, int year, String editor, String ISBN, int stock, List<Topic> topics ){
        super(name, author, year, editor, ISBN, stock, topics);
    }
}
