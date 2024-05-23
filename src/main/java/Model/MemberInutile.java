package Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a member of the library.
 */
public class MemberInutile {
    private String lastName;
    private String name;
    private Date birthDate;
    private String mail;
    private List<Document> borrowedBooks;
    /**
     * Constructs a Member object with the specified attributes.
     *
     * @param lastName   the last name of the member
     * @param name       the first name of the member
     * @param birthDate  the birthdate of the member
     * @param mail       the email address of the member
     */
    public MemberInutile(String lastName, String name, Date birthDate, String mail){
        this.borrowedBooks = new ArrayList<Document>();
        this.lastName = lastName;
        this.name = name;
        this.birthDate = birthDate;
        this.mail = mail;
    }
    /**
     * Returns the birthdate of the member.
     *
     * @return the birthdate of the member
     */
    public Date getBirthDate() {
        return birthDate;
    }
    /**
     * Returns the list of borrowed books by the member.
     *
     * @return the list of borrowed books by the member
     */
    public List<Document> getBorrowedBooks() {
        return borrowedBooks;
    }
    /**
     * Returns the last name of the member.
     *
     * @return the last name of the member
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Returns the email address of the member.
     *
     * @return the email address of the member
     */
    public String getMail() {
        return mail;
    }
    /**
     * Returns the first name of the member.
     *
     * @return the first name of the member
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the birthdate of the member.
     *
     * @param birthDate the birthdate of the member
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    /**
     * Sets the list of borrowed books by the member.
     *
     * @param borrowedBooks the list of borrowed books by the member
     */
    public void setBorrowedBooks(List<Document> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
    /**
     * Sets the last name of the member.
     *
     * @param lastName the last name of the member
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Sets the email address of the member.
     *
     * @param mail the email address of the member
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
    /**
     * Sets the first name of the member.
     *
     * @param name the first name of the member
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a document to the list of borrowed books by the member.
     *
     * @param document the document to be added to the list of borrowed books
     */
    public void addBorrowedBooks(Document document) {
        this.borrowedBooks.add(document);
    }
}
