package classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Member {
    private String lastName;
    private String name;
    private Date birthDate;
    private String mail;
    private List<Document> borrowedBooks;

    public Member(String lastName, String name, Date birthDate, String mail){
        this.borrowedBooks = new ArrayList<Document>();
        this.lastName = lastName;
        this.name = name;
        this.birthDate = birthDate;
        this.mail = mail;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public List<Document> getBorrowedBooks() {
        return borrowedBooks;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public void addBorrowedBooks(Document document) {
        this.borrowedBooks.add(document);
    }
}
