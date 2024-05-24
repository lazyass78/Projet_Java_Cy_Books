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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNumberBorrow() {
        return numberBorrow;
    }

    public void setNumberBorrow(int numberBorrow) {
        this.numberBorrow = numberBorrow;
    }

    public boolean isInOrder() {
        return inOrder;
    }

    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<String> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }
}