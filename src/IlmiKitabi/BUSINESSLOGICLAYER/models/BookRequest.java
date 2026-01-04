package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;
import java.util.UUID;

/**
 * BookRequest - Represents a borrow/buy request between students
 * Implements Use Case 10 & 11
 */
public class BookRequest {
    private String requestID;
    private String requesterID;
    private String ownerID;
    private String bookID;
    private String requestType; // LEND or SELL
    private String status; // Pending, Accepted, Rejected
    private Date requestDate;

    public BookRequest(String requesterID, String ownerID, String bookID, String requestType) {
        this.requestID = generateRequestID();
        this.requesterID = requesterID;
        this.ownerID = ownerID;
        this.bookID = bookID;
        this.requestType = requestType;
        this.status = "Pending";
        this.requestDate = new Date();
    }

    private String generateRequestID() {
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Getters
    public String getRequestID() { return requestID; }
    public String getRequesterID() { return requesterID; }
    public String getOwnerID() { return ownerID; }
    public String getBookID() { return bookID; }
    public String getRequestType() { return requestType; }
    public String getStatus() { return status; }
    public Date getRequestDate() { return requestDate; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}
