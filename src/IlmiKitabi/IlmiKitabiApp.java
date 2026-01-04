package IlmiKitabi;

import IlmiKitabi.PresentationLayer.common.WelcomeView;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * IlmiKitabiApp - Main Application Class
 * Entry point for the Ilmi Kitabi Book Exchange System
 *
 * This is a JavaFX application that implements a complete book exchange platform
 * for FAST NUCES students.
 *
 * Features Implemented:
 * - Welcome Screen (Role Selection: Student/Admin)
 * - Student Login
 *
 * Architecture:
 * - Presentation Layer: JavaFX UI components
 * - Business Logic Layer: Controllers, Services, Models
 * - Data Access Layer: DAO classes and Database Connection
 *
 * Design Patterns Used:
 * - GRASP: Controller, Information Expert, High Cohesion, Low Coupling
 * - GOF: Singleton (DatabaseConnection, AuthenticationService)
 */
public class IlmiKitabiApp extends Application {

    /**
     * Application startup
     * GRASP: Controller - Manages application initialization
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            if (dbConnection.testConnection()) {
                System.out.println("=================================");
                System.out.println("Ilmi Kitabi System Starting...");
                System.out.println("Database: Connected ✓");
                System.out.println("=================================");
            } else {
                System.err.println("Database connection failed!");
                showDatabaseError(primaryStage);
                return;
            }

            // Start with Welcome Screen
            WelcomeView welcomeView = new WelcomeView(primaryStage);
            welcomeView.show();

            // Handle application close
            primaryStage.setOnCloseRequest(e -> {
                System.out.println("Closing Ilmi Kitabi application...");
                dbConnection.closeConnection();
                System.out.println("Application closed successfully");
            });

        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show database connection error
     */
    private void showDatabaseError(Stage stage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Database Error");
        alert.setHeaderText("Cannot Connect to Database");
        alert.setContentText(
                "Please ensure:\n" +
                        "1. MySQL is installed and running\n" +
                        "2. Database credentials are correct in DatabaseConnection.java\n" +
                        "3. MySQL JDBC driver is in the classpath"
        );
        alert.showAndWait();
        System.exit(1);
    }

    /**
     * Main method - Application entry point
     */
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("    ILMI KITABI");
        System.out.println("    Book Exchange Platform");
        System.out.println("    FAST NUCES");
        System.out.println("=================================\n");

        launch(args);
    }
}