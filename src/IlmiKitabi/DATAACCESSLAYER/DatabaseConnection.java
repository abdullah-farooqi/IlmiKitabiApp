package IlmiKitabi.DATAACCESSLAYER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**ff
 * DatabaseConnection - Manages database connections
 * GOF: Singleton Pattern
 * GRASP: Low Coupling - Centralized database connection management
 */
public class DatabaseConnection {

    // Database configurationf
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "ilmi_kitabi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // ✅ Update with your MySQL password if needed

    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * Private constructor for Singleton pattern
     */
    private DatabaseConnection() {
        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create DB if missing
            createDatabaseIfNotExists();

            // Connect to DB
            connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
            System.out.println("✅ Database connection established successfully");

            // Initialize DB schema
            initializeSchema();

        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get singleton instance (Thread-safe)
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error retrieving connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Create database if not exists
     */
    private void createDatabaseIfNotExists() {
        try (Connection tempConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = tempConn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✅ Database '" + DB_NAME + "' is ready");

        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize all database tables
     */
    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {

            // 🧑‍🎓 Students Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    student_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    nu_email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    phone_number VARCHAR(20),
                    department VARCHAR(100),
                    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_active BOOLEAN DEFAULT FALSE
                )
            """);
            System.out.println("✅ Students table is ready");

            // 👨‍💼 Admins Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS admins (
                    admin_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE
                )
            """);
            System.out.println("✅ Admins table is ready");

            // 📚 Books Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS books (
                    book_id VARCHAR(50) PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    author VARCHAR(100),
                    isbn VARCHAR(20),
                    subject VARCHAR(100),
                    description TEXT,
                    image_url VARCHAR(255),
                    book_condition VARCHAR(50),
                    owner_id VARCHAR(50),
                    offer_type VARCHAR(20),
                    is_available BOOLEAN DEFAULT TRUE,
                    FOREIGN KEY (owner_id) REFERENCES students(student_id)
                )
            """);
            System.out.println("✅ Books table is ready");

            // 🤝 Lending Offers
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS lending_offers (
                    offer_id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50),
                    book_id VARCHAR(50),
                    duration INT,
                    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    end_date TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Available',
                    meeting_location VARCHAR(255),
                    FOREIGN KEY (student_id) REFERENCES students(student_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Lending offers table is ready");

            // 💰 Selling Offers
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS selling_offers (
                    offer_id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50),
                    book_id VARCHAR(50),
                    price DECIMAL(10,2),
                    is_negotiable BOOLEAN DEFAULT FALSE,
                    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Available',
                    meeting_location VARCHAR(255),
                    FOREIGN KEY (student_id) REFERENCES students(student_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Selling offers table is ready");

            // 🔄 Exchange Offers
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exchange_offers (
                    offer_id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50),
                    desired_book_title VARCHAR(200) NOT NULL,
                    desired_book_author VARCHAR(100),
                    offered_book_ids TEXT,
                    post_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Available',
                    meeting_location VARCHAR(255),
                    FOREIGN KEY (student_id) REFERENCES students(student_id)
                )
            """);
            System.out.println("✅ Exchange offers table is ready");

            // 🔄 Borrow Transactions
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS borrow_transactions (
                    transaction_id VARCHAR(50) PRIMARY KEY,
                    borrower_id VARCHAR(50),
                    lender_id VARCHAR(50),
                    book_id VARCHAR(50),
                    borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    due_date TIMESTAMP,
                    return_date TIMESTAMP,
                    is_returned BOOLEAN DEFAULT FALSE,
                    is_overdue BOOLEAN DEFAULT FALSE,
                    status VARCHAR(20) DEFAULT 'Active',
                    meeting_details VARCHAR(255),
                    FOREIGN KEY (borrower_id) REFERENCES students(student_id),
                    FOREIGN KEY (lender_id) REFERENCES students(student_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Borrow transactions table is ready");

            // 🛍 Purchase Transactions
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS purchase_transactions (
                    transaction_id VARCHAR(50) PRIMARY KEY,
                    buyer_id VARCHAR(50),
                    seller_id VARCHAR(50),
                    book_id VARCHAR(50),
                    amount DECIMAL(10,2),
                    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Completed',
                    payment_method VARCHAR(50),
                    meeting_details VARCHAR(255),
                    FOREIGN KEY (buyer_id) REFERENCES students(student_id),
                    FOREIGN KEY (seller_id) REFERENCES students(student_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Purchase transactions table is ready");

            // ♻ Exchange Transactions
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS exchange_transactions (
                    transaction_id VARCHAR(50) PRIMARY KEY,
                    student1_id VARCHAR(50),
                    student2_id VARCHAR(50),
                    book1_id VARCHAR(50),
                    book2_id VARCHAR(50),
                    exchange_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Pending',
                    is_completed BOOLEAN DEFAULT FALSE,
                    meeting_details VARCHAR(255),
                    FOREIGN KEY (student1_id) REFERENCES students(student_id),
                    FOREIGN KEY (student2_id) REFERENCES students(student_id),
                    FOREIGN KEY (book1_id) REFERENCES books(book_id),
                    FOREIGN KEY (book2_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Exchange transactions table is ready");

            // ⚖ Complaints
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS complaints (
                    complaint_id VARCHAR(50) PRIMARY KEY,
                    complainant_id VARCHAR(50),
                    accused_id VARCHAR(50),
                    transaction_id VARCHAR(50),
                    description TEXT NOT NULL,
                    category VARCHAR(100),
                    filed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Pending',
                    resolution TEXT,
                    resolved_date TIMESTAMP,
                    FOREIGN KEY (complainant_id) REFERENCES students(student_id),
                    FOREIGN KEY (accused_id) REFERENCES students(student_id)
                )
            """);
            System.out.println("✅ Complaints table is ready");

            // 💸 Fines
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fines (
                    fine_id VARCHAR(50) PRIMARY KEY,
                    student_id VARCHAR(50),
                    amount DECIMAL(10,2) NOT NULL,
                    reason TEXT NOT NULL,
                    imposed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    due_date TIMESTAMP,
                    is_paid BOOLEAN DEFAULT FALSE,
                    status VARCHAR(20) DEFAULT 'Unpaid',
                    FOREIGN KEY (student_id) REFERENCES students(student_id)
                )
            """);
            System.out.println("✅ Fines table is ready");

            // 📩 Book Requests (Updated Column Names)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS book_requests (
                    request_id VARCHAR(50) PRIMARY KEY,
                    requester_id VARCHAR(50),
                    owner_id VARCHAR(50),
                    book_id VARCHAR(50),
                    request_type VARCHAR(20),
                    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'Pending',
                    FOREIGN KEY (requester_id) REFERENCES students(student_id),
                    FOREIGN KEY (owner_id) REFERENCES students(student_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )
            """);
            System.out.println("✅ Book requests table is ready");

            insertDefaultAdmin();
            System.out.println("✅ Database schema initialized successfully");

        } catch (SQLException e) {
            System.err.println("❌ Error initializing schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert a default admin for testing
     */
    private void insertDefaultAdmin() {
        try (Statement stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM admins WHERE email = 'admin@nu.edu.pk'");
            if (rs.next() && rs.getInt("count") == 0) {
                String hashedPassword = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"; // SHA-256 of "admin123"
                stmt.executeUpdate("""
                    INSERT INTO admins (admin_id, name, email, password, is_active)
                    VALUES ('ADM001', 'System Administrator', 'admin@nu.edu.pk', '""" + hashedPassword + "', TRUE)"
                );
                System.out.println("👤 Default admin created — Email: admin@nu.edu.pk | Password: admin123");
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error creating default admin: " + e.getMessage());
        }
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}