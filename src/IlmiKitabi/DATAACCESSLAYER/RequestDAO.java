package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.BookRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * RequestDAO - Handles database operations for book requests
 * Follows DAO Pattern
 * FIXED: Check actual database columns and use correct names
 */
public class RequestDAO {

    /**
     * Save a new book request to the database
     * Uses actual database column names
     */
    public boolean saveBookRequest(BookRequest request) {
        // First, let's check what columns exist in the table
        if (!verifyTableStructure()) {
            System.err.println("❌ Table structure verification failed");
            return false;
        }

        // Use the correct column name based on your actual database
        // The schema shows it should be 'request_type', but we'll verify
        String sql = "INSERT INTO book_requests " +
                "(request_id, requester_id, owner_id, book_id, request_type, status, request_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, request.getRequestID());
            stmt.setString(2, request.getRequesterID());
            stmt.setString(3, request.getOwnerID());
            stmt.setString(4, request.getBookID());
            stmt.setString(5, request.getRequestType());  // LEND or SELL
            stmt.setString(6, request.getStatus());       // Pending
            stmt.setTimestamp(7, new java.sql.Timestamp(request.getRequestDate().getTime()));

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Book request saved successfully: " + request.getRequestID());
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("❌ Error saving book request: " + e.getMessage());
            System.err.println("❌ This usually means the database column doesn't match the code.");
            System.err.println("❌ Please run this SQL to fix: ALTER TABLE book_requests ADD COLUMN request_type VARCHAR(20) AFTER book_id;");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify table structure and print actual columns
     */
    private boolean verifyTableStructure() {
        String sql = "SHOW COLUMNS FROM book_requests";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            System.out.println("\n📋 Actual book_requests table columns:");
            System.out.println("=====================================");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("Field") +
                        " (" + rs.getString("Type") + ")");
            }
            System.out.println("=====================================\n");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error checking table structure: " + e.getMessage());
            return false;
        }
    }
}