package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;
import java.util.Calendar;

/**
 * LendingOffer - Lending offer model
 * GRASP: Information Expert
 * Inherits from Offer (abstract class from Class Diagram)
 */
public class LendingOffer {
    private String offerID;
    private String studentID;
    private String bookID;
    private int duration; // days
    private Date postDate;
    private Date endDate;
    private String status;
    private String meetingLocation;

    public LendingOffer(String studentID, String bookID, int duration) {
        this.offerID = generateOfferID();
        this.studentID = studentID;
        this.bookID = bookID;
        this.duration = duration;
        this.postDate = new Date();
        this.endDate = calculateDueDate();
        this.status = "Available";
    }

    public LendingOffer(String offerID, String studentID, String bookID, int duration,
                        Date postDate, Date endDate, String status, String meetingLocation) {
        this.offerID = offerID;
        this.studentID = studentID;
        this.bookID = bookID;
        this.duration = duration;
        this.postDate = postDate;
        this.endDate = endDate;
        this.status = status;
        this.meetingLocation = meetingLocation;
    }

    private String generateOfferID() {
        return "LEND" + System.currentTimeMillis();
    }

    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(postDate);
        cal.add(Calendar.DAY_OF_MONTH, duration);
        return cal.getTime();
    }

    // Getters and Setters
    public String getOfferID() { return offerID; }
    public void setOfferID(String offerID) { this.offerID = offerID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getBookID() { return bookID; }
    public void setBookID(String bookID) { this.bookID = bookID; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }
}