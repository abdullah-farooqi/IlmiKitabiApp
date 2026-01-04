package IlmiKitabi.BUSINESSLOGICLAYER.models;

import IlmiKitabi.Abstract.Transaction;

import java.util.Date;

/**
 * ExchangeTransaction - Exchange transaction model
 * GRASP: Information Expert
 * From Class Diagram - Use Case 9: Exchange
 */
public class ExchangeTransaction extends Transaction {
    private String student1ID;
    private String student2ID;
    private String book1ID; // Book offered by student1
    private String book2ID; // Book offered by student2
    private Date exchangeDate;
    private boolean isCompleted;

    public ExchangeTransaction(String student1ID, String student2ID, String book1ID, String book2ID) {
        super();
        this.transactionID = generateTransactionID();
        this.student1ID = student1ID;
        this.student2ID = student2ID;
        this.book1ID = book1ID;
        this.book2ID = book2ID;
        this.exchangeDate = new Date();
        this.isCompleted = false;
    }

    public ExchangeTransaction(String transactionID, String student1ID, String student2ID,
                               String book1ID, String book2ID, Date exchangeDate,
                               boolean isCompleted, String status, String meetingDetails) {
        super(transactionID, exchangeDate, status, meetingDetails);
        this.student1ID = student1ID;
        this.student2ID = student2ID;
        this.book1ID = book1ID;
        this.book2ID = book2ID;
        this.exchangeDate = exchangeDate;
        this.isCompleted = isCompleted;
    }

    @Override
    protected String generateTransactionID() {
        return "EXCT" + System.currentTimeMillis();
    }

    @Override
    protected String getDefaultStatus() {
        return "Pending";
    }

    @Override
    public boolean validate() {
        return student1ID != null && !student1ID.isEmpty() &&
                student2ID != null && !student2ID.isEmpty() &&
                book1ID != null && !book1ID.isEmpty() &&
                book2ID != null && !book2ID.isEmpty();
    }

    public void confirmExchange() {
        this.isCompleted = true;
        this.status = "Completed";
    }

    public boolean validateExchange() {
        return validate();
    }

    // Getters and Setters
    public String getStudent1ID() { return student1ID; }
    public void setStudent1ID(String student1ID) { this.student1ID = student1ID; }

    public String getStudent2ID() { return student2ID; }
    public void setStudent2ID(String student2ID) { this.student2ID = student2ID; }

    public String getBook1ID() { return book1ID; }
    public void setBook1ID(String book1ID) { this.book1ID = book1ID; }

    public String getBook2ID() { return book2ID; }
    public void setBook2ID(String book2ID) { this.book2ID = book2ID; }

    public Date getExchangeDate() { return exchangeDate; }
    public void setExchangeDate(Date exchangeDate) { this.exchangeDate = exchangeDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}