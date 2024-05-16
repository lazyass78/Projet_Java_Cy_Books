package Model;

import java.util.Date;

/**
 * Association Class Borrowing
 * Represents a borrowing association between a document and a member.
 */
public class Borrowing {
    /** The document being borrowed. */
    private Document document;

    /** The member who borrowed the document. */
    private Member member;

    /** The date when the document was borrowed. */
    private Date borrowingDate;

    /** The date when the document is expected to be returned. */
    private Date returnDate;
    private boolean status;

    /**
     * Constructor
     * Constructs a new Borrowing object with the specified document, member, borrowing date, and return date.
     * @param document
     * @param member
     * @param borrowingDate
     * @param returnDate
     */
    public Borrowing(Document document, Member member, Date borrowingDate, Date returnDate, Boolean status){
        this.document = document;
        this.member = member;
        this.borrowingDate = borrowingDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Date getBorrowingDate() {
        return borrowingDate;
    }

    public Document getDocument() {
        return document;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Member getMember() {
        return member;
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Extends the return date of the borrowing.
     *
     * @param extendDate the new return date to be set
     * @return true if the return date is successfully extended, false otherwise
     */
    public boolean extendDate(Date extendDate) {
        if(extendDate.after(this.returnDate)) {
            this.returnDate = extendDate;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkStatus() {
        if (this.status) {
            System.out.println("Livre emprunté");
            return true;
        }
        else {
            System.out.println("Livre disponible");
            return false;
        }

    }
}
