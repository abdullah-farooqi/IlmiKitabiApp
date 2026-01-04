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
 * ManageStudentsView - Admin student management
 */
public class ManageStudentsView {
    private Stage stage;
    private Admin admin;
    private AdminController adminController;
    private TableView<Student> table;

    public ManageStudentsView(Stage stage, Admin admin) {
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
        stage.setTitle("Ilmi Kitabi - Manage Students");
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
        refreshBtn.setOnAction(e -> loadStudents());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("👥 Manage Students");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("View and manage all registered students");
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

        TableColumn<Student, String> idCol = new TableColumn<>("Student ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        idCol.setPrefWidth(150);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Student, String> emailCol = new TableColumn<>("NU Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("nuEmail"));
        emailCol.setPrefWidth(250);

        TableColumn<Student, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(140);

        TableColumn<Student, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        deptCol.setPrefWidth(180);

        TableColumn<Student, String> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            return new javafx.beans.property.SimpleStringProperty(
                    sdf.format(cellData.getValue().getRegistrationDate())
            );
        });
        dateCol.setPrefWidth(150);

        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().isActive() ? "Active" : "Inactive"
                )
        );
        statusCol.setPrefWidth(100);

        TableColumn<Student, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");

            {
                viewBtn.setStyle(
                        "-fx-background-color: #2196F3; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 5 15; " +
                                "-fx-cursor: hand;"
                );

                viewBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showStudentDetails(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, deptCol, dateCol, statusCol, actionCol);

        // Load data
        loadStudents();

        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().add(table);

        return content;
    }

    private void loadStudents() {
        List<Student> students = adminController.getAllStudents();
        table.getItems().clear();
        table.getItems().addAll(students);
    }

    private void showStudentDetails(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText(student.getName());

        String details =
                "Student ID: " + student.getStudentID() + "\n" +
                        "Name: " + student.getName() + "\n" +
                        "NU Email: " + student.getNuEmail() + "\n" +
                        "Phone: " + student.getPhoneNumber() + "\n" +
                        "Department: " + student.getDepartment() + "\n" +
                        "Registration Date: " + new SimpleDateFormat("dd MMM yyyy").format(student.getRegistrationDate()) + "\n" +
                        "Status: " + (student.isActive() ? "Active" : "Inactive");

        alert.setContentText(details);
        alert.showAndWait();
    }
}