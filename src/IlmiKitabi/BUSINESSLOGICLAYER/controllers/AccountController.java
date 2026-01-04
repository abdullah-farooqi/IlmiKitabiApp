package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.services.AuthenticationService;
import IlmiKitabi.DATAACCESSLAYER.StudentDAO;

/**
 * AccountController - Handles account-related system operations
 * GRASP Pattern: Controller - Delegates system operations to appropriate objects
 * Implements Use Case 1 (Create Account) and Use Case 2 (View/Update Info)
 * Follows Sequence Diagrams SD 1 and SD 2
 */
public class AccountController {
    private AuthenticationService authService;
    private StudentDAO studentDAO;

    /**
     * Constructor
     * GRASP: Creator - Controller creates the services it needs
     */
    public AccountController() {
        this.authService = AuthenticationService.getInstance();
        this.studentDAO = new StudentDAO();
    }

    /**
     * Register new student account (Use Case 1: Create Account)
     * Follows SD 1: Create Account sequence diagram
     *
     * @param student Student object with registration details
     * @return true if registration successful, false otherwise
     */
    public boolean registerStudent(Student student) {
        try {
            // SD 1 Step: Validate NU email
            if (!authService.validateNUEmail(student.getNuEmail())) {
                System.out.println("Invalid NU email format");
                return false;
            }

            // SD 1 Step: Check if email already exists
            if (studentDAO.emailExists(student.getNuEmail())) {
                System.out.println("Email already registered");
                return false;
            }

            // SD 1 Step: Hash password before storing
            String hashedPassword = authService.hashPassword(student.getPassword());
            student.setPassword(hashedPassword);

            // SD 1 Step: Validate student data
            if (!student.validate()) {
                System.out.println("Invalid student data");
                return false;
            }

            // SD 1 Step: Save registration request to database
            boolean saved = studentDAO.save(student);

            if (saved) {
                System.out.println("Registration successful - Awaiting admin approval");
                // In full implementation: NotificationService.notifyAdmin()
            }

            return saved;

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Student login (Use Case 2: View/Update Info - Login part)
     * Follows SD 2: View/Update Info sequence diagram
     *
     * @param email Student's NU email
     * @param password Student's password
     * @return Student object if login successful, null otherwise
     */
    public Student loginStudent(String email, String password) {
        try {
            // SD 2 Step: Validate credentials format
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                System.out.println("Email and password are required");
                return null;
            }

            // SD 2 Step: Authenticate using AuthenticationService
            boolean authenticated = authService.authenticateStudent(email, password);

            if (!authenticated) {
                System.out.println("Authentication failed");
                return null;
            }

            // SD 2 Step: Retrieve student data
            Student student = studentDAO.findByEmail(email);

            if (student != null) {
                System.out.println("Login successful for: " + student.getName());
                // In full implementation: Generate and return authentication token
            }

            return student;

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update student profile (Use Case 2: View/Update Info - Update part)
     * Follows SD 2: View/Update Info sequence diagram
     *
     * @param studentID Student's ID
     * @param name Updated name
     * @param phoneNumber Updated phone number
     * @param department Updated department
     * @return true if update successful, false otherwise
     */
    public boolean updateStudentProfile(String studentID, String name,
                                        String phoneNumber, String department) {
        try {
            // SD 2 Step: Retrieve student data
            Student student = studentDAO.findById(studentID);

            if (student == null) {
                System.out.println("Student not found");
                return false;
            }

            // SD 2 Step: Update profile using Information Expert
            student.updateProfile(name, phoneNumber, department);

            // SD 2 Step: Validate updated data
            if (!student.validate()) {
                System.out.println("Invalid profile data");
                return false;
            }

            // SD 2 Step: Save updates to database
            boolean updated = studentDAO.update(student);

            if (updated) {
                System.out.println("Profile updated successfully");
            }

            return updated;

        } catch (Exception e) {
            System.err.println("Profile update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get student profile information
     * GRASP: Controller - Coordinates retrieval of student data
     *
     * @param studentID Student's ID
     * @return Student object with profile data
     */
    public Student getStudentProfile(String studentID) {
        try {
            return studentDAO.findById(studentID);
        } catch (Exception e) {
            System.err.println("Error retrieving profile: " + e.getMessage());
            return null;
        }
    }
}