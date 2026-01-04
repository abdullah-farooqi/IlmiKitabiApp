package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * ExchangeOffer - Exchange offer model
 * GRASP: Information Expert
 * From Class Diagram - Use Case 9: Exchange
 */
public class ExchangeOffer {
    private String offerID;
    private String studentID;
    private String desiredBookTitle;
    private String desiredBookAuthor;
    private List<String> offeredBookIDs;
    private Date postDate;
    private String status;
    private String meetingLocation;

    public ExchangeOffer(String studentID, String desiredBookTitle, String desiredBookAuthor) {
        this.offerID = generateOfferID();
        this.studentID = studentID;
        this.desiredBookTitle = desiredBookTitle;
        this.desiredBookAuthor = desiredBookAuthor;
        this.offeredBookIDs = new ArrayList<>();
        this.postDate = new Date();
        this.status = "Available";
    }

    public ExchangeOffer(String offerID, String studentID, String desiredBookTitle,
                         String desiredBookAuthor, List<String> offeredBookIDs,
                         Date postDate, String status, String meetingLocation) {
        this.offerID = offerID;
        this.studentID = studentID;
        this.desiredBookTitle = desiredBookTitle;
        this.desiredBookAuthor = desiredBookAuthor;
        this.offeredBookIDs = offeredBookIDs;
        this.postDate = postDate;
        this.status = status;
        this.meetingLocation = meetingLocation;
    }

    private String generateOfferID() {
        return "EXCH" + System.currentTimeMillis();
    }

    public void addOfferedBook(String bookID) {
        if (!offeredBookIDs.contains(bookID)) {
            offeredBookIDs.add(bookID);
        }
    }

    public void removeOfferedBook(String bookID) {
        offeredBookIDs.remove(bookID);
    }

    public boolean matchDesiredBook(String title, String author) {
        return desiredBookTitle.equalsIgnoreCase(title) &&
                desiredBookAuthor.equalsIgnoreCase(author);
    }

    // Getters and Setters
    public String getOfferID() { return offerID; }
    public void setOfferID(String offerID) { this.offerID = offerID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public String getDesiredBookTitle() { return desiredBookTitle; }
    public void setDesiredBookTitle(String desiredBookTitle) { this.desiredBookTitle = desiredBookTitle; }

    public String getDesiredBookAuthor() { return desiredBookAuthor; }
    public void setDesiredBookAuthor(String desiredBookAuthor) { this.desiredBookAuthor = desiredBookAuthor; }

    public List<String> getOfferedBookIDs() { return new ArrayList<>(offeredBookIDs); }
    public void setOfferedBookIDs(List<String> offeredBookIDs) { this.offeredBookIDs = offeredBookIDs; }

    public Date getPostDate() { return postDate; }
    public void setPostDate(Date postDate) { this.postDate = postDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMeetingLocation() { return meetingLocation; }
    public void setMeetingLocation(String meetingLocation) { this.meetingLocation = meetingLocation; }
}