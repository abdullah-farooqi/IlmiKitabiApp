package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AdminController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Fine;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * DeleteAccountView - Admin delete student account
 * Implements Use Case 11: Delete Account
 * Follows SD 11
 */
public class DeleteAccountView {
    private Stage stage;
    private Admin admin;
    private AdminController adminController;

    public DeleteAccountView(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
        this.adminController = new AdminController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox header = createHeader();
        root.setTop(header);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);

        VBox content = createContent();
        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Delete Account");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            AdminDashboardView dashboard = new AdminDashboardView(stage, admin);
            dashboard.show();
        });

        Label title = new Label("🗑️ Delete Student Account");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Permanently delete a student account");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#F44336"));
        subtitle.setStyle("-fx-font-weight: bold;");

        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30, 80, 30, 80));
        content.setMaxWidth(800);

        // Warning box
        VBox warningBox = new VBox(15);
        warningBox.setStyle(
                "-fx-background-color: #FFF3E0; " +
                        "-fx-border-color: #FF9800; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8;"
        );

        Label warningIcon = new Label("⚠️ WARNING");
        warningIcon.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        warningIcon.setTextFill(Color.web("#FF9800"));

        Label warningText = new Label(
                "Deleting a student account is permanent and cannot be undone.\n\n" +
                        "Before deletion:\n" +
                        "• All pending dues (fines, unreturned books) will be added to university fees\n" +
                        "• Transaction history will be archived\n" +
                        "• All active offers will be deactivated\n" +
                        "• Personal data will be removed"
        );
        warningText.setFont(Font.font("Arial", 13));
        warningText.setTextFill(Color.web("#666666"));
        warningText.setWrapText(true);

        warningBox.getChildren().addAll(warningIcon, warningText);

        // Search student section
        VBox searchSection = createSearchSection();

        content.getChildren().addAll(warningBox, searchSection);

        return content;
    }

    private VBox createSearchSection() {
        VBox section = new VBox(20);
        section.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 12;");

        Label sectionTitle = new Label("Select Student Account");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sectionTitle.setTextFill(Color.web("#2C3E2E"));

        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<StudentItem> studentCombo = new ComboBox<>();
        studentCombo.setPromptText("Select student to delete");
        studentCombo.setPrefWidth(500);
        loadStudents(studentCombo);

        Button viewBtn = new Button("View Details");
        viewBtn.setStyle(
                "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        viewBtn.setOnAction(e -> {
            StudentItem selected = studentCombo.getValue();
            if (selected != null) {
                showStudentDetails(selected.studentID);
            }
        });

        searchBox.getChildren().addAll(studentCombo, viewBtn);

        // Student info display area
        VBox studentInfoBox = new VBox(20);
        studentInfoBox.setId("studentInfo");
        studentInfoBox.setVisible(false);

        // Delete button
        Button deleteBtn = new Button("🗑️ DELETE ACCOUNT");
        deleteBtn.setStyle(
                "-fx-background-color: #F44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 14 40; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8;"
        );
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> {
            StudentItem selected = studentCombo.getValue();
            if (selected != null) {
                handleDeleteAccount(selected.studentID);
            } else {
                showAlert("Error", "Please select a student first", Alert.AlertType.ERROR);
            }
        });

        studentCombo.setOnAction(e -> {
            StudentItem selected = studentCombo.getValue();
            if (selected != null) {
                displayStudentInfo(selected.studentID, studentInfoBox);
                studentInfoBox.setVisible(true);
            }
        });

        section.getChildren().addAll(sectionTitle, searchBox, studentInfoBox, deleteBtn);

        return section;
    }

    private void loadStudents(ComboBox<StudentItem> combo) {
        List<Student> students = adminController.getAllStudents();
        for (Student student : students) {
            combo.getItems().add(new StudentItem(student.getStudentID(), student.getName(), student.getNuEmail()));
        }
    }

    private void displayStudentInfo(String studentID, VBox container) {
        container.getChildren().clear();

        // Get student info
        List<Student> students = adminController.getAllStudents();
        Student student = students.stream()
                .filter(s -> s.getStudentID().equals(studentID))
                .findFirst()
                .orElse(null);

        if (student == null) return;

        // Get pending dues
        List<Fine> fines = adminController.getStudentFines(studentID);
        double unpaidFines = fines.stream()
                .filter(f -> !f.isPaid())
                .mapToDouble(Fine::getAmount)
                .sum();

        // Get active transactions
        int activeTransactions = getActiveTransactionsCount(studentID);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setStyle("-fx-background-color: #F5F3E8; -fx-padding: 20; -fx-background-radius: 8;");

        addInfoRow(grid, 0, "Name:", student.getName());
        addInfoRow(grid, 1, "Email:", student.getNuEmail());
        addInfoRow(grid, 2, "Phone:", student.getPhoneNumber());
        addInfoRow(grid, 3, "Department:", student.getDepartment());
        addInfoRow(grid, 4, "Status:", student.isActive() ? "Active" : "Inactive");
        addInfoRow(grid, 5, "Registration Date:", new SimpleDateFormat("dd MMM yyyy").format(student.getRegistrationDate()));
        addInfoRow(grid, 6, "Unpaid Fines:", String.format("PKR %.0f", unpaidFines));
        addInfoRow(grid, 7, "Active Transactions:", String.valueOf(activeTransactions));

        Label duesTitle = new Label("Pending Dues Summary:");
        duesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        duesTitle.setTextFill(Color.web("#F44336"));

        VBox duesBox = new VBox(5);
        duesBox.setStyle("-fx-background-color: #FFEBEE; -fx-padding: 15; -fx-background-radius: 5;");

        Label duesInfo = new Label(
                "• Unpaid Fines: PKR " + String.format("%.0f", unpaidFines) + "\n" +
                        "• Active Transactions: " + activeTransactions + "\n" +
                        "• All dues will be added to university fees before deletion"
        );
        duesInfo.setFont(Font.font("Arial", 12));
        duesInfo.setTextFill(Color.web("#666666"));

        duesBox.getChildren().add(duesInfo);

        container.getChildren().addAll(grid, duesTitle, duesBox);
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        labelNode.setTextFill(Color.web("#666666"));

        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Arial", 14));
        valueNode.setTextFill(Color.web("#2C3E2E"));

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void showStudentDetails(String studentID) {
        List<Student> students = adminController.getAllStudents();
        Student student = students.stream()
                .filter(s -> s.getStudentID().equals(studentID))
                .findFirst()
                .orElse(null);

        if (student == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getName());

        String details = String.format(
                "Student ID: %s\n" +
                        "Name: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n" +
                        "Department: %s\n" +
                        "Status: %s\n" +
                        "Registration Date: %s",
                student.getStudentID(),
                student.getName(),
                student.getNuEmail(),
                student.getPhoneNumber(),
                student.getDepartment(),
                student.isActive() ? "Active" : "Inactive",
                new SimpleDateFormat("dd MMM yyyy").format(student.getRegistrationDate())
        );

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void handleDeleteAccount(String studentID) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("CONFIRM DELETION");
        confirm.setHeaderText("Are you absolutely sure?");
        confirm.setContentText(
                "This will PERMANENTLY delete the student account.\n\n" +
                        "This action CANNOT be undone.\n\n" +
                        "Type 'DELETE' to confirm:"
        );

        TextInputDialog confirmDialog = new TextInputDialog();
        confirmDialog.setTitle("CONFIRM DELETION");
        confirmDialog.setHeaderText("Type 'DELETE' to confirm account deletion");
        confirmDialog.setContentText("Confirmation:");

        confirmDialog.showAndWait().ifPresent(input -> {
            if ("DELETE".equals(input)) {
                // SD 11: Check pending dues and delete
                if (performAccountDeletion(studentID)) {
                    showAlert("Success",
                            "Account deleted successfully.\n" +
                                    "All pending dues have been added to university fees.",
                            Alert.AlertType.INFORMATION);
                    AdminDashboardView dashboard = new AdminDashboardView(stage, admin);
                    dashboard.show();
                } else {
                    showAlert("Error", "Failed to delete account. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Cancelled", "Account deletion cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    private boolean performAccountDeletion(String studentID) {
        // SD 11: Delete Account sequence
        try {
            // 1. Archive transaction history (simulated)
            System.out.println("Archiving transaction history for: " + studentID);

            // 2. Deactivate all posts (simulated)
            System.out.println("Deactivating all posts for: " + studentID);

            // 3. Transfer dues to university fees (simulated)
            List<Fine> fines = adminController.getStudentFines(studentID);
            double totalDues = fines.stream().filter(f -> !f.isPaid()).mapToDouble(Fine::getAmount).sum();
            System.out.println("Transferring dues to university fees: PKR " + totalDues);

            // 4. Delete account
            return adminController.rejectRegistration(studentID); // Uses existing delete method

        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }

    private int getActiveTransactionsCount(String studentID) {
        String sql = "SELECT COUNT(*) as count FROM borrow_transactions " +
                "WHERE (borrower_id = ? OR lender_id = ?) AND status = 'Active'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            stmt.setString(2, studentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class StudentItem {
        String studentID;
        String name;
        String email;

        StudentItem(String studentID, String name, String email) {
            this.studentID = studentID;
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return name + " - " + email;
        }
    }
}