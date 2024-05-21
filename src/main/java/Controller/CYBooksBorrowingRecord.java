package Controller;

import java.time.LocalDate;

public class CYBooksBorrowingRecord {
    private String isbn;
    private String memberId;
    private String title;
    private String author;
    private int year;
    private String editor;
    private int stock;
    private String topics;
    private LocalDate borrowingDate;
    private LocalDate returnDate;

        public CYBooksBorrowingRecord(String isbn,String memberId, String title, String author, int year, String editor, int stock, String topics,LocalDate borrowingDate,LocalDate returnDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.title=title;
        this.author = author;
        this.year = year;
        this.editor = editor;
        this.stock = stock;
        this.topics = topics;
        this.borrowingDate = borrowingDate;
        this.returnDate=returnDate;

    }
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
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

