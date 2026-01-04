package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import java.sql.*;

/**
 * AdminDAO - Data Access Object for Admin
 * GRASP: High Cohesion - All admin database operations
 */
public class AdminDAO {
    private DatabaseConnection dbConnection;

    public AdminDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Find admin by email
     */
    public Admin findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Admin(
                        rs.getString("admin_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_active")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error finding admin: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}