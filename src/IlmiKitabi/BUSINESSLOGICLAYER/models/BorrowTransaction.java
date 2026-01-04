package IlmiKitabi.BUSINESSLOGICLAYER.models;

import IlmiKitabi.Abstract.Transaction;

import java.util.Date;
import java.util.Calendar;

/**
 * BorrowTransaction - Borrow transaction model
 * GRASP: Information Expert
 * From Class Diagram
 */
public class BorrowTransaction extends Transaction {
    private String borrowerID;
    private String lenderID;
    private String bookID;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private boolean isReturned;
    private boolean isOverdue;

    public BorrowTransaction(String borrowerID, String lenderID, String bookID, int duration) {
        super();
        this.transactionID = generateTransactionID();
        this.borrowerID = borrowerID;
        this.lenderID = lenderID;
        this.bookID = bookID;
        this.borrowDate = new Date();
        this.dueDate = calculateDueDate(duration);
        this.isReturned = false;
        this.isOverdue = false;
    }

    public BorrowTransaction(String transactionID, String borrowerID, String lenderID, String bookID,
                             Date borrowDate, Date dueDate, Date returnDate, boolean isReturned,
                             boolean isOverdue, String status, String meetingDetails) {
        super(transactionID, borrowDate, status, meetingDetails);
        this.borrowerID = borrowerID;
        this.lenderID = lenderID;
        this.bookID = bookID;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
        this.isOverdue = isOverdue;
    }

    @Override
    protected String generateTransactionID() {
        return "BRW" + System.currentTimeMillis();
    }

    @Override
    protected String getDefaultStatus() {
        return "Active";
    }

    @Override
    public boolean validate() {
        return borrowerID != null && !borrowerID.isEmpty() &&
                lenderID != null && !lenderID.isEmpty() &&
                bookID != null && !bookID.isEmpty();
    }

    private Date calculateDueDate(int duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowDate);
        cal.add(Calendar.DAY_OF_MONTH, duration);
        return cal.getTime();
    }

    public boolean checkOverdue() {
        if (!isReturned && dueDate != null) {
            return new Date().after(dueDate);
        }
        return false;
    }

    // Getters and Setters
    public String getBorrowerID() { return borrowerID; }
    public void setBorrowerID(String borrowerID) { this.borrowerID = borrowerID; }

    public String getLenderID() { return lenderID; }
    public void setLenderID(String lenderID) { this.lenderID = lenderID; }

    public String getBookID() { return bookID; }
    public void setBookID(String bookID) { this.bookID = bookID; }

    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
}
