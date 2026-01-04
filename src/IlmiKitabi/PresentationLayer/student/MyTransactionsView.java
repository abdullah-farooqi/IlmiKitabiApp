package IlmiKitabi.PresentationLayer.student;

import IlmiKitabi.BUSINESSLOGICLAYER.controllers.TransactionController;
import IlmiKitabi.BUSINESSLOGICLAYER.models.Student;
import IlmiKitabi.BUSINESSLOGICLAYER.models.TransactionHistory;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * MyTransactionsView - View transaction history
 * Shows all borrow, lend, buy, sell transactions
 */
public class MyTransactionsView {
    private Stage stage;
    private Student student;
    private TransactionController transactionController;
    private VBox contentContainer;

    public MyTransactionsView(Stage stage, Student student) {
        this.stage = stage;
        this.student = student;
        this.transactionController = new TransactionController();
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
        loadTransactions();

        scrollPane.setContent(contentContainer);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Ilmi Kitabi - My Transactions");
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
            StudentDashboardView dashboard = new StudentDashboardView(stage, student);
            dashboard.show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #2C3E2E; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadTransactions());

        topBar.getChildren().addAll(backBtn, spacer, refreshBtn);

        Label title = new Label("📋 My Transactions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#2C3E2E"));

        Label subtitle = new Label("Complete history of all your book transactions");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#666666"));

        header.getChildren().addAll(topBar, title, subtitle);
        return header;
    }

    private void loadTransactions() {
        contentContainer.getChildren().clear();

        // Get transaction history
        List<TransactionHistory> transactions = transactionController.getTransactionHistory(student.getStudentID());

        // Calculate stats
        long borrowCount = transactions.stream().filter(t -> t.getTransactionType().equals("BORROW")).count();
        long lendCount = transactions.stream().filter(t -> t.getTransactionType().equals("LEND")).count();
        long buyCount = transactions.stream().filter(t -> t.getTransactionType().equals("BUY")).count();
        long sellCount = transactions.stream().filter(t -> t.getTransactionType().equals("SELL")).count();
        long exchangeCount = transactions.stream().filter(t -> t.getTransactionType().equals("EXCHANGE")).count();

        // Stats cards
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Borrowed", String.valueOf(borrowCount), "📖", "#2196F3"),
                createStatCard("Lent Out", String.valueOf(lendCount), "🤝", "#4CAF50"),
                createStatCard("Purchased", String.valueOf(buyCount), "🛒", "#FF9800"),
                createStatCard("Sold", String.valueOf(sellCount), "💰", "#9C27B0"),
                createStatCard("Exchanges", String.valueOf(exchangeCount), "♻", "#607D8B")
        );

        // Transactions list
        Label listTitle = new Label("Transaction History (" + transactions.size() + " total)");
        listTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        listTitle.setTextFill(Color.web("#2C3E2E"));

        VBox transactionsList = new VBox(15);

        if (transactions.isEmpty()) {
            transactionsList.getChildren().add(createEmptyState());
        } else {
            for (TransactionHistory transaction : transactions) {
                transactionsList.getChildren().add(createTransactionCard(transaction));
            }
        }

        contentContainer.getChildren().addAll(statsBox, listTitle, transactionsList);
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

    private HBox createTransactionCard(TransactionHistory transaction) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Type badge
        VBox badgeBox = new VBox();
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setPrefWidth(120);

        String emoji = getTransactionEmoji(transaction.getTransactionType());
        String color = getTransactionColor(transaction.getTransactionType());

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(40));

        Label typeLabel = new Label(transaction.getTransactionType());
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-padding: 6 15; " +
                        "-fx-background-radius: 5;"
        );

        badgeBox.getChildren().addAll(emojiLabel, typeLabel);

        // Book details
        VBox detailsBox = new VBox(8);
        detailsBox.setPrefWidth(500);

        Label title = new Label(transaction.getBookTitle());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#2C3E2E"));
        title.setWrapText(true);

        Label author = new Label("by " + transaction.getBookAuthor());
        author.setFont(Font.font("Arial", 14));
        author.setTextFill(Color.web("#666666"));

        Label otherParty = new Label(getPartyLabel(transaction.getTransactionType()) + ": " + transaction.getOtherPartyName());
        otherParty.setFont(Font.font("Arial", 13));
        otherParty.setTextFill(Color.web("#999999"));

        detailsBox.getChildren().addAll(title, author, otherParty);

        // Transaction info
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPrefWidth(250);

        Label date = new Label(new SimpleDateFormat("dd MMM yyyy").format(transaction.getTransactionDate()));
        date.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        date.setTextFill(Color.web("#2C3E2E"));

        if (transaction.getDueDate() != null) {
            Label dueDate = new Label("Due: " + new SimpleDateFormat("dd MMM yyyy").format(transaction.getDueDate()));
            dueDate.setFont(Font.font("Arial", 12));
            dueDate.setTextFill(transaction.isOverdue() ? Color.web("#F44336") : Color.web("#999999"));
            infoBox.getChildren().addAll(date, dueDate);
        } else {
            infoBox.getChildren().add(date);
        }

        if (transaction.getAmount() != null) {
            Label amount = new Label("PKR " + String.format("%.0f", transaction.getAmount()));
            amount.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            amount.setTextFill(Color.web("#FF9800"));
            infoBox.getChildren().add(amount);
        }

        // Status
        VBox statusBox = new VBox(10);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(180);

        Label status = new Label(transaction.getStatus());
        status.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        status.setTextFill(Color.WHITE);
        status.setStyle(
                "-fx-background-color: " + getStatusColor(transaction.getStatus()) + "; " +
                        "-fx-padding: 8 20; " +
                        "-fx-background-radius: 5;"
        );

        if (transaction.isOverdue()) {
            Label overdueLabel = new Label("⚠ OVERDUE");
            overdueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            overdueLabel.setTextFill(Color.web("#F44336"));
            statusBox.getChildren().addAll(status, overdueLabel);
        } else {
            statusBox.getChildren().add(status);
        }

        card.getChildren().addAll(badgeBox, detailsBox, infoBox, statusBox);

        return card;
    }

    private VBox createEmptyState() {
        VBox empty = new VBox(20);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(80));
        empty.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        Label emptyIcon = new Label("📭");
        emptyIcon.setFont(Font.font(80));

        Label emptyTitle = new Label("No transactions yet");
        emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        emptyTitle.setTextFill(Color.web("#666666"));

        Label emptyMsg = new Label("Start borrowing, lending, buying, or selling books to see your transaction history");
        emptyMsg.setFont(Font.font("Arial", 14));
        emptyMsg.setTextFill(Color.web("#999999"));
        emptyMsg.setWrapText(true);
        emptyMsg.setMaxWidth(400);
        emptyMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        empty.getChildren().addAll(emptyIcon, emptyTitle, emptyMsg);
        return empty;
    }

    private String getTransactionEmoji(String type) {
        switch (type) {
            case "BORROW": return "📖";
            case "LEND": return "🤝";
            case "BUY": return "🛒";
            case "SELL": return "💰";
            case "EXCHANGE": return "♻";
            default: return "📚";
        }
    }

    private String getTransactionColor(String type) {
        switch (type) {
            case "BORROW": return "#2196F3";
            case "LEND": return "#4CAF50";
            case "BUY": return "#FF9800";
            case "SELL": return "#9C27B0";
            case "EXCHANGE": return "#607D8B";
            default: return "#666666";
        }
    }

    private String getPartyLabel(String type) {
        switch (type) {
            case "BORROW": return "Lender";
            case "LEND": return "Borrower";
            case "BUY": return "Seller";
            case "SELL": return "Buyer";
            case "EXCHANGE": return "Exchange Partner";
            default: return "Other Party";
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
}