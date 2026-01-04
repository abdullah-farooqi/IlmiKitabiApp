package IlmiKitabi.BUSINESSLOGICLAYER.interfaces;

public interface IAuthenticator {

    // Student Authentication
    boolean authenticateStudent(String email, String password);

    // Admin Authentication
    boolean authenticateAdmin(String email, String password);

    // Utility Methods
    boolean validateNUEmail(String email);

    String hashPassword(String password);

    boolean validatePasswordStrength(String password);

    String generateToken(String userID);

    boolean verifyToken(String token);
}
