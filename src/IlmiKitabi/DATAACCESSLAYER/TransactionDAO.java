package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAO - Data Access for Transactions
 * GRASP: High Cohesion
 */
public class TransactionDAO {
    private DatabaseConnection dbConnection;

    public TransactionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean saveBorrowTransaction(BorrowTransaction transaction) {
        String sql = "INSERT INTO borrow_transactions (transaction_id, borrower_id, lender_id, " +
                "book_id, borrow_date, due_date, return_date, is_returned, is_overdue, " +
                "status, meeting_details) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, transaction.getBorrowerID());
            stmt.setString(3, transaction.getLenderID());
            stmt.setString(4, transaction.getBookID());
            stmt.setTimestamp(5, new Timestamp(transaction.getBorrowDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(transaction.getDueDate().getTime()));
            stmt.setTimestamp(7, transaction.getReturnDate() != null ?
                    new Timestamp(transaction.getReturnDate().getTime()) : null);
            stmt.setBoolean(8, transaction.isReturned());
            stmt.setBoolean(9, transaction.isOverdue());
            stmt.setString(10, transaction.getStatus());
            stmt.setString(11, transaction.getMeetingDetails());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving borrow transaction: " + e.getMessage());
            return false;
        }
    }

    public boolean savePurchaseTransaction(PurchaseTransaction transaction) {
        String sql = "INSERT INTO purchase_transactions (transaction_id, buyer_id, seller_id, " +
                "book_id, amount, transaction_date, status, payment_method, meeting_details) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, transaction.getBuyerID());
            stmt.setString(3, transaction.getSellerID());
            stmt.setString(4, transaction.getBookID());
            stmt.setDouble(5, transaction.getAmount());
            stmt.setTimestamp(6, new Timestamp(transaction.getTransactionDate().getTime()));
            stmt.setString(7, transaction.getStatus());
            stmt.setString(8, transaction.getPaymentMethod());
            stmt.setString(9, transaction.getMeetingDetails());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving purchase transaction: " + e.getMessage());
            return false;
        }
    }

    public boolean saveExchangeTransaction(ExchangeTransaction transaction) {
        String sql = "INSERT INTO exchange_transactions (transaction_id, student1_id, student2_id, " +
                "book1_id, book2_id, exchange_date, status, is_completed, meeting_details) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, transaction.getStudent1ID());
            stmt.setString(3, transaction.getStudent2ID());
            stmt.setString(4, transaction.getBook1ID());
            stmt.setString(5, transaction.getBook2ID());
            stmt.setTimestamp(6, new Timestamp(transaction.getExchangeDate().getTime()));
            stmt.setString(7, transaction.getStatus());
            stmt.setBoolean(8, transaction.isCompleted());
            stmt.setString(9, transaction.getMeetingDetails());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving exchange transaction: " + e.getMessage());
            return false;
        }
    }

    public List<TransactionHistory> getStudentTransactions(String studentID) {
        List<TransactionHistory> transactions = new ArrayList<>();

        // Get borrow transactions (as borrower)
        String borrowQuery =
                "SELECT bt.transaction_id, bt.borrower_id, b.title, b.author, s.name as other_party, " +
                        "'BORROW' as type, bt.borrow_date AS transaction_date, bt.due_date, bt.status, NULL as amount, bt.is_overdue " +
                        "FROM borrow_transactions bt " +
                        "JOIN books b ON bt.book_id = b.book_id " +
                        "JOIN students s ON bt.lender_id = s.student_id " +
                        "WHERE bt.borrower_id = ? " +
                        "UNION ALL " +
                        "SELECT bt.transaction_id, bt.lender_id, b.title, b.author, s.name as other_party, " +
                        "'LEND' as type, bt.borrow_date AS transaction_date, bt.due_date, bt.status, NULL as amount, bt.is_overdue " +
                        "FROM borrow_transactions bt " +
                        "JOIN books b ON bt.book_id = b.book_id " +
                        "JOIN students s ON bt.borrower_id = s.student_id " +
                        "WHERE bt.lender_id = ? " +
                        "UNION ALL " +
                        "SELECT pt.transaction_id, pt.buyer_id, b.title, b.author, s.name as other_party, " +
                        "'BUY' as type, pt.transaction_date AS transaction_date, NULL as due_date, pt.status, pt.amount, FALSE as is_overdue " +
                        "FROM purchase_transactions pt " +
                        "JOIN books b ON pt.book_id = b.book_id " +
                        "JOIN students s ON pt.seller_id = s.student_id " +
                        "WHERE pt.buyer_id = ? " +
                        "UNION ALL " +
                        "SELECT pt.transaction_id, pt.seller_id, b.title, b.author, s.name as other_party, " +
                        "'SELL' as type, pt.transaction_date AS transaction_date, NULL as due_date, pt.status, pt.amount, FALSE as is_overdue " +
                        "FROM purchase_transactions pt " +
                        "JOIN books b ON pt.book_id = b.book_id " +
                        "JOIN students s ON pt.buyer_id = s.student_id " +
                        "WHERE pt.seller_id = ? " +
                        "UNION ALL " +
                        "SELECT et.transaction_id, et.student1_id, CONCAT(b1.title, ' ↔ ', b2.title) as title, " +
                        "CONCAT(b1.author, ' ↔ ', b2.author) as author, s2.name as other_party, " +
                        "'EXCHANGE' as type, et.exchange_date AS transaction_date, NULL as due_date, et.status, NULL as amount, FALSE as is_overdue " +
                        "FROM exchange_transactions et " +
                        "JOIN books b1 ON et.book1_id = b1.book_id " +
                        "JOIN books b2 ON et.book2_id = b2.book_id " +
                        "JOIN students s2 ON et.student2_id = s2.student_id " +
                        "WHERE et.student1_id = ? " +
                        "UNION ALL " +
                        "SELECT et.transaction_id, et.student2_id, CONCAT(b1.title, ' ↔ ', b2.title) as title, " +
                        "CONCAT(b1.author, ' ↔ ', b2.author) as author, s1.name as other_party, " +
                        "'EXCHANGE' as type, et.exchange_date AS transaction_date, NULL as due_date, et.status, NULL as amount, FALSE as is_overdue " +
                        "FROM exchange_transactions et " +
                        "JOIN books b1 ON et.book1_id = b1.book_id " +
                        "JOIN books b2 ON et.book2_id = b2.book_id " +
                        "JOIN students s1 ON et.student1_id = s1.student_id " +
                        "WHERE et.student2_id = ? " +
                        "ORDER BY transaction_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(borrowQuery)) {

            stmt.setString(1, studentID);
            stmt.setString(2, studentID);
            stmt.setString(3, studentID);
            stmt.setString(4, studentID);
            stmt.setString(5, studentID);
            stmt.setString(6, studentID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TransactionHistory history = new TransactionHistory(
                        rs.getString("transaction_id"),
                        rs.getString("borrower_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("other_party"),
                        rs.getString("type"),
                        rs.getTimestamp("transaction_date"),
                        rs.getTimestamp("due_date"),
                        rs.getString("status"),
                        rs.getObject("amount") != null ? rs.getDouble("amount") : null,
                        rs.getBoolean("is_overdue")
                );
                transactions.add(history);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    public List<BorrowTransaction> getActiveBorrowTransactions(String studentID) {
        List<BorrowTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM borrow_transactions " +
                "WHERE (borrower_id = ? OR lender_id = ?) AND status = 'Active'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            stmt.setString(2, studentID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                BorrowTransaction transaction = new BorrowTransaction(
                        rs.getString("transaction_id"),
                        rs.getString("borrower_id"),
                        rs.getString("lender_id"),
                        rs.getString("book_id"),
                        rs.getTimestamp("borrow_date"),
                        rs.getTimestamp("due_date"),
                        rs.getTimestamp("return_date"),
                        rs.getBoolean("is_returned"),
                        rs.getBoolean("is_overdue"),
                        rs.getString("status"),
                        rs.getString("meeting_details")
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving active transactions: " + e.getMessage());
        }

        return transactions;
    }
}