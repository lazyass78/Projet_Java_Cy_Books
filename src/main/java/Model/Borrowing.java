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
    public Borrowing(Document document, Member member, Date borrowingDate, Date returnDate){
        this.document = document;
        this.member = member;
        this.borrowingDate = borrowingDate;
        this.returnDate = returnDate;
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
}
