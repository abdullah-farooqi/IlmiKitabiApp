package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.LendingOffer;
import IlmiKitabi.BUSINESSLOGICLAYER.models.SellingOffer;
import java.sql.*;

/**
 * OfferDAO - Data Access for Lending and Selling Offers
 * GRASP: High Cohesion
 */
public class OfferDAO {
    private DatabaseConnection dbConnection;

    public OfferDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean saveLendingOffer(LendingOffer offer) {
        String sql = "INSERT INTO lending_offers (offer_id, student_id, book_id, duration, " +
                "post_date, end_date, status, meeting_location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, offer.getOfferID());
            stmt.setString(2, offer.getStudentID());
            stmt.setString(3, offer.getBookID());
            stmt.setInt(4, offer.getDuration());
            stmt.setTimestamp(5, new Timestamp(offer.getPostDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(offer.getEndDate().getTime()));
            stmt.setString(7, offer.getStatus());
            stmt.setString(8, offer.getMeetingLocation());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving lending offer: " + e.getMessage());
            return false;
        }
    }

    public boolean saveSellingOffer(SellingOffer offer) {
        String sql = "INSERT INTO selling_offers (offer_id, student_id, book_id, price, " +
                "is_negotiable, post_date, status, meeting_location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, offer.getOfferID());
            stmt.setString(2, offer.getStudentID());
            stmt.setString(3, offer.getBookID());
            stmt.setDouble(4, offer.getPrice());
            stmt.setBoolean(5, offer.isNegotiable());
            stmt.setTimestamp(6, new Timestamp(offer.getPostDate().getTime()));
            stmt.setString(7, offer.getStatus());
            stmt.setString(8, offer.getMeetingLocation());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving selling offer: " + e.getMessage());
            return false;
        }
    }
}