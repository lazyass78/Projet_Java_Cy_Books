package classes;

import java.util.Date;

public class Borrowing {
    private Document document;

    private Member member;

    private Date borrowingDate;

    private Date returnDate;

    public Borrowing(Document document, Member member, Date borrowingDate, Date returnDate){
        this.document = document;
        this.member = member;
        this.borrowingDate = borrowingDate;
        this.returnDate = returnDate;
    }

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
