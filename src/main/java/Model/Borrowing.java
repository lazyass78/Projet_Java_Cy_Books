package Model;

import java.time.LocalDate;

public class Borrowing {
    private String isbn;
    private String memberMail;
    private String title;
    private String author;
    private String year;
    private int stock;
    private LocalDate borrowingDate;
    private LocalDate returnDate;

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
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMemberMail() {
        return memberMail;
    }

    public void setMemberMail(String memberMail) {
        this.memberMail = memberMail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getBorrowingDate() {
        return borrowingDate;
    }

    public void setBorrowingDate(LocalDate borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }


}

