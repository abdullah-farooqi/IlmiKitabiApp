package IlmiKitabi.BUSINESSLOGICLAYER.services;

import IlmiKitabi.BUSINESSLOGICLAYER.interfaces.IAuthenticator;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.DATAACCESSLAYER.AdminDAO;
import IlmiKitabi.DATAACCESSLAYER.StudentDAO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AuthenticationService - Handles authentication and security operations
 * GRASP Pattern: High Cohesion - All authentication logic in one place
 * GRASP Pattern: Low Coupling - Independent service
 * GOF Pattern: Singleton - Only one instance needed
 * Implements IAuthenticator interface from Class Diagram
 */
public class AuthenticationService implements IAuthenticator {
    private static AuthenticationService instance;
    private StudentDAO studentDAO;
    private AdminDAO adminDAO;

    /**
     * Private constructor for Singleton pattern
     */
    private AuthenticationService() {
        this.studentDAO = new StudentDAO();
        this.adminDAO = new AdminDAO();
    }

    /**
     * Get singleton instance
     * GOF: Singleton Pattern
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    // ============================================================
    // 🧑‍🎓 STUDENT AUTHENTICATION
    // ============================================================

    /**
     * Authenticate student with email and password
     * Follows SD 2: View/Update Info (authentication step)
     *
     * @param email    Student's NU email
     * @param password Student's password (plain text)
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateStudent(String email, String password) {
        try {
            // Validate email format
            if (!validateNUEmail(email)) {
                System.out.println("❌ Invalid NU email format");
                return false;
            }

            // Retrieve student from database
            Student student = studentDAO.findByEmail(email);
            if (student == null) {
                System.out.println("❌ Student not found");
                return false;
            }

            // Compare hashed password
            String hashedPassword = hashPassword(password);
            if (!student.getPassword().equals(hashedPassword)) {
                System.out.println("❌ Incorrect password");
                return false;
            }

            // Check account activation
            if (!student.isActive()) {
                System.out.println("⚠️ Account pending admin approval");
                return false;
            }

            System.out.println("✅ Student authentication successful");
            return true;

        } catch (Exception e) {
            System.err.println("Error authenticating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // 👨‍💼 ADMIN AUTHENTICATION
    // ============================================================

    /**
     * Authenticate admin with email and password
     * Similar logic to student authentication
     *
     * @param email    Admin's email
     * @param password Admin's password (plain text)
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateAdmin(String email, String password) {
        try {
            // Basic email format check
            if (email == null || !email.contains("@")) {
                System.out.println("❌ Invalid admin email format");
                return false;
            }

            // Retrieve admin from database
            Admin admin = adminDAO.findByEmail(email);
            if (admin == null) {
                System.out.println("❌ Admin not found");
                return false;
            }

            // Compare hashed password
            String hashedPassword = hashPassword(password);
            if (!admin.getPassword().equals(hashedPassword)) {
                System.out.println("❌ Incorrect admin password");
                return false;
            }

            // Check account activation
            if (!admin.isActive()) {
                System.out.println("⚠️ Admin account is not active");
                return false;
            }

            System.out.println("✅ Admin authentication successful");
            return true;

        } catch (Exception e) {
            System.err.println("Error authenticating admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // 🔐 UTILITY METHODS
    // ============================================================

    /**
     * Validate NU email format
     *
     * @param email Email to validate
     * @return true if valid NU email, false otherwise
     */
    public boolean validateNUEmail(String email) {
        if (email == null || email.isEmpty()) return false;

        // NU email format: [letters/numbers]@nu.edu.pk
        String pattern = "^[a-zA-Z0-9]+@nu\\.edu\\.pk$";
        return email.matches(pattern);
    }

    /**
     * Hash password using SHA-256
     *
     * @param password Plain text password
     * @return Hashed password (hexadecimal)
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return Integer.toString(password.hashCode());
        }
    }

    /**
     * Validate password strength
     *
     * @param password Password to validate
     * @return true if strong, false otherwise
     */
    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Generate simple authentication token (future use)
     */
    public String generateToken(String userID) {
        String tokenData = userID + System.currentTimeMillis();
        return hashPassword(tokenData);
    }

    /**
     * Verify token validity (placeholder)
     */
    public boolean verifyToken(String token) {
        return token != null && !token.isEmpty();
    }
}
