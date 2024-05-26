package Model;

import java.time.LocalDate;
import java.util.List;

public class Member {
    private int id;
    private String firstName;
    private String lastName;
    private int numberBorrow;
    private boolean inOrder;
    private String email;
    private LocalDate birthDate;
    private List<String> borrowedBooks;

    /**
     * Constructs a Member object with the specified parameters.
     * @param id The ID of the member.
     * @param firstName The first name of the member.
     * @param lastName The last name of the member.
     * @param numberBorrow The number of books borrowed by the member.
     * @param inOrder Indicates whether the member's account is in order.
     * @param email The email address of the member.
     * @param birthDate The birth date of the member.
     * @param borrowedBooks The list of books borrowed by the member.
     */
    public Member(int id, String firstName, String lastName,int numberBorrow, boolean inOrder, String email, LocalDate birthDate, List<String> borrowedBooks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numberBorrow = numberBorrow;
        this.inOrder = inOrder;
        this.email = email;
        this.birthDate = birthDate;
        this.borrowedBooks=borrowedBooks;
    }

    /**
     * Returns the ID of the member.
     * @return The ID of the member.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the member.
     * @param id The ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the first name of the member.
     * @return The first name of the member.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the member.
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the member.
     * @return The last name of the member.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the member.
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the number of books borrowed by the member.
     * @return The number of books borrowed by the member.
     */
    public int getNumberBorrow() {
        return numberBorrow;
    }

    /**
     * Sets the number of books borrowed by the member.
     * @param numberBorrow The number of books to set.
     */
    public void setNumberBorrow(int numberBorrow) {
        this.numberBorrow = numberBorrow;
    }

    /**
     * Returns whether the member's account is in order.
     * @return true if the account is in order, false otherwise.
     */
    public boolean isInOrder() {
        return inOrder;
    }

    /**
     * Sets whether the member's account is in order.
     * @param inOrder true if the account is in order, false otherwise.
     */
    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    /**
     * Returns the email address of the member.
     * @return The email address of the member.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the member.
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the birthdate of the member.
     * @return The birthdate of the member.
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birthdate of the member.
     * @param birthDate The birthdate to set.
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Returns the list of books borrowed by the member.
     * @return The list of books borrowed by the member.
     */
    public List<String> getBorrowedBooks() {
        return borrowedBooks;
    }

    /**
     * Sets the list of books borrowed by the member.
     * @param borrowedBooks The list of books to set.
     */
    public void setBorrowedBooks(List<String> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}