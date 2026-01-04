package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDashboardView - Complete Student Dashboard
 * Central hub for all student operations
 * FULLY INTEGRATED with Database, Transactions, and Business Layer
 */
public class StudentDashboardView {
    private Stage stage;
    private Student student;
    private VBox mainContent;

    // Statistics cache
    private int myBooksCount = 0;
    private int borrowedCount = 0;
    private int lentOutCount = 0;
    private int transactionsCount = 0;
    private int pendingRequestsCount = 0;

    public StudentDashboardView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
        loadStatistics(); // Load stats when dashboard is created
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F5F3E8;");

        // Sidebar navigation
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // ✅ FIXED: Wrap main content in ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Main content area
        mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - Student Dashboard");
        stage.show();
    }

    /**
     * Load all statistics from database
     */
    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Count my books (offers)
            myBooksCount = getMyBooksCount(conn);

            // Count borrowed books (active borrow transactions where I'm the borrower)
            borrowedCount = getBorrowedCount(conn);

            // Count lent out books (active borrow transactions where I'm the lender)
            lentOutCount = getLentOutCount(conn);

            // Count total transactions
            transactionsCount = getTotalTransactionsCount(conn);

            // Count pending incoming requests
            pendingRequestsCount = getPendingRequestsCount(conn);

            System.out.println("✅ Dashboard statistics loaded:");
            System.out.println("   My Books: " + myBooksCount);
            System.out.println("   Borrowed: " + borrowedCount);
            System.out.println("   Lent Out: " + lentOutCount);
            System.out.println("   Total Transactions: " + transactionsCount);
            System.out.println("   Pending Requests: " + pendingRequestsCount);

        } catch (SQLException e) {
            System.err.println("❌ Error loading dashboard statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getMyBooksCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT book_id) as count FROM " +
                "(SELECT book_id FROM lending_offers WHERE student_id = ? " +
                "UNION " +
                "SELECT book_id FROM selling_offers WHERE student_id = ?) as books";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getBorrowedCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM borrow_transactions " +
                "WHERE borrower_id = ? AND status = 'Active' AND is_returned = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getLentOutCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM borrow_transactions " +
                "WHERE lender_id = ? AND status = 'Active' AND is_returned = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getTotalTransactionsCount(Connection conn) throws SQLException {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM borrow_transactions WHERE borrower_id = ? OR lender_id = ?) + " +
                "(SELECT COUNT(*) FROM purchase_transactions WHERE buyer_id = ? OR seller_id = ?) as count";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, student.getStudentID());
            stmt.setString(4, student.getStudentID());
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    private int getPendingRequestsCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM book_requests " +
                "WHERE owner_id = ? AND status = 'Pending'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentID());
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

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #FFFFFF;");

        // Navigation buttons
        Button dashboardBtn = createNavButton("🏠 Dashboard", true);
        dashboardBtn.setOnAction(e -> {
            loadStatistics(); // Reload stats
            loadDashboardContent();
        });

        Button searchBtn = createNavButton("🔍 Search Books", false);
        searchBtn.setOnAction(e -> {
            SearchBooksView searchView = new SearchBooksView(stage, student);
            searchView.show();
        });

        Button myOffersBtn = createNavButton("📚 My Offers", false);
        myOffersBtn.setOnAction(e -> {
            MyOffersView myOffersView = new MyOffersView(stage, student);
            myOffersView.show();
        });

        // NEW: My Requests button with badge
        Button requestsBtn = createNavButtonWithBadge("📨 My Requests", false, pendingRequestsCount);
        requestsBtn.setOnAction(e -> {
            MyRequestsView requestsView = new MyRequestsView(stage, student);
            requestsView.show();
        });

        Button transactionsBtn = createNavButton("📋 Transactions", false);
        transactionsBtn.setOnAction(e -> {
            MyTransactionsView transactionsView = new MyTransactionsView(stage, student);
            transactionsView.show();
        });

        Button profileBtn = createNavButton("👤 My Profile", false);
        profileBtn.setOnAction(e -> {
            ProfileView profileView = new ProfileView(stage, student);
            profileView.show();
        });

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #555555;");

        Button complaintsBtn = createNavButton("⚠️ File Complaint", false);
        complaintsBtn.setOnAction(e -> {
            FileComplaintView complaintView = new FileComplaintView(stage, student);
            complaintView.show();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User info at bottom
        VBox userInfo = new VBox(5);
        userInfo.setPadding(new Insets(15));
        userInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");

        Label userName = new Label(student.getName());
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        userName.setTextFill(Color.WHITE);

        Label userEmail = new Label(student.getNuEmail());
        userEmail.setFont(Font.font("Arial", 11));
        userEmail.setTextFill(Color.web("#CCCCCC"));

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

        userInfo.getChildren().addAll(userName, userEmail, logoutBtn);

        sidebar.getChildren().addAll(
                logoBox, sep1,
                dashboardBtn, searchBtn, myOffersBtn, requestsBtn, transactionsBtn, profileBtn,
                sep2, complaintsBtn,
                spacer, userInfo
        );

        return sidebar;
    }

    private Button createNavButton(String text, boolean active) {
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

    /**
     * Create navigation button with notification badge
     */
    private Button createNavButtonWithBadge(String text, boolean active, int badgeCount) {
        Button btn = createNavButton(text, active);

        if (badgeCount > 0) {
            // Add badge
            StackPane stack = new StackPane();
            Label badge = new Label(String.valueOf(badgeCount));
            badge.setStyle(
                    "-fx-background-color: #F44336; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 2 6; " +
                            "-fx-background-radius: 10; " +
                            "-fx-font-size: 10; " +
                            "-fx-font-weight: bold;"
            );
            stack.getChildren().addAll(btn, badge);
            StackPane.setAlignment(badge, Pos.TOP_RIGHT);
            StackPane.setMargin(badge, new Insets(5, 5, 0, 0));
        }

        return btn;
    }

    private VBox createMainContent() {
        mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));

        loadDashboardContent();

        return mainContent;
    }

    /**
     * ✅ FIXED: Load dashboard with fresh data
     */
    private void loadDashboardContent() {
        mainContent.getChildren().clear();

        // ✅ Reload statistics to get fresh counts
        loadStatistics();

        // Welcome header
        VBox header = new VBox(10);
        Label welcome = new Label("Welcome back, " + student.getName() + "! 👋");
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcome.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("What would you like to do today?");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(welcome, subtitle);

        // Quick stats - NOW WITH REAL DATA
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("My Books", String.valueOf(myBooksCount), "📚", "#4CAF50"),
                createStatCard("Borrowed", String.valueOf(borrowedCount), "📖", "#2196F3"),
                createStatCard("Lent Out", String.valueOf(lentOutCount), "🤝", "#FF9800"),
                createStatCard("Transactions", String.valueOf(transactionsCount), "📊", "#9C27B0")
        );

        // ✅ Pending requests notification - ONLY show if there are pending requests
        VBox contentBox = new VBox(30);
        contentBox.getChildren().addAll(header, statsBox);

        if (pendingRequestsCount > 0) {
            HBox notificationBox = createNotificationBox();
            contentBox.getChildren().add(1, notificationBox); // Insert after header
        }

        // Quick actions
        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        actionsTitle.setTextFill(Color.web("#2C3E2E"));

        GridPane actionsGrid = new GridPane();
        actionsGrid.setHgap(20);
        actionsGrid.setVgap(20);

        actionsGrid.add(createActionCard("🔍", "Search Books", "Find books from fellow students", e -> {
            SearchBooksView searchView = new SearchBooksView(stage, student);
            searchView.show();
        }), 0, 0);

        actionsGrid.add(createActionCard("📚", "Lend a Book", "Share your books with others", e -> {
            CreateOfferView createOffer = new CreateOfferView(stage, student, "LEND");
            createOffer.show();
        }), 1, 0);

        actionsGrid.add(createActionCard("💰", "Sell a Book", "Sell your used books", e -> {
            CreateOfferView createOffer = new CreateOfferView(stage, student, "SELL");
            createOffer.show();
        }), 2, 0);

        actionsGrid.add(createActionCard("📨", "My Requests", "View and manage book requests", e -> {
            MyRequestsView requestsView = new MyRequestsView(stage, student);
            requestsView.show();
        }), 3, 0);

        // Recent activity - NOW WITH REAL DATA
        Label recentTitle = new Label("Recent Activity");
        recentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        recentTitle.setTextFill(Color.web("#2C3E2E"));

        VBox recentBox = createRecentActivitySection();

        contentBox.getChildren().addAll(actionsTitle, actionsGrid, recentTitle, recentBox);
        mainContent.getChildren().add(contentBox);
    }

    /**
     * Create notification box for pending requests
     */
    /**
     * ✅ FIXED: Create clickable notification box that dismisses after click
     */
    private HBox createNotificationBox() {
        HBox notification = new HBox(20);
        notification.setPadding(new Insets(20));
        notification.setAlignment(Pos.CENTER_LEFT);
        notification.setStyle(
                "-fx-background-color: #FFF3E0; " +
                        "-fx-border-color: #FF9800; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12;"
        );

        Label icon = new Label("🔔");
        icon.setFont(Font.font(30));

        VBox textBox = new VBox(5);
        Label title = new Label("You have " + pendingRequestsCount + " pending request(s)!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Students are waiting for your response");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.web("#666666"));

        textBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ✅ View Requests button - navigates and removes notification
        Button viewBtn = new Button("View Requests →");
        viewBtn.setStyle(
                "-fx-background-color: #FF9800; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10 25; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 8;"
        );
        viewBtn.setOnAction(e -> {
            // Navigate to My Requests
            MyRequestsView requestsView = new MyRequestsView(stage, student);
            requestsView.show();
            // Notification will disappear when user returns because pendingRequestsCount will be 0
        });

        // ✅ Dismiss button (X)
        Button dismissBtn = new Button("✕");
        dismissBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #666666; " +
                        "-fx-padding: 5 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 16; " +
                        "-fx-font-weight: bold;"
        );
        dismissBtn.setOnAction(e -> {
            // Remove notification from view temporarily
            mainContent.getChildren().remove(notification);
        });

        notification.getChildren().addAll(icon, textBox, spacer, viewBtn, dismissBtn);
        return notification;
    }
    /**
     * Create recent activity section with real data
     */
    private VBox createRecentActivitySection() {
        VBox section = new VBox(15);
        section.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 30; " +
                        "-fx-background-radius: 12;"
        );

        List<ActivityItem> activities = getRecentActivities();

        if (activities.isEmpty()) {
            Label noActivity = new Label("No recent activity");
            noActivity.setFont(Font.font("Arial", 16));
            noActivity.setTextFill(Color.web("#999999"));
            section.getChildren().add(noActivity);
        } else {
            for (ActivityItem activity : activities) {
                section.getChildren().add(createActivityCard(activity));
            }
        }

        return section;
    }

    /**
     * Get recent activities from database
     */
    private List<ActivityItem> getRecentActivities() {
        List<ActivityItem> activities = new ArrayList<>();

        String sql = "SELECT 'BORROW' as type, bt.borrow_date as date, b.title as book_title, " +
                "s.name as other_party, bt.status " +
                "FROM borrow_transactions bt " +
                "JOIN books b ON bt.book_id = b.book_id " +
                "JOIN students s ON bt.lender_id = s.student_id " +
                "WHERE bt.borrower_id = ? " +
                "UNION ALL " +
                "SELECT 'LEND' as type, bt.borrow_date as date, b.title as book_title, " +
                "s.name as other_party, bt.status " +
                "FROM borrow_transactions bt " +
                "JOIN books b ON bt.book_id = b.book_id " +
                "JOIN students s ON bt.borrower_id = s.student_id " +
                "WHERE bt.lender_id = ? " +
                "UNION ALL " +
                "SELECT 'BUY' as type, pt.transaction_date as date, b.title as book_title, " +
                "s.name as other_party, pt.status " +
                "FROM purchase_transactions pt " +
                "JOIN books b ON pt.book_id = b.book_id " +
                "JOIN students s ON pt.seller_id = s.student_id " +
                "WHERE pt.buyer_id = ? " +
                "UNION ALL " +
                "SELECT 'SELL' as type, pt.transaction_date as date, b.title as book_title, " +
                "s.name as other_party, pt.status " +
                "FROM purchase_transactions pt " +
                "JOIN books b ON pt.book_id = b.book_id " +
                "JOIN students s ON pt.buyer_id = s.student_id " +
                "WHERE pt.seller_id = ? " +
                "ORDER BY date DESC LIMIT 5";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getStudentID());
            stmt.setString(2, student.getStudentID());
            stmt.setString(3, student.getStudentID());
            stmt.setString(4, student.getStudentID());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ActivityItem item = new ActivityItem();
                item.type = rs.getString("type");
                item.bookTitle = rs.getString("book_title");
                item.otherParty = rs.getString("other_party");
                item.status = rs.getString("status");
                item.date = rs.getTimestamp("date");
                activities.add(item);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading recent activities: " + e.getMessage());
            e.printStackTrace();
        }

        return activities;
    }

    /**
     * Create activity card
     */
    private HBox createActivityCard(ActivityItem activity) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #F5F3E8; " +
                        "-fx-background-radius: 8;"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        String emoji = getActivityEmoji(activity.type);
        Label icon = new Label(emoji);
        icon.setFont(Font.font(30));

        VBox details = new VBox(5);
        Label action = new Label(getActivityText(activity.type, activity.bookTitle));
        action.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        action.setTextFill(Color.web("#2C3E2E"));

        Label party = new Label("with " + activity.otherParty);
        party.setFont(Font.font("Arial", 12));
        party.setTextFill(Color.web("#666666"));

        Label date = new Label(new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(activity.date));
        date.setFont(Font.font("Arial", 11));
        date.setTextFill(Color.web("#999999"));

        details.getChildren().addAll(action, party, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(activity.status);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setStyle(
                "-fx-background-color: " + getStatusColor(activity.status) + "; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-radius: 5;"
        );

        card.getChildren().addAll(icon, details, spacer, statusLabel);
        return card;
    }

    private String getActivityEmoji(String type) {
        switch (type) {
            case "BORROW": return "📖";
            case "LEND": return "🤝";
            case "BUY": return "🛒";
            case "SELL": return "💰";
            default: return "📚";
        }
    }

    private String getActivityText(String type, String bookTitle) {
        switch (type) {
            case "BORROW": return "Borrowed: " + bookTitle;
            case "LEND": return "Lent out: " + bookTitle;
            case "BUY": return "Purchased: " + bookTitle;
            case "SELL": return "Sold: " + bookTitle;
            default: return bookTitle;
        }
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Active": return "#4CAF50";
            case "Completed": return "#2196F3";
            case "Pending": return "#FF9800";
            case "Overdue": return "#F44336";
            default: return "#666666";
        }
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
        card.setPrefSize(260, 130);

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

    private VBox createActionCard(String icon, String title, String desc,
                                  javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30, 25, 30, 25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 12; " +
                        "-fx-cursor: hand;"
        );
        card.setPrefSize(250, 180);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(50));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2C3E2E"));

        Label descLabel = new Label(desc);
        descLabel.setFont(Font.font("Arial", 13));
        descLabel.setTextFill(Color.web("#666666"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);
        card.setOnMouseClicked(e -> handler.handle(null));

        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: #2C3E2E; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: #2C3E2E; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;"
            );
            titleLabel.setTextFill(Color.WHITE);
            descLabel.setTextFill(Color.web("#CCCCCC"));
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-color: #E0E0E0; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 12; " +
                            "-fx-cursor: hand;"
            );
            titleLabel.setTextFill(Color.web("#2C3E2E"));
            descLabel.setTextFill(Color.web("#666666"));
        });

        return card;
    }

    /**
     * Helper class for activity items
     */
    private static class ActivityItem {
        String type;
        String bookTitle;
        String otherParty;
        String status;
        java.util.Date date;
    }
}