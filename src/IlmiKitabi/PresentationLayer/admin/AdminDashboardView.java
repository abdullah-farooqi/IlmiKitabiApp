package IlmiKitabi.PresentationLayer.admin;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Admin;
import IlmiKitabi.DATAACCESSLAYER.DatabaseConnection;
import IlmiKitabi.PresentationLayer.common.WelcomeView;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.sql.*;

/**
 * AdminDashboardView - Admin Dashboard with REAL-TIME DATA
 * ✅ FIXED: Now shows dynamic statistics from database
 */
public class AdminDashboardView {
    private Stage stage;
    private Admin admin;
    private VBox mainContent;

    // Statistics cache
    private int pendingApprovalsCount = 0;
    private int totalStudentsCount = 0;
    private int activeBooksCount = 0;
    private int pendingComplaintsCount = 0;
    private int totalTransactionsCount = 0;
    private int activeFinesCount = 0;

    public AdminDashboardView(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
        loadStatistics(); // ✅ Load real data
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // ✅ Wrap in ScrollPane for better UX
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Admin Dashboard");
        stage.show();
    }

    /**
     * ✅ NEW: Load all statistics from database
     */
    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Pending registration approvals
            pendingApprovalsCount = getPendingApprovalsCount(conn);

            // Total students
            totalStudentsCount = getTotalStudentsCount(conn);

            // Active books
            activeBooksCount = getActiveBooksCount(conn);

            // Pending complaints
            pendingComplaintsCount = getPendingComplaintsCount(conn);

            // Total transactions
            totalTransactionsCount = getTotalTransactionsCount(conn);

            // Active fines
            activeFinesCount = getActiveFinesCount(conn);

            System.out.println("✅ Admin dashboard statistics loaded:");
            System.out.println("   Pending Approvals: " + pendingApprovalsCount);
            System.out.println("   Total Students: " + totalStudentsCount);
            System.out.println("   Active Books: " + activeBooksCount);
            System.out.println("   Pending Complaints: " + pendingComplaintsCount);
            System.out.println("   Total Transactions: " + totalTransactionsCount);
            System.out.println("   Active Fines: " + activeFinesCount);

        } catch (SQLException e) {
            System.err.println("❌ Error loading admin statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getPendingApprovalsCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM students WHERE is_active = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getTotalStudentsCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM students WHERE is_active = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getActiveBooksCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM books WHERE is_available = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getPendingComplaintsCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM complaints WHERE status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getTotalTransactionsCount(Connection conn) throws SQLException {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM borrow_transactions) + " +
                "(SELECT COUNT(*) FROM purchase_transactions) + " +
                "(SELECT COUNT(*) FROM exchange_transactions) as count";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getActiveFinesCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM fines WHERE is_paid = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setStyle("-fx-background-color: #2C3E2E;");
        sidebar.setPrefWidth(280);

        // Logo
        HBox logoBox = new HBox(5);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label ilmi = new Label("Ilmi");
        ilmi.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        ilmi.setTextFill(Color.web("#C4A57B"));
        Label kitabi = new Label("Kitabi");
        kitabi.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        kitabi.setTextFill(Color.WHITE);
        logoBox.getChildren().addAll(ilmi, kitabi);

        Label adminLabel = new Label("Admin Portal");
        adminLabel.setFont(Font.font("Arial", 14));
        adminLabel.setTextFill(Color.web("#FFFFFF"));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #FFFFFF;");

        // Navigation buttons
        Button dashboardBtn = createSidebarButton("🏠 Dashboard", true);
        dashboardBtn.setOnAction(e -> {
            loadStatistics(); // Refresh stats
            AdminDashboardView dashboard = new AdminDashboardView(stage, admin);
            dashboard.show();
        });

        Button approveBtn = createSidebarButton("✓ Approve Registrations", false);
        approveBtn.setOnAction(e -> {
            ApproveRegistrationsView approveView = new ApproveRegistrationsView(stage, admin);
            approveView.show();
        });

        Button studentsBtn = createSidebarButton("👥 Manage Students", false);
        studentsBtn.setOnAction(e -> {
            ManageStudentsView studentsView = new ManageStudentsView(stage, admin);
            studentsView.show();
        });

        Button complaintsBtn = createSidebarButton("📋 Handle Complaints", false);
        complaintsBtn.setOnAction(e -> {
            HandleComplaintsView complaintsView = new HandleComplaintsView(stage, admin);
            complaintsView.show();
        });

        Button finesBtn = createSidebarButton("💰 Manage Fines", false);
        finesBtn.setOnAction(e -> {
            ManageFinesView finesView = new ManageFinesView(stage, admin);
            finesView.show();
        });

        Button deleteAccountBtn = createSidebarButton("🗑️ Delete Account", false);
        deleteAccountBtn.setOnAction(e -> {
            DeleteAccountView deleteView = new DeleteAccountView(stage, admin);
            deleteView.show();
        });

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #555555;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Admin info
        VBox adminInfo = new VBox(5);
        adminInfo.setPadding(new Insets(15));
        adminInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");

        Label adminName = new Label(admin.getName());
        adminName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        adminName.setTextFill(Color.WHITE);

        Label adminEmail = new Label(admin.getEmail());
        adminEmail.setFont(Font.font("Arial", 11));
        adminEmail.setTextFill(Color.web("#CCCCCC"));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color: #C4A57B; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5 15; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 12;"
        );
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            WelcomeView welcome = new WelcomeView(stage);
            welcome.show();
        });

        adminInfo.getChildren().addAll(adminName, adminEmail, logoutBtn);

        sidebar.getChildren().addAll(
                logoBox, adminLabel, sep1,
                dashboardBtn, approveBtn, studentsBtn, complaintsBtn, finesBtn, deleteAccountBtn,
                sep2, spacer, adminInfo
        );

        return sidebar;
    }

    private Button createSidebarButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
                "-fx-background-color: " + (active ? "#C4A57B" : "transparent") + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 12 20; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8;"
        );

        btn.setOnMouseEntered(e -> {
            if (!active) {
                btn.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.1); " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 12 20; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 8;"
                );
            }
        });

        btn.setOnMouseExited(e -> {
            if (!active) {
                btn.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 12 20; " +
                                "-fx-font-size: 14; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 8;"
                );
            }
        });

        return btn;
    }

    private VBox createMainContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));

        // Welcome header
        VBox header = new VBox(10);
        Label welcome = new Label("Welcome, " + admin.getName() + " 👋");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcome.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Admin Dashboard - System Overview");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(welcome, subtitle);

        // Statistics grid - NOW WITH REAL DATA
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        statsGrid.add(createStatCard("Pending Approvals", String.valueOf(pendingApprovalsCount), "📝", "#FF9800"), 0, 0);
        statsGrid.add(createStatCard("Total Students", String.valueOf(totalStudentsCount), "👥", "#2196F3"), 1, 0);
        statsGrid.add(createStatCard("Active Books", String.valueOf(activeBooksCount), "📚", "#4CAF50"), 2, 0);
        statsGrid.add(createStatCard("Pending Complaints", String.valueOf(pendingComplaintsCount), "⚠️", "#F44336"), 3, 0);

        // Second row of stats
        GridPane statsGrid2 = new GridPane();
        statsGrid2.setHgap(20);
        statsGrid2.setVgap(20);

        statsGrid2.add(createStatCard("Total Transactions", String.valueOf(totalTransactionsCount), "💳", "#9C27B0"), 0, 0);
        statsGrid2.add(createStatCard("Active Fines", String.valueOf(activeFinesCount), "💰", "#FF5722"), 1, 0);
        statsGrid2.add(createStatCard("System Status", "Online", "✓", "#4CAF50"), 2, 0);

        // Quick actions
        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        actionsTitle.setTextFill(Color.web("#2C3E2E"));

        HBox actionsBox = new HBox(20);

        Button approveAction = createActionButton("✓ Approve Registrations", "#FF9800");
        approveAction.setOnAction(e -> {
            ApproveRegistrationsView view = new ApproveRegistrationsView(stage, admin);
            view.show();
        });

        Button studentsAction = createActionButton("👥 Manage Students", "#2196F3");
        studentsAction.setOnAction(e -> {
            ManageStudentsView view = new ManageStudentsView(stage, admin);
            view.show();
        });

        Button complaintsAction = createActionButton("📋 Handle Complaints", "#F44336");
        complaintsAction.setOnAction(e -> {
            HandleComplaintsView view = new HandleComplaintsView(stage, admin);
            view.show();
        });

        Button finesAction = createActionButton("💰 Manage Fines", "#FF5722");
        finesAction.setOnAction(e -> {
            ManageFinesView view = new ManageFinesView(stage, admin);
            view.show();
        });

        actionsBox.getChildren().addAll(approveAction, studentsAction, complaintsAction, finesAction);

        content.getChildren().addAll(header, statsGrid, statsGrid2, actionsTitle, actionsBox);

        return content;
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
        card.setPrefSize(300, 130);

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

    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 15 30; " +
                        "-fx-font-size: 14; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8;"
        );
        btn.setPrefWidth(280);

        // Hover effect
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: derive(" + color + ", -10%); " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 15 30; " +
                            "-fx-font-size: 14; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 8;"
            );
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: " + color + "; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 15 30; " +
                            "-fx-font-size: 14; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand; " +
                            "-fx-background-radius: 8;"
            );
        });

        return btn;
    }
}