/**
 * ✅ ENHANCED: Track dismissed notifications
 */
package IlmiKitabi.BUSINESSLOGICLAYER.services;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * NotificationService - Handles notifications and alerts
 * GRASP: High Cohesion - All notification logic in one place
 * GRASP: Low Coupling - Centralized notification handling
 * GOF: Singleton Pattern
 */
public class NotificationService {
    private static NotificationService instance;
    private List<String> notificationLog;
    private Set<String> dismissedNotifications; // ✅ NEW: Track dismissed notifications

    private NotificationService() {
        this.notificationLog = new ArrayList<>();
        this.dismissedNotifications = new HashSet<>();
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /**
     * ✅ NEW: Check if notification was dismissed
     */
    public boolean isDismissed(String notificationKey) {
        return dismissedNotifications.contains(notificationKey);
    }

    /**
     * ✅ NEW: Dismiss a notification
     */
    public void dismissNotification(String notificationKey) {
        dismissedNotifications.add(notificationKey);
        System.out.println("🔕 Notification dismissed: " + notificationKey);
    }

    /**
     * ✅ NEW: Clear dismissed notifications
     */
    public void clearDismissed() {
        dismissedNotifications.clear();
    }

    /**
     * Send email notification (simulated)
     */
    public boolean sendEmail(String recipientEmail, String subject, String message) {
        try {
            String notification = String.format(
                    "EMAIL TO: %s | SUBJECT: %s | MESSAGE: %s",
                    recipientEmail, subject, message
            );
            notificationLog.add(notification);
            System.out.println("📧 Email sent to " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send in-app notification (simulated)
     */
    public boolean sendInAppNotification(String userID, String title, String message) {
        try {
            String notification = String.format(
                    "IN-APP TO: %s | TITLE: %s | MESSAGE: %s",
                    userID, title, message
            );
            notificationLog.add(notification);
            System.out.println("🔔 In-app notification sent to " + userID);
            return true;
        } catch (Exception e) {
            System.err.println("In-app notification failed: " + e.getMessage());
            return false;
        }
    }

    // ... (rest of the notification methods remain the same) ...

    /**
     * Get notification log (for debugging)
     */
    public List<String> getNotificationLog() {
        return new ArrayList<>(notificationLog);
    }

    /**
     * Clear notification log
     */
    public void clearLog() {
        notificationLog.clear();
    }
}