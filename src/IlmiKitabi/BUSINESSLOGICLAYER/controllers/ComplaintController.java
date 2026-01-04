package IlmiKitabi.BUSINESSLOGICLAYER.controllers;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Complaint;
import IlmiKitabi.DATAACCESSLAYER.ComplaintDAO;
import java.util.List;

/**
 * ComplaintController - Handles complaint operations
 * GRASP: Controller
 * Implements Use Case 12: Handle Complaint
 * Follows SD 12
 */
public class ComplaintController {
    private ComplaintDAO complaintDAO;

    public ComplaintController() {
        this.complaintDAO = new ComplaintDAO();
    }

    /**
     * File a complaint (SD 12: File Complaint)
     */
    public boolean fileComplaint(String complainantID, String accusedID, String transactionID,
                                 String description, String category) {
        try {
            Complaint complaint = new Complaint(complainantID, accusedID, transactionID,
                    description, category);

            if (!complaint.validate()) {
                System.out.println("Invalid complaint data");
                return false;
            }

            if (complaintDAO.save(complaint)) {
                System.out.println("Complaint filed: " + complaint.getComplaintID());
                // In full implementation: NotificationService.notifyAdmin()
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error filing complaint: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get complaints for a student
     */
    public List<Complaint> getStudentComplaints(String studentID) {
        try {
            return complaintDAO.findByStudent(studentID);
        } catch (Exception e) {
            System.err.println("Error retrieving complaints: " + e.getMessage());
            return List.of();
        }
    }
}