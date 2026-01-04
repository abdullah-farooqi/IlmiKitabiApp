package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;

/**
 * TransactionHistory - Unified view of all transactions
 * GRASP: Information Expert
 */
public class TransactionHistory {
    private String transactionID;
    private String studentID;
    private String bookTitle;
    private String bookAuthor;
    private String otherPartyName;
    private String transactionType; // BORROW, LEND, BUY, SELL
    private Date transactionDate;
    private Date dueDate;
    private String status;
    private Double amount;
    private boolean isOverdue;

    public TransactionHistory(String transactionID, String studentID, String bookTitle,
                              String bookAuthor, String otherPartyName, String transactionType,
                              Date transactionDate, Date dueDate, String status,
                              Double amount, boolean isOverdue) {
        this.transactionID = transactionID;
        this.studentID = studentID;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.otherPartyName = otherPartyName;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.dueDate = dueDate;
        this.status = status;
        this.amount = amount;
        this.isOverdue = isOverdue;
    }

    // Getters and Setters
    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String transactionID) { this.transactionID = transactionID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public String getOtherPartyName() { return otherPartyName; }
    public void setOtherPartyName(String otherPartyName) { this.otherPartyName = otherPartyName; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public boolean isOverdue() { return isOverdue; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
}