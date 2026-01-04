package IlmiKitabi.Abstract;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.DATAACCESSLAYER.BookDAO;

public abstract class AbstractOfferController {

    protected BookDAO bookDAO;

    public AbstractOfferController() {
        this.bookDAO = new BookDAO();
    }

    public final boolean createOffer(String studentID, Book book, String offerType) {
        try {
            if (!book.validateBookInfo()) {
                System.out.println("❌ Invalid book information");
                return false;
            }

            book.setOwnerID(studentID);
            book.setOfferType(offerType);

            if (!bookDAO.save(book)) {
                System.out.println("❌ Failed to save book");
                return false;
            }

            return createSpecificOffer(studentID, book);

        } catch (Exception e) {
            System.err.println("❌ Error creating offer: " + e.getMessage());
            return false;
        }
    }

    protected abstract boolean createSpecificOffer(String studentID, Book book);
}
