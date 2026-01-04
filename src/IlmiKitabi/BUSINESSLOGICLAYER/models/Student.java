package IlmiKitabi.BUSINESSLOGICLAYER.models;

import IlmiKitabi.BUSINESSLOGICLAYER.interfaces.IObserver;

import java.util.Date;

/**
 * Student - Domain Model Class
 * GRASP Pattern: Information Expert - Student knows its own data and behavior
 * Represents a student in the Ilmi Kitabi system
 */
public class Student implements IObserver {

    // Attributes from Class Diagram
    private String studentID;
    private String name;
    private String nuEmail;
    private String password;
    private String phoneNumber;
    private String department;
    private Date registrationDate;
    private boolean isActive;  // Requires admin approval

    /**
     * Constructor for new student registration
     */
    public Student(String name, String nuEmail, String password,
                   String phoneNumber, String department) {

        this.studentID = generateStudentID();
        this.name = name;
        this.nuEmail = nuEmail;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.registrationDate = new Date();
        this.isActive = false;  // Pending admin approval
    }

    /**
     * Constructor for loading existing student from database
     */
    public Student(String studentID, String name, String nuEmail, String password,
                   String phoneNumber, String department, Date registrationDate, boolean isActive) {

        this.studentID = studentID;
        this.name = name;
        this.nuEmail = nuEmail;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
    }

    /**
     * Generates unique student ID
     */
    private String generateStudentID() {
        return "STU" + System.currentTimeMillis();
    }

    /**
     * Updates profile information (Use Case 2)
     */
    public void updateProfile(String name, String phoneNumber, String department) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            this.phoneNumber = phoneNumber;
        }
        if (department != null && !department.isEmpty()) {
            this.department = department;
        }
    }

    /**
     * Validates student data
     */
    public boolean validate() {
        return name != null && !name.isEmpty()
                && nuEmail != null && nuEmail.matches("^[a-zA-Z0-9]+@nu\\.edu\\.pk$")
                && password != null && !password.isEmpty()
                && phoneNumber != null && !phoneNumber.isEmpty()
                && department != null && !department.isEmpty();
    }

    // -------------------------------------
    // IObserver Interface Implementations
    // -------------------------------------

    @Override
    public void update(String message) {
        // Implementation later
        // Example: System.out.println("Student Updated: " + message);
    }

    @Override
    public String getID() {
        return studentID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return nuEmail;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public void notifyUser(String notificationMessage) {
        // Implementation later
        // Example: System.out.println("Notification: " + notificationMessage);
    }

    // -------------------------------------
    // Regular Getters & Setters
    // -------------------------------------

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNuEmail() {
        return nuEmail;
    }

    public void setNuEmail(String nuEmail) {
        this.nuEmail = nuEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentID='" + studentID + '\'' +
                ", name='" + name + '\'' +
                ", nuEmail='" + nuEmail + '\'' +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
