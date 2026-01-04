package IlmiKitabi.BUSINESSLOGICLAYER.models;

import IlmiKitabi.BUSINESSLOGICLAYER.interfaces.IObserver;

/**
 * Admin - Domain Model Class
 * GRASP: Information Expert - Admin knows its own data
 */
public class Admin implements IObserver {

    private String adminID;
    private String name;
    private String email;
    private String password;
    private boolean isActive;

    public Admin(String adminID, String name, String email, String password, boolean isActive) {
        this.adminID = adminID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    // ------------------------
    // Interface Implementation
    // ------------------------

    @Override
    public void update(String message) {
        // Will be implemented later
    }

    @Override
    public String getID() {
        return adminID;
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
        // Will be implemented later
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    // ------------------------
    // Regular Getters & Setters
    // ------------------------

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
