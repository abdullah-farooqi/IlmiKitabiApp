package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ComplaintDAO - Data Access for Complaints
 * GRASP: High Cohesion
 * ✅ FIXED: Added updateComplaintStatus method
 */
public class ComplaintDAO {
    private DatabaseConnection dbConnection;

    public ComplaintDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public boolean save(Complaint complaint) {
        String sql = "INSERT INTO complaints (complaint_id, complainant_id, accused_id, " +
                "transaction_id, description, category, filed_date, status, " +
                "resolution, resolved_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, complaint.getComplaintID());
            stmt.setString(2, complaint.getComplainantID());
            stmt.setString(3, complaint.getAccusedID());
            stmt.setString(4, complaint.getTransactionID());
            stmt.setString(5, complaint.getDescription());
            stmt.setString(6, complaint.getCategory());
            stmt.setTimestamp(7, new Timestamp(complaint.getFiledDate().getTime()));
            stmt.setString(8, complaint.getStatus());
            stmt.setString(9, complaint.getResolution());
            stmt.setTimestamp(10, complaint.getResolvedDate() != null ?
                    new Timestamp(complaint.getResolvedDate().getTime()) : null);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving complaint: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ NEW: Update complaint status and resolution
     */
    public boolean updateComplaintStatus(String complaintID, String status, String resolution) {
        String sql = "UPDATE complaints SET status = ?, resolution = ?, resolved_date = NOW() " +
                "WHERE complaint_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, resolution);
            stmt.setString(3, complaintID);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Complaint status updated in database: " + complaintID + " -> " + status);
                return true;
            } else {
                System.err.println("❌ No complaint found with ID: " + complaintID);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating complaint status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Complaint> findByStudent(String studentID) {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE complainant_id = ? OR accused_id = ? " +
                "ORDER BY filed_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            stmt.setString(2, studentID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding complaints: " + e.getMessage());
        }

        return complaints;
    }

    public List<Complaint> findPendingComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints WHERE status = 'Pending' ORDER BY filed_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding pending complaints: " + e.getMessage());
        }

        return complaints;
    }

    /**
     * ✅ NEW: Get all complaints (for admin dashboard stats)
     */
    public List<Complaint> findAll() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY filed_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all complaints: " + e.getMessage());
        }

        return complaints;
    }

    /**
     * ✅ NEW: Get resolved complaints count
     */
    public int countResolvedComplaints() {
        String sql = "SELECT COUNT(*) as count FROM complaints WHERE status = 'Resolved'";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("Error counting resolved complaints: " + e.getMessage());
        }

        return 0;
    }

    private Complaint extractComplaintFromResultSet(ResultSet rs) throws SQLException {
        return new Complaint(
                rs.getString("complaint_id"),
                rs.getString("complainant_id"),
                rs.getString("accused_id"),
                rs.getString("transaction_id"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getTimestamp("filed_date"),
                rs.getString("status"),
                rs.getString("resolution"),
                rs.getTimestamp("resolved_date")
        );
    }
}