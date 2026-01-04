package IlmiKitabi.Abstract;

import java.util.Date;

/**
 * Transaction - Abstract base class for all transactions
 * GRASP: Information Expert, Polymorphism
 * Provides common functionality for all transaction types
 */
public abstract class Transaction {
    protected String transactionID;
    protected Date transactionDate;
    protected String status;
    protected String meetingDetails;

    public Transaction() {
        this.transactionDate = new Date();
        this.status = getDefaultStatus();
    }

    public Transaction(String transactionID, Date transactionDate, String status, String meetingDetails) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.status = status;
        this.meetingDetails = meetingDetails;
    }

    // Abstract methods to be implemented by subclasses
    protected abstract String generateTransactionID();
    protected abstract String getDefaultStatus();
    public abstract boolean validate();

    // Common getters and setters
    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String transactionID) { this.transactionID = transactionID; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetingDetails() { return meetingDetails; }
    public void setMeetingDetails(String meetingDetails) { this.meetingDetails = meetingDetails; }
}