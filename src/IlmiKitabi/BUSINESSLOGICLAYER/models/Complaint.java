package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;

/**
 * Complaint - Complaint model
 * GRASP: Information Expert
 * From Class Diagram - Use Case 12: Handle Complaint
 */
public class Complaint {
    private String complaintID;
    private String complainantID;
    private String accusedID;
    private String transactionID;
    private String description;
    private String category;
    private Date filedDate;
    private String status;
    private String resolution;
    private Date resolvedDate;

    public Complaint(String complainantID, String accusedID, String transactionID,
                     String description, String category) {
        this.complaintID = generateComplaintID();
        this.complainantID = complainantID;
        this.accusedID = accusedID;
        this.transactionID = transactionID;
        this.description = description;
        this.category = category;
        this.filedDate = new Date();
        this.status = "Pending";
    }

    public Complaint(String complaintID, String complainantID, String accusedID,
                     String transactionID, String description, String category,
                     Date filedDate, String status, String resolution, Date resolvedDate) {
        this.complaintID = complaintID;
        this.complainantID = complainantID;
        this.accusedID = accusedID;
        this.transactionID = transactionID;
        this.description = description;
        this.category = category;
        this.filedDate = filedDate;
        this.status = status;
        this.resolution = resolution;
        this.resolvedDate = resolvedDate;
    }

    private String generateComplaintID() {
        return "COMP" + System.currentTimeMillis();
    }

    public boolean validate() {
        return complainantID != null && !complainantID.isEmpty() &&
                accusedID != null && !accusedID.isEmpty() &&
                description != null && !description.isEmpty() &&
                category != null && !category.isEmpty();
    }

    // Getters and Setters
    public String getComplaintID() { return complaintID; }
    public void setComplaintID(String complaintID) { this.complaintID = complaintID; }

    public String getComplainantID() { return complainantID; }
    public void setComplainantID(String complainantID) { this.complainantID = complainantID; }

    public String getAccusedID() { return accusedID; }
    public void setAccusedID(String accusedID) { this.accusedID = accusedID; }

    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String transactionID) { this.transactionID = transactionID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getFiledDate() { return filedDate; }
    public void setFiledDate(Date filedDate) { this.filedDate = filedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public Date getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(Date resolvedDate) { this.resolvedDate = resolvedDate; }
}