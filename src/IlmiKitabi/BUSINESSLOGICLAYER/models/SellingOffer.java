package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;

/**
 * SellingOffer - Selling offer model
 * GRASP: Information Expert
 */
public class SellingOffer {
    private String offerID;
    private String studentID;
    private String bookID;
    private double price;
    private boolean isNegotiable;
    private Date postDate;
    private String status;
    private String meetingLocation;

    public SellingOffer(String studentID, String bookID, double price, boolean isNegotiable) {
        this.offerID = generateOfferID();
        this.studentID = studentID;
        this.bookID = bookID;
        this.price = price;
        this.isNegotiable = isNegotiable;
        this.postDate = new Date();
        this.status = "Available";
    }

    public SellingOffer(String offerID, String studentID, String bookID, double price,
                        boolean isNegotiable, Date postDate, String status, String meetingLocation) {
        this.offerID = offerID;
        this.studentID = studentID;
        this.bookID = bookID;
        this.price = price;
        this.isNegotiable = isNegotiable;
        this.postDate = postDate;
        this.status = status;
        this.meetingLocation = meetingLocation;
    }

    private String generateOfferID() {
        return "SELL" + System.currentTimeMillis();
    }

    public boolean validatePrice() {
        return price > 0 && price < 100000;
    }

    // Getters and Setters
    public String getOfferID() { return offerID; }
    public void setOfferID(String offerID) { this.offerID = offerID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getBookID() { return bookID; }
    public void setBookID(String bookID) { this.bookID = bookID; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isNegotiable() { return isNegotiable; }
    public void setNegotiable(boolean negotiable) { isNegotiable = negotiable; }

    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }
}