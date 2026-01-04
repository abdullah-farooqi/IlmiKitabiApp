package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Fine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FineDAO - Data Access for Fines
 * GRASP: High Cohesion
 */
public class FineDAO {
    private DatabaseConnection dbConnection;

    public FineDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean save(Fine fine) {
        String sql = "INSERT INTO fines (fine_id, student_id, amount, reason, " +
                "imposed_date, due_date, is_paid, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fine.getFineID());
            stmt.setString(2, fine.getStudentID());
            stmt.setDouble(3, fine.getAmount());
            stmt.setString(4, fine.getReason());
            stmt.setTimestamp(5, new Timestamp(fine.getImposedDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(fine.getDueDate().getTime()));
            stmt.setBoolean(7, fine.isPaid());
            stmt.setString(8, fine.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving fine: " + e.getMessage());
            return false;
        }
    }

    public List<Fine> findByStudent(String studentID) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines WHERE student_id = ? ORDER BY imposed_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                fines.add(extractFineFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding fines: " + e.getMessage());
        }

        return fines;
    }

    public List<Fine> findAll() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines ORDER BY imposed_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fines.add(extractFineFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all fines: " + e.getMessage());
        }

        return fines;
    }

    public List<Fine> findUnpaidFines(String studentID) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines WHERE student_id = ? AND is_paid = FALSE";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                fines.add(extractFineFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding unpaid fines: " + e.getMessage());
        }

        return fines;
    }

    public boolean updateStatus(String fineID, boolean isPaid, String status) {
        String sql = "UPDATE fines SET is_paid = ?, status = ? WHERE fine_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isPaid);
            stmt.setString(2, status);
            stmt.setString(3, fineID);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating fine status: " + e.getMessage());
            return false;
        }
    }

    private Fine extractFineFromResultSet(ResultSet rs) throws SQLException {
        return new Fine(
                rs.getString("fine_id"),
                rs.getString("student_id"),
                rs.getDouble("amount"),
                rs.getString("reason"),
                rs.getTimestamp("imposed_date"),
                rs.getTimestamp("due_date"),
                rs.getBoolean("is_paid"),
                rs.getString("status")
        );
    }
}