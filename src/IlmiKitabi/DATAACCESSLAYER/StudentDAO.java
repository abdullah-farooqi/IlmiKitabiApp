package IlmiKitabi.DATAACCESSLAYER;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - Data Access Object for Student entity
 * GRASP Pattern: High Cohesion - All Student database operations in one class
 * GRASP Pattern: Information Expert - Knows how to access Student data
 * Handles all CRUD operations for students table
 */
public class StudentDAO {
    private DatabaseConnection dbConnection;

    /**
     * Constructor
     */
    public StudentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Save new student to database (Use Case 1: Create Account)
     * Follows SD 1: Create Account sequence diagram
     *
     * @param student Student object to save
     * @return true if save successful, false otherwise
     */
    public boolean save(Student student) {
        String sql = "INSERT INTO students (student_id, name, nu_email, password, " +
                "phone_number, department, registration_date, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getNuEmail());
            stmt.setString(4, student.getPassword());
            stmt.setString(5, student.getPhoneNumber());
            stmt.setString(6, student.getDepartment());
            stmt.setTimestamp(7, new Timestamp(student.getRegistrationDate().getTime()));
            stmt.setBoolean(8, student.isActive());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student saved successfully: " + student.getStudentID());
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error saving student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find student by email (Use Case 2: View/Update Info - Login)
     * Follows SD 2: View/Update Info sequence diagram
     *
     * @param email Student's NU email
     * @return Student object if found, null otherwise
     */
    public Student findByEmail(String email) {
        String sql = "SELECT * FROM students WHERE nu_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding student by email: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find student by ID
     *
     * @param studentID Student's ID
     * @return Student object if found, null otherwise
     */
    public Student findById(String studentID) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding student by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update student profile (Use Case 2: View/Update Info - Update)
     * Follows SD 2: View/Update Info sequence diagram
     *
     * @param student Student object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Student student) {
        String sql = "UPDATE students SET name = ?, phone_number = ?, department = ? " +
                "WHERE student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getPhoneNumber());
            stmt.setString(3, student.getDepartment());
            stmt.setString(4, student.getStudentID());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student updated successfully: " + student.getStudentID());
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if email already exists
     * Used during registration to prevent duplicates
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM students WHERE nu_email = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update student active status (for admin approval)
     * Used in Use Case 1: Create Account (admin approval step)
     *
     * @param studentID Student's ID
     * @param isActive Active status
     * @return true if update successful, false otherwise
     */
    public boolean updateActiveStatus(String studentID, boolean isActive) {
        String sql = "UPDATE students SET is_active = ? WHERE student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isActive);
            stmt.setString(2, studentID);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student active status updated: " + studentID);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error updating active status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all students (for admin view)
     *
     * @return List of all students
     */
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY registration_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    /**
     * Get all pending registrations (awaiting admin approval)
     * Used in Use Case 10: Admin management
     *
     * @return List of students awaiting approval
     */
    public List<Student> findPendingApprovals() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE is_active = FALSE ORDER BY registration_date DESC";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving pending approvals: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }

    /**
     * Delete student account (Use Case 11: Delete Account)
     *
     * @param studentID Student's ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(String studentID) {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully: " + studentID);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract Student object from ResultSet
     * GRASP: Information Expert - DAO knows how to construct Student from database data
     *
     * @param rs ResultSet from query
     * @return Student object
     * @throws SQLException if data extraction fails
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("nu_email"),
                rs.getString("password"),
                rs.getString("phone_number"),
                rs.getString("department"),
                rs.getTimestamp("registration_date"),
                rs.getBoolean("is_active")
        );
    }
}