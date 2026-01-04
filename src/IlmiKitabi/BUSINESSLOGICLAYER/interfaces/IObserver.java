package IlmiKitabi.BUSINESSLOGICLAYER.interfaces;

/**
 * IObserver - Observer Pattern Interface
 * This interface provides common methods that Student and Admin
 * will later implement as observers.
 */
public interface IObserver {

    // Observer pattern update method
    void update(String message);

    // Common identity-related getters
    String getID();
    String getName();
    String getEmail();

    // Status handling (active/inactive)
    boolean isActive();
    void setActive(boolean status);

    // Optional: Notify-related behavior
    void notifyUser(String notificationMessage);
}
