package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Complaint;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Fine;
import IlmiKitabi.BUSINESSLOGICLAYER.services.AuthenticationService;
import IlmiKitabi.DATAACCESSLAYER.AdminDAO;
import IlmiKitabi.DATAACCESSLAYER.StudentDAO;
import IlmiKitabi.DATAACCESSLAYER.ComplaintDAO;
import IlmiKitabi.DATAACCESSLAYER.FineDAO;
import java.util.List;

/**
 * AdminController - Handles admin operations
 * GRASP: Controller - Coordinates admin use cases
 * Implements Use Cases 1, 10, 11, 12
 * ✅ FIXED: Now properly resolves complaints in database
 */
public class AdminController {
    private AuthenticationService authService;
    private AdminDAO adminDAO;
    private StudentDAO studentDAO;
    private ComplaintDAO complaintDAO;
    private FineDAO fineDAO;

    public AdminController() {
        this.authService = AuthenticationService.getInstance();
        this.adminDAO = new AdminDAO();
        this.studentDAO = new StudentDAO();
        this.complaintDAO = new ComplaintDAO();
        this.fineDAO = new FineDAO();
    }

    /**
     * Admin login
     */
    public Admin loginAdmin(String email, String password) {
        try {
            if (authService.authenticateAdmin(email, password)) {
                return adminDAO.findByEmail(email);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Admin login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get pending student registrations (SD 1: Admin review step)
     */
    public List<Student> getPendingRegistrations() {
        return studentDAO.findPendingApprovals();
    }

    /**
     * Approve student registration (SD 1: Admin approval)
     */
    public boolean approveRegistration(String studentID) {
        try {
            boolean updated = studentDAO.updateActiveStatus(studentID, true);
            if (updated) {
                System.out.println("Student approved: " + studentID);
                // In full implementation: NotificationService.notifyStudent()
            }
            return updated;
        } catch (Exception e) {
            System.err.println("Approval error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reject student registration
     */
    public boolean rejectRegistration(String studentID) {
        try {
            return studentDAO.delete(studentID);
        } catch (Exception e) {
            System.err.println("Rejection error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Get pending complaints (SD 12: Admin reviews)
     */
    public List<Complaint> getPendingComplaints() {
        return complaintDAO.findPendingComplaints();
    }

    /**
     * ✅ FIXED: Resolve complaint (SD 12: Admin decides outcome)
     * Now properly updates the database
     */
    public boolean resolveComplaint(String complaintID, String resolution) {
        try {
            boolean updated = complaintDAO.updateComplaintStatus(complaintID, "Resolved", resolution);
            if (updated) {
                System.out.println("✅ Complaint resolved: " + complaintID);
                // In full implementation: NotificationService.notifyStudent()
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error resolving complaint: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Impose fine (SD 10: Impose Fine)
     */
    public boolean imposeFine(String studentID, double amount, String reason) {
        try {
            Fine fine = new Fine(studentID, amount, reason);

            if (fineDAO.save(fine)) {
                System.out.println("Fine imposed: " + fine.getFineID());
                // In full implementation: NotificationService.notifyStudent()
                return true;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error imposing fine: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all fines
     */
    public List<Fine> getAllFines() {
        return fineDAO.findAll();
    }

    /**
     * Get student fines
     */
    public List<Fine> getStudentFines(String studentID) {
        return fineDAO.findByStudent(studentID);
    }

    /**
     * Mark fine as paid
     */
    public boolean markFineAsPaid(String fineID) {
        try {
            return fineDAO.updateStatus(fineID, true, "Paid");
        } catch (Exception e) {
            System.err.println("Error marking fine as paid: " + e.getMessage());
            return false;
        }
    }

    /**
     * Waive fine
     */
    public boolean waiveFine(String fineID) {
        try {
            return fineDAO.updateStatus(fineID, true, "Waived");
        } catch (Exception e) {
            System.err.println("Error waiving fine: " + e.getMessage());
            return false;
        }
    }
}