package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookDAO - Data Access Object for Book
 * GRASP: High Cohesion
 */
public class BookDAO {
    private DatabaseConnection dbConnection;

    public BookDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    /**
     * ✅ FIXED: Save book with owner and offer type in one step
     */
    public boolean save(Book book) {
        String sql = "INSERT INTO books (book_id, title, author, isbn, subject, " +
                "description, image_url, book_condition, owner_id, offer_type, is_available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getBookID());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getIsbn());
            stmt.setString(5, book.getSubject());
            stmt.setString(6, book.getDescription());
            stmt.setString(7, book.getImageURL());
            stmt.setString(8, book.getCondition());

            // ✅ NEW: Set owner_id and offer_type from Book object
            stmt.setString(9, book.getOwnerID());
            stmt.setString(10, book.getOfferType());
            stmt.setBoolean(11, book.isAvailable());

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Book saved successfully: " + book.getBookID());
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error saving book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Book findById(String bookID) {
        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding book: " + e.getMessage());
        }

        return null;
    }

    public List<Book> searchBooks(String keyword, String subject, String condition) {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT b.*, s.name as owner_name, " +
                        "CASE " +
                        "  WHEN lo.offer_id IS NOT NULL THEN 'LEND' " +
                        "  WHEN so.offer_id IS NOT NULL THEN 'SELL' " +
                        "  ELSE 'UNKNOWN' " +
                        "END as offer_type, " +
                        "COALESCE(so.price, 0) as price " +
                        "FROM books b " +
                        "LEFT JOIN lending_offers lo ON b.book_id = lo.book_id AND lo.status = 'Available' " +
                        "LEFT JOIN selling_offers so ON b.book_id = so.book_id AND so.status = 'Available' " +
                        "LEFT JOIN students s ON COALESCE(lo.student_id, so.student_id) = s.student_id " +
                        "WHERE b.is_available = TRUE "
        );

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (b.title LIKE ? OR b.author LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (subject != null && !subject.equals("All")) {
            sql.append("AND b.subject = ? ");
            params.add(subject);
        }

        if (condition != null && !condition.equals("All")) {
            sql.append("AND b.book_condition = ? ");
            params.add(condition);
        }

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                book.setOfferType(rs.getString("offer_type") != null ? rs.getString("offer_type") : "UNKNOWN");
                book.setOwnerName(rs.getString("owner_name") != null ? rs.getString("owner_name") : "Unknown Owner");
                book.setPrice(rs.getDouble("price"));
                books.add(book);
            }

        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }

        return books;
    }

    /**
     * Count total active books in the system (for admin dashboard)
     */
    public int countActiveBooks() {
        String sql = "SELECT COUNT(*) FROM books WHERE is_available = TRUE";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting active books: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get total books count (all books)
     */
    public int countTotalBooks() {
        String sql = "SELECT COUNT(*) FROM books";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting total books: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getString("subject"),
                rs.getString("description"),
                rs.getString("image_url"),
                rs.getString("book_condition"),
                rs.getBoolean("is_available")
        );
    }

    /**
     * ✅ NEW: Update book owner and offer type
     */
    public boolean updateOwnerAndType(String bookID, String ownerID, String offerType) {
        String sql = "UPDATE books SET owner_id = ?, offer_type = ? WHERE book_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerID);
            stmt.setString(2, offerType);
            stmt.setString(3, bookID);

            int result = stmt.executeUpdate();

            if (result > 0) {
                System.out.println("✅ Book owner and type updated: " + bookID + " -> Owner: " + ownerID + ", Type: " + offerType);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error updating book owner and type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}