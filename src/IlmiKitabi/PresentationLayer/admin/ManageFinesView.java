package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AdminController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
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
 * ManageFinesView - Admin fine management
 * Implements Use Case 10: Impose Fine
 * Follows SD 10
 */
public class ManageFinesView {
    private Stage stage;
    private Admin admin;
    private AdminController adminController;
    private VBox contentContainer;

    public ManageFinesView(Stage stage, Admin admin) {
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

        contentContainer = new VBox(30);
        contentContainer.setPadding(new Insets(30, 40, 30, 40));
        loadFines();

        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Manage Fines");
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

        Button imposeFineBtn = new Button("+ Impose New Fine");
        imposeFineBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold;");
        imposeFineBtn.setOnAction(e -> showImposeFineDialog());

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadFines());

        topBar.getChildren().addAll(backBtn, spacer, imposeFineBtn, refreshBtn);
        HBox.setMargin(refreshBtn, new Insets(0, 0, 0, 10));

        Label title = new Label("💰 Manage Fines");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Impose and manage student fines");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private void loadFines() {
        contentContainer.getChildren().clear();

        List<Fine> fines = adminController.getAllFines();

        // Stats Calculation
        // A fine is considered "Unpaid" only if isPaid is false.
        // When a fine is WAIVED, isPaid becomes TRUE, so it is removed from this count/sum.
        long unpaidCount = fines.stream().filter(f -> !f.isPaid()).count();
        double unpaidAmount = fines.stream().filter(f -> !f.isPaid()).mapToDouble(Fine::getAmount).sum();

        // Total amount of fines ever imposed (regardless of status)
        double totalAmount = fines.stream().mapToDouble(Fine::getAmount).sum();

        // Stats UI
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Total Fines Imposed", String.valueOf(fines.size()), "📊", "#2196F3"),
                createStatCard("Unpaid Count", String.valueOf(unpaidCount), "⚠", "#FF9800"),
                createStatCard("Outstanding Debt", String.format("PKR %.0f", unpaidAmount), "💰", "#F44336"),
                createStatCard("Total Value Imposed", String.format("PKR %.0f", totalAmount), "💵", "#4CAF50")
        );

        Label listTitle = new Label("All Fines (" + fines.size() + ")");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        listTitle.setTextFill(Color.web("#2C3E2E"));

        VBox finesContainer = new VBox(15);

        if (fines.isEmpty()) {
            finesContainer.getChildren().add(createEmptyState());
        } else {
            for (Fine fine : fines) {
                finesContainer.getChildren().add(createFineCard(fine));
            }
        }

        contentContainer.getChildren().addAll(statsBox, listTitle, finesContainer);
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
        card.setPrefSize(320, 130);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(40));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.web("#666666"));

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private HBox createFineCard(Fine fine) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Icon
        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.CENTER);
        Label icon = new Label("💰");
        icon.setFont(Font.font(50));
        iconBox.getChildren().add(icon);

        // Details
        VBox detailsBox = new VBox(8);
        detailsBox.setPrefWidth(400);

        String studentName = getStudentName(fine.getStudentID());
        Label student = new Label("Student: " + studentName);
        student.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        student.setTextFill(Color.web("#2C3E2E"));

        Label reason = new Label("Reason: " + fine.getReason());
        reason.setFont(Font.font("Arial", 13));
        reason.setTextFill(Color.web("#666666"));
        reason.setWrapText(true);
        reason.setMaxWidth(380);

        HBox datesBox = new HBox(20);
        Label imposed = new Label("Imposed: " + new SimpleDateFormat("dd MMM yyyy").format(fine.getImposedDate()));
        imposed.setFont(Font.font("Arial", 11));
        imposed.setTextFill(Color.web("#999999"));

        Label due = new Label("Due: " + new SimpleDateFormat("dd MMM yyyy").format(fine.getDueDate()));
        due.setFont(Font.font("Arial", 11));
        due.setTextFill(Color.web("#999999"));

        datesBox.getChildren().addAll(imposed, due);

        detailsBox.getChildren().addAll(student, reason, datesBox);

        // Amount
        VBox amountBox = new VBox(5);
        amountBox.setAlignment(Pos.CENTER);
        amountBox.setPrefWidth(200);

        Label amount = new Label("PKR " + String.format("%.0f", fine.getAmount()));
        amount.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        amount.setTextFill(Color.web("#F44336"));

        Label fineID = new Label("ID: " + fine.getFineID().substring(0, 12) + "...");
        fineID.setFont(Font.font("Arial", 10));
        fineID.setTextFill(Color.web("#999999"));

        amountBox.getChildren().addAll(amount, fineID);

        // Status and actions
        VBox statusBox = new VBox(10);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(220);

        String statusColor = "#F44336"; // Red for unpaid
        if (fine.getStatus().equalsIgnoreCase("Paid")) statusColor = "#4CAF50"; // Green
        else if (fine.getStatus().equalsIgnoreCase("Waived")) statusColor = "#2196F3"; // Blue

        Label status = new Label(fine.getStatus());
        status.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        status.setTextFill(Color.WHITE);
        status.setStyle(
                "-fx-background-color: " + statusColor + "; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 5;"
        );

        if (!fine.isPaid()) {
            HBox actionsBox = new HBox(10);
            actionsBox.setAlignment(Pos.CENTER);

            // BUTTON: Mark Paid
            Button markPaidBtn = new Button("✓ Mark Paid");
            markPaidBtn.setStyle(
                    "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 15; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-font-size: 11;"
            );
            markPaidBtn.setOnAction(e -> markFineAsPaid(fine));

            // BUTTON: Waive
            Button waiveBtn = new Button("Waive");
            waiveBtn.setStyle(
                    "-fx-background-color: #FF9800; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6 15; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 5; " +
                            "-fx-font-size: 11;"
            );
            waiveBtn.setOnAction(e -> waiveFine(fine));

            actionsBox.getChildren().addAll(markPaidBtn, waiveBtn);
            statusBox.getChildren().addAll(status, actionsBox);
        } else {
            statusBox.getChildren().add(status);
        }

        card.getChildren().addAll(iconBox, detailsBox, amountBox, statusBox);

        return card;
    }

    private void showImposeFineDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Impose Fine");
        dialog.setHeaderText("Impose fine on student");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        ComboBox<StudentItem> studentCombo = new ComboBox<>();
        studentCombo.setPromptText("Select student");
        studentCombo.setPrefWidth(300);
        loadStudents(studentCombo);

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount (PKR)");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter reason for fine");
        reasonArea.setPrefRowCount(4);
        reasonArea.setPrefWidth(300);

        grid.add(new Label("Student:"), 0, 0);
        grid.add(studentCombo, 1, 0);
        grid.add(new Label("Amount (PKR):"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Reason:"), 0, 2);
        grid.add(reasonArea, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    StudentItem selected = studentCombo.getValue();
                    double amount = Double.parseDouble(amountField.getText());
                    String reason = reasonArea.getText();

                    if (selected == null || reason.isEmpty()) {
                        showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
                        return;
                    }

                    if (adminController.imposeFine(selected.studentID, amount, reason)) {
                        showAlert("Success", "Fine imposed successfully", Alert.AlertType.INFORMATION);
                        loadFines();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid amount entered", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void loadStudents(ComboBox<StudentItem> combo) {
        List<IlmiKitabi.BUSINESSLOGICLAYER.models.Student> students = adminController.getAllStudents();
        for (var student : students) {
            if (student.isActive()) {
                combo.getItems().add(new StudentItem(student.getStudentID(), student.getName()));
            }
        }
    }

    private void markFineAsPaid(Fine fine) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Mark Fine as Paid");
        confirm.setHeaderText("Mark this fine as paid?");
        confirm.setContentText("Fine Amount: PKR " + String.format("%.0f", fine.getAmount()));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (adminController.markFineAsPaid(fine.getFineID())) {
                    showAlert("Success", "Fine marked as paid", Alert.AlertType.INFORMATION);
                    loadFines();
                }
            }
        });
    }

    private void waiveFine(Fine fine) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Waive Fine");
        confirm.setHeaderText("Waive this fine?");
        confirm.setContentText("Fine will be waived (removed from student debt).\nAmount: PKR " + String.format("%.0f", fine.getAmount()));

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Issue fix: waiveFine logic ensures isPaid=true so it's removed from debt calculation
                if (adminController.waiveFine(fine.getFineID())) {
                    showAlert("Success", "Fine waived successfully", Alert.AlertType.INFORMATION);
                    loadFines();
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

        Label emptyIcon = new Label("💰");
        emptyIcon.setFont(Font.font(80));

        Label emptyText = new Label("No fines imposed yet");
        emptyText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        emptyText.setTextFill(Color.web("#666666"));

        empty.getChildren().addAll(emptyIcon, emptyText);
        return empty;
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

        StudentItem(String studentID, String name) {
            this.studentID = studentID;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + studentID + ")";
        }
    }
}