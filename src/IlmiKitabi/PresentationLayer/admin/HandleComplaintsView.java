package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AdminController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Complaint;
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
 * HandleComplaintsView - Admin complaint management
 * ✅ FIXED: Now properly removes resolved complaints from view
 */
public class HandleComplaintsView {
    private Stage stage;
    private Admin admin;
    private AdminController adminController;
    private VBox contentContainer;

    public HandleComplaintsView(Stage stage, Admin admin) {
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

        contentContainer = new VBox(25);
        contentContainer.setPadding(new Insets(30, 40, 30, 40));
        loadComplaints();

        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Handle Complaints");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2C3E2E; -fx-font-size: 14; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            AdminDashboardView dashboard = new AdminDashboardView(stage, admin);
            dashboard.show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadComplaints());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("⚠️ Handle Complaints");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Review and resolve student complaints");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private void loadComplaints() {
        contentContainer.getChildren().clear();

        List<Complaint> complaints = adminController.getPendingComplaints();
        int resolvedCount = getResolvedComplaintsCount();

        // Stats
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Pending", String.valueOf(complaints.size()), "⚠️", "#FF9800"),
                createStatCard("Resolved", String.valueOf(resolvedCount), "✓", "#4CAF50")
        );

        Label listTitle = new Label("Pending Complaints (" + complaints.size() + ")");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        listTitle.setTextFill(Color.web("#2C3E2E"));

        VBox complaintsContainer = new VBox(15);

        if (complaints.isEmpty()) {
            complaintsContainer.getChildren().add(createEmptyState());
        } else {
            for (Complaint complaint : complaints) {
                complaintsContainer.getChildren().add(createComplaintCard(complaint));
            }
        }

        contentContainer.getChildren().addAll(statsBox, listTitle, complaintsContainer);
    }

    /**
     * ✅ NEW: Get count of resolved complaints
     */
    private int getResolvedComplaintsCount() {
        String sql = "SELECT COUNT(*) as count FROM complaints WHERE status = 'Resolved'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private VBox createStatCard(String title, String value, String icon, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        card.setPrefSize(350, 130);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(40));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.web("#666666"));

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private VBox createComplaintCard(Complaint complaint) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );

        // Header
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("⚠️");
        icon.setFont(Font.font(40));

        VBox infoBox = new VBox(5);
        Label category = new Label(complaint.getCategory());
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        category.setTextFill(Color.web("#2C3E2E"));

        Label date = new Label("Filed: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(complaint.getFiledDate()));
        date.setFont(Font.font("Arial", 12));
        date.setTextFill(Color.web("#999999"));

        infoBox.getChildren().addAll(category, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label complaintID = new Label("ID: " + complaint.getComplaintID().substring(0, Math.min(12, complaint.getComplaintID().length())) + "...");
        complaintID.setFont(Font.font("Arial", 11));
        complaintID.setTextFill(Color.web("#999999"));

        headerBox.getChildren().addAll(icon, infoBox, spacer, complaintID);

        // Parties info
        HBox partiesBox = new HBox(40);

        VBox complainantBox = new VBox(5);
        Label complainantLabel = new Label("Complainant:");
        complainantLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        complainantLabel.setTextFill(Color.web("#666666"));

        String complainantName = getStudentName(complaint.getComplainantID());
        Label complainantValue = new Label(complainantName);
        complainantValue.setFont(Font.font("Arial", 14));
        complainantValue.setTextFill(Color.web("#2C3E2E"));

        complainantBox.getChildren().addAll(complainantLabel, complainantValue);

        VBox accusedBox = new VBox(5);
        Label accusedLabel = new Label("Accused:");
        accusedLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        accusedLabel.setTextFill(Color.web("#666666"));

        String accusedName = getStudentName(complaint.getAccusedID());
        Label accusedValue = new Label(accusedName);
        accusedValue.setFont(Font.font("Arial", 14));
        accusedValue.setTextFill(Color.web("#F44336"));

        accusedBox.getChildren().addAll(accusedLabel, accusedValue);

        partiesBox.getChildren().addAll(complainantBox, accusedBox);

        // Description
        Label descTitle = new Label("Description:");
        descTitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        descTitle.setTextFill(Color.web("#2C3E2E"));

        Label description = new Label(complaint.getDescription());
        description.setFont(Font.font("Arial", 13));
        description.setTextFill(Color.web("#666666"));
        description.setWrapText(true);
        description.setMaxWidth(1200);

        // Actions
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsBtn = new Button("📋 View Details");
        viewDetailsBtn.setStyle(
                "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        viewDetailsBtn.setOnAction(e -> showComplaintDetails(complaint));

        Button imposeFineBtn = new Button("💰 Impose Fine");
        imposeFineBtn.setStyle(
                "-fx-background-color: #FF9800; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        imposeFineBtn.setOnAction(e -> showImposeFineDialog(complaint));

        Button resolveBtn = new Button("✓ Resolve");
        resolveBtn.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        resolveBtn.setOnAction(e -> resolveComplaint(complaint));

        Button dismissBtn = new Button("✗ Dismiss");
        dismissBtn.setStyle(
                "-fx-background-color: #F44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5;"
        );
        dismissBtn.setOnAction(e -> dismissComplaint(complaint));

        actionsBox.getChildren().addAll(viewDetailsBtn, imposeFineBtn, resolveBtn, dismissBtn);

        card.getChildren().addAll(headerBox, partiesBox, descTitle, description, actionsBox);

        return card;
    }

    private void showComplaintDetails(Complaint complaint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Complaint Details");
        alert.setHeaderText("Complaint ID: " + complaint.getComplaintID());

        String details =
                "Category: " + complaint.getCategory() + "\n" +
                        "Complainant: " + getStudentName(complaint.getComplainantID()) + "\n" +
                        "Accused: " + getStudentName(complaint.getAccusedID()) + "\n" +
                        "Filed Date: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(complaint.getFiledDate()) + "\n" +
                        "Transaction ID: " + (complaint.getTransactionID() != null ? complaint.getTransactionID() : "N/A") + "\n\n" +
                        "Description:\n" + complaint.getDescription();

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showImposeFineDialog(Complaint complaint) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Impose Fine");
        dialog.setHeaderText("Impose fine on " + getStudentName(complaint.getAccusedID()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount (PKR)");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for fine");
        reasonArea.setPrefRowCount(3);

        grid.add(new Label("Amount (PKR):"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Reason:"), 0, 1);
        grid.add(reasonArea, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String reason = reasonArea.getText();

                    if (adminController.imposeFine(complaint.getAccusedID(), amount, reason)) {
                        showAlert("Fine Imposed", "Fine successfully imposed on student", Alert.AlertType.INFORMATION);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid amount entered", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void resolveComplaint(Complaint complaint) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Resolve Complaint");
        dialog.setHeaderText("Resolve complaint");
        dialog.setContentText("Resolution notes:");

        dialog.showAndWait().ifPresent(resolution -> {
            if (resolution != null && !resolution.trim().isEmpty()) {
                if (adminController.resolveComplaint(complaint.getComplaintID(), resolution)) {
                    showAlert("Success", "Complaint resolved successfully", Alert.AlertType.INFORMATION);
                    loadComplaints(); // ✅ Reload to remove from list
                } else {
                    showAlert("Error", "Failed to resolve complaint", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Please enter resolution notes", Alert.AlertType.ERROR);
            }
        });
    }

    private void dismissComplaint(Complaint complaint) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Dismiss Complaint");
        confirm.setHeaderText("Dismiss this complaint?");
        confirm.setContentText("This action will mark the complaint as invalid/dismissed.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (adminController.resolveComplaint(complaint.getComplaintID(), "Dismissed - Invalid complaint")) {
                    showAlert("Success", "Complaint dismissed", Alert.AlertType.INFORMATION);
                    loadComplaints(); // ✅ Reload to remove from list
                } else {
                    showAlert("Error", "Failed to dismiss complaint", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private String getStudentName(String studentID) {
        String sql = "SELECT name FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private VBox createEmptyState() {
        VBox empty = new VBox(20);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        empty.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label emptyIcon = new Label("✓");
        emptyIcon.setFont(Font.font(80));
        emptyIcon.setTextFill(Color.web("#4CAF50"));

        Label emptyText = new Label("No pending complaints");
        emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        emptyText.setTextFill(Color.web("#666666"));

        Label emptyHint = new Label("All complaints have been handled");
        emptyHint.setFont(Font.font("Arial", 14));
        emptyHint.setTextFill(Color.web("#999999"));

        empty.getChildren().addAll(emptyIcon, emptyText, emptyHint);
        return empty;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}