package IlmiKitabi.BUSINESSLOGICLAYER.models;

import java.util.Date;
import java.util.Calendar;

/**
 * Fine - Fine model
 * GRASP: Information Expert
 * From Class Diagram - Use Case 10: Impose Fine
 */
public class Fine {
    private String fineID;
    private String studentID;
    private double amount;
    private String reason;
    private Date imposedDate;
    private Date dueDate;
    private boolean isPaid;
    private String status;

    public Fine(String studentID, double amount, String reason) {
        this.fineID = generateFineID();
        this.studentID = studentID;
        this.amount = amount;
        this.reason = reason;
        this.imposedDate = new Date();
        this.dueDate = calculateDueDate();
        this.isPaid = false;
        this.status = "Unpaid";
    }

    public Fine(String fineID, String studentID, double amount, String reason,
                Date imposedDate, Date dueDate, boolean isPaid, String status) {
        this.fineID = fineID;
        this.studentID = studentID;
        this.amount = amount;
        this.reason = reason;
        this.imposedDate = imposedDate;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.status = status;
    }

    private String generateFineID() {
        return "FINE" + System.currentTimeMillis();
    }

    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(imposedDate);
        cal.add(Calendar.DAY_OF_MONTH, 30); // 30 days to pay
        return cal.getTime();
    }

    public double calculateAmount(int overdueDays) {
        // Rs. 50 per day overdue
        return overdueDays * 50.0;
    }

    // Getters and Setters
    public String getFineID() { return fineID; }
    public void setFineID(String fineID) { this.fineID = fineID; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Date getImposedDate() { return imposedDate; }
    public void setImposedDate(Date imposedDate) { this.imposedDate = imposedDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}