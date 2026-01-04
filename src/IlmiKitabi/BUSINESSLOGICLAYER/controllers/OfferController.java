package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.Abstract.AbstractOfferController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import IlmiKitabi.BUSINESSLOGICLAYER.models.LendingOffer;
import IlmiKitabi.BUSINESSLOGICLAYER.models.SellingOffer;
import IlmiKitabi.DATAACCESSLAYER.OfferDAO;

public class OfferController extends AbstractOfferController {

    private OfferDAO offerDAO;

    private int lendingDuration;
    private double sellingPrice;
    private boolean sellingNegotiable;

    private enum OfferMode {
        NONE, LEND, SELL
    }

    private OfferMode mode = OfferMode.NONE;

    public OfferController() {
        super();
        this.offerDAO = new OfferDAO();
    }

    // -------------------------------------------------------
    //   PUBLIC METHODS CALLED BY UI / CONTROLLERS
    // -------------------------------------------------------

    public boolean createLendingOffer(String studentID, Book book, int duration) {
        this.mode = OfferMode.LEND;
        this.lendingDuration = duration;

        return createOffer(studentID, book, "LEND");
    }

    public boolean createSellingOffer(String studentID, Book book, double price, boolean negotiable) {
        this.mode = OfferMode.SELL;
        this.sellingPrice = price;
        this.sellingNegotiable = negotiable;

        return createOffer(studentID, book, "SELL");
    }

    // -------------------------------------------------------
    //  TEMPLATE METHOD IMPLEMENTATION
    // -------------------------------------------------------
    @Override
    protected boolean createSpecificOffer(String studentID, Book book) {

        switch (mode) {

            case LEND: {
                LendingOffer offer = new LendingOffer(studentID, book.getBookID(), lendingDuration);
                return offerDAO.saveLendingOffer(offer);
            }

            case SELL: {
                SellingOffer offer = new SellingOffer(studentID, book.getBookID(),
                        sellingPrice, sellingNegotiable);

                if (!offer.validatePrice()) {
                    System.out.println("❌ Invalid price");
                    return false;
                }

                return offerDAO.saveSellingOffer(offer);
            }

            default:
                System.out.println("❌ Unknown offer mode");
                return false;
        }
    }
}
