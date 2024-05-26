package Model;

import java.time.LocalDate;

/**
 * Class that represents a borrowing record in the library
 */
public class Borrowing {
    private String isbn;
    private String memberMail;
    private String title;
    private String author;
    private String year;
    private int stock;
    private LocalDate borrowingDate;
    private LocalDate returnDate;

    /**
     * Constructs a new Borrowing object with the specified details.
     * @param isbn The ISBN of the borrowed book.
     * @param memberMail The email of the member who borrowed the book.
     * @param title The title of the borrowed book.
     * @param author The author of the borrowed book.
     * @param year The year of publication of the borrowed book.
     * @param stock The stock quantity of the borrowed book.
     * @param borrowingDate The date when the book was borrowed.
     * @param returnDate The date when the book is due to be returned.
     */
    public Borrowing(String isbn, String memberMail, String title, String author, String year,  int stock, LocalDate borrowingDate, LocalDate returnDate) {
        this.isbn = isbn;
        this.memberMail = memberMail;
        this.title=title;
        this.author = author;
        this.year = year;
        this.stock = stock;
        this.borrowingDate = borrowingDate;
        this.returnDate=returnDate;

    }
    /**
     * Returns the ISBN of the borrowed book.
     * @return The ISBN of the borrowed book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the borrowed book.
     * @param isbn The ISBN to set.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Returns the email of the member who borrowed the book.
     * @return The email of the member who borrowed the book.
     */
    public String getMemberMail() {
        return memberMail;
    }

    /**
     * Sets the email of the member who borrowed the book.
     * @param memberMail The email to set.
     */
    public void setMemberMail(String memberMail) {
        this.memberMail = memberMail;
    }

    /**
     * Returns the title of the borrowed book.
     * @return The title of the borrowed book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the borrowed book.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of the borrowed book.
     * @return The author of the borrowed book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the borrowed book.
     * @param author The author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the year of publication of the borrowed book.
     * @return The year of publication of the borrowed book.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year of publication of the borrowed book.
     * @param year The year to set.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Returns the stock quantity of the borrowed book.
     * @return The stock quantity of the borrowed book.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the stock quantity of the borrowed book.
     * @param stock The stock quantity to set.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Returns the date when the book was borrowed.
     * @return The date when the book was borrowed.
     */
    public LocalDate getBorrowingDate() {
        return borrowingDate;
    }

    /**
     * Sets the date when the book was borrowed.
     * @param borrowingDate The borrowing date to set.
     */
    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    /**
     * Returns the date when the book is due to be returned.
     * @return The date when the book is due to be returned.
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the date when the book is due to be returned.
     * @param returnDate The return date to set.
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }


}

