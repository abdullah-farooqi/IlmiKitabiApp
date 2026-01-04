package IlmiKitabi.BUSINESSLOGICLAYER.models;

/**
 * Book - Domain Model Class
 * GRASP: Information Expert - Book knows its own data
 * From Class Diagram
 */
public class Book {
    private String bookID;
    private String title;
    private String author;
    private String isbn;
    private String subject;
    private String description;
    private String imageURL;
    private String condition;
    private boolean isAvailable;
    private String ownerID;
    // For display purposes
    private String ownerName;
    private String offerType;
    private Double price;

    public Book(String title, String author, String subject, String description,
                String condition) {
        this.bookID = generateBookID();
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.description = description;
        this.condition = condition;
        this.isAvailable = true;
    }

    public Book(String bookID, String title, String author, String isbn, String subject,
                String description, String imageURL, String condition, boolean isAvailable) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.subject = subject;
        this.description = description;
        this.imageURL = imageURL;
        this.condition = condition;
        this.isAvailable = isAvailable;
    }

    private String generateBookID() {
        return "BOOK" + System.currentTimeMillis();
    }

    public boolean validateBookInfo() {
        return title != null && !title.isEmpty() &&
                author != null && !author.isEmpty() &&
                subject != null && !subject.isEmpty();
    }

    // Getters and Setters
    public String getBookID() { return bookID; }
    public void setBookID(String bookID) { this.bookID = bookID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getSafeOfferType() {
        return (offerType != null && !offerType.isEmpty()) ? offerType : "UNKNOWN";
    }
    public String getOwnerID() {
        return ownerID;
    }

    // ✅ ADD THIS SETTER
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }


}