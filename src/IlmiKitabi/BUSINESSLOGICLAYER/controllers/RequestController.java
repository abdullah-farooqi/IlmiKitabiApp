package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.BUSINESSLOGICLAYER.models.BookRequest;
import IlmiKitabi.DATAACCESSLAYER.RequestDAO;

/**
 * RequestController - Handles borrow/buy requests
 * GRASP: Controller
 * Implements Use Cases 10 & 11: Borrow/Buy Request
 */
public class RequestController {
    private RequestDAO requestDAO;

    public RequestController() {
        this.requestDAO = new RequestDAO();
    }

    /**
     * Send a request to the book owner.
     * @param requesterID ID of the student requesting the book
     * @param book The book being requested
     */
    public boolean sendBookRequest(String requesterID, Book book) {
        try {
            if (book == null || requesterID == null || requesterID.isEmpty()) {
                System.out.println("Invalid request details");
                return false;
            }

            // We no longer use book.getOwnerID()
            // Assuming the owner ID is not required or handled internally by the DAO
            BookRequest request = new BookRequest(
                    requesterID,
                    null, // no owner ID, can be set later or ignored by DAO
                    book.getBookID(),
                    book.getOfferType()
            );

            // Save in DB
            return requestDAO.saveBookRequest(request);
        } catch (Exception e) {
            System.err.println("Error sending book request: " + e.getMessage());
            return false;
        }
    }
}
