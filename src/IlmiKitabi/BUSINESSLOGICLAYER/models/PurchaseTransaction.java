package IlmiKitabi.BUSINESSLOGICLAYER.models;

import IlmiKitabi.Abstract.Transaction;

import java.util.Date;

/**
 * PurchaseTransaction - Purchase transaction model
 * GRASP: Information Expert
 */

public class PurchaseTransaction extends Transaction {
    private String buyerID;
    private String sellerID;
    private String bookID;
    private double amount;
    private String paymentMethod;

    public PurchaseTransaction(String buyerID, String sellerID, String bookID, double amount) {
        super();
        this.transactionID = generateTransactionID();
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.bookID = bookID;
        this.amount = amount;
        this.paymentMethod = "Cash";
    }

    public PurchaseTransaction(String transactionID, String buyerID, String sellerID, String bookID,
                               double amount, Date transactionDate, String status,
                               String paymentMethod, String meetingDetails) {
        super(transactionID, transactionDate, status, meetingDetails);
        this.buyerID = buyerID;
        this.sellerID = sellerID;
        this.bookID = bookID;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    @Override
    protected String generateTransactionID() {
        return "PUR" + System.currentTimeMillis();
    }

    @Override
    protected String getDefaultStatus() {
        return "Completed";
    }

    @Override
    public boolean validate() {
        return buyerID != null && !buyerID.isEmpty() &&
                sellerID != null && !sellerID.isEmpty() &&
                bookID != null && !bookID.isEmpty() &&
                amount > 0;
    }

    // Getters and Setters
    public String getBuyerID() { return buyerID; }
    public void setBuyerID(String buyerID) { this.buyerID = buyerID; }

    public String getSellerID() { return sellerID; }
    public void setSellerID(String sellerID) { this.sellerID = sellerID; }

    public String getBookID() { return bookID; }
    public void setBookID(String bookID) { this.bookID = bookID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}