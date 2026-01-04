package IlmiKitabi.BUSINESSLOGICLAYER.services;

/**
 * ValidationService - Centralized validation logic
 * GRASP: High Cohesion - All validation logic in one place
 * GRASP: Low Coupling - Independent service
 */
public class ValidationService {
    private static ValidationService instance;

    private ValidationService() {}

    public static ValidationService getInstance() {
        if (instance == null) {
            instance = new ValidationService();
        }
        return instance;
    }

    /**
     * Validate NU email format
     */
    public boolean validateNUEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9]+@nu\\.edu\\.pk$");
    }

    /**
     * Validate password strength
     */
    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Validate phone number format
     */
    public boolean validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Pakistani phone format: 03XX-XXXXXXX or 03XXXXXXXXX
        return phone.matches("^03\\d{2}-?\\d{7}$");
    }

    /**
     * Validate price
     */
    public boolean validatePrice(double price) {
        return price > 0 && price < 100000;
    }

    /**
     * Validate duration (days)
     */
    public boolean validateDuration(int duration) {
        return duration > 0 && duration <= 365;
    }

    /**
     * Validate book title
     */
    public boolean validateBookTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= 200;
    }

    /**
     * Validate text field (not empty)
     */
    public boolean validateRequiredField(String field) {
        return field != null && !field.trim().isEmpty();
    }

    /**
     * Validate amount
     */
    public boolean validateAmount(double amount) {
        return amount >= 0;
    }
}