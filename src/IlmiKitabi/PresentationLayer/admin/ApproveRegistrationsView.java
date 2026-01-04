package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.AdminController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ApproveRegistrationsView - Approve pending student registrations
 * Implements Use Case 1: Create Account (Admin approval step)
 * Follows SD 1 sequence diagram
 */
public class ApproveRegistrationsView {
    private Stage stage;
    private Admin admin;
    private AdminController adminController;
    private TableView<Student> table;

    public ApproveRegistrationsView(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
        this.adminController = new AdminController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox header = createHeader();
        root.setTop(header);

        VBox content = createContent();
        root.setCenter(content);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Approve Registrations");
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(20);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setStyle("-fx-background-color: white;");

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
        refreshBtn.setOnAction(e -> loadPendingRegistrations());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("Pending Registrations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Review and approve student registration requests");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));

        // Create table
        table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Student, String> emailCol = new TableColumn<>("NU Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("nuEmail"));
        emailCol.setPrefWidth(220);

        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(140);

        TableColumn<Student, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        deptCol.setPrefWidth(200);

        TableColumn<Student, String> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            return new javafx.beans.property.SimpleStringProperty(
                    sdf.format(cellData.getValue().getRegistrationDate())
            );
        });
        dateCol.setPrefWidth(150);

        TableColumn<Student, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("✓ Approve");
            private final Button rejectBtn = new Button("✗ Reject");

            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-padding: 5 15; -fx-cursor: hand;");

                approveBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    handleApprove(student);
                });

                rejectBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    handleReject(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, approveBtn, rejectBtn);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(nameCol, emailCol, phoneCol, deptCol, dateCol, actionCol);

        // Load data
        loadPendingRegistrations();

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().add(table);

        return content;
    }

    /**
     * Load pending registrations from database
     * SD 1: Admin retrieves pending requests
     */
    private void loadPendingRegistrations() {
        List<Student> pendingStudents = adminController.getPendingRegistrations();
        table.getItems().clear();
        table.getItems().addAll(pendingStudents);

        if (pendingStudents.isEmpty()) {
            showAlert("No Pending Requests", "All registration requests have been processed!", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Handle approve action
     * SD 1: Admin approves registration
     */
    private void handleApprove(Student student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Registration");
        confirm.setHeaderText("Approve " + student.getName() + "?");
        confirm.setContentText("Student will be able to login and access the system.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (adminController.approveRegistration(student.getStudentID())) {
                    showAlert("Success",
                            "Registration approved!\n" + student.getName() + " can now login.",
                            Alert.AlertType.INFORMATION);
                    loadPendingRegistrations();
                } else {
                    showAlert("Error", "Failed to approve registration", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Handle reject action
     */
    private void handleReject(Student student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Registration");
        confirm.setHeaderText("Reject " + student.getName() + "?");
        confirm.setContentText("This will permanently delete the registration request.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (adminController.rejectRegistration(student.getStudentID())) {
                    showAlert("Success", "Registration rejected", Alert.AlertType.INFORMATION);
                    loadPendingRegistrations();
                } else {
                    showAlert("Error", "Failed to reject registration", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}