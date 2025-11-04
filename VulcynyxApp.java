package mini_project_dbms;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class VulcynyxApp extends JFrame {
    private Database dbManager;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JPanel buttonPanel;
    private JLabel statusLabel;
    
    public VulcynyxApp() {
        super("Vulcynyx Jewellry Business Analytics Dashboard");
        
        // Setup UI first
        initializeUI();
        
        // Window settings
        setSize(1500, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Close handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApplication();
            }
        });
        
        // Initialize database manager (after UI is ready)
        try {
            dbManager = new Database();
            logMessage("Connected to Vulcynyx database successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Database Connection Error: " + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Title Panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Left Control Panel
        JScrollPane controlScrollPane = createControlPanel();
        add(controlScrollPane, BorderLayout.WEST);
        
        // Center Table Panel
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel (Log + Status)
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Vulcynyx Jewellry Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Complete Business Intelligence Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JScrollPane createControlPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Menu Label
        JLabel menuLabel = new JLabel("ANALYTICS MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(menuLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Section 1: Product Analytics
        addSectionHeader("PRODUCT ANALYTICS");
        addQueryButton("Top Products by Age Group", this::query01);
        addQueryButton("Products in Price Range", this::query02);
        addQueryButton("Search by Material", this::query05);
        addQueryButton("Sort Products by Price", this::query06);
        addQueryButton("Total Quantity Sold", this::query11);
        addQueryButton("Categories of Product", this::query04);
        
        addSeparator();
        
        // Section 2: Sales & Revenue
        addSectionHeader("SALES & REVENUE");
        addQueryButton("Revenue by Platform", this::query03);
        addQueryButton("Sale of Product", this::query08);
        addQueryButton("Sale by Category", this::query09);
        addQueryButton("Top 5 Customers by Spending", this::query10);
        addQueryButton("Revenue per Category", this::query14);
        addQueryButton("Avg Price per Category", this::query12);
        
        addSeparator();
        
        // Section 3: Regional & Customer Analytics
        addSectionHeader("REGIONAL & CUSTOMER");
        addQueryButton("Customers in City", this::query07);
        addQueryButton("Top Selling by Region", this::query13);
        addQueryButton("Top Customers by Year", this::query16);
        
        addSeparator();
        
        // Section 4: Campaign & Ads
        addSectionHeader("CAMPAIGNS & ADS");
        addQueryButton("Campaigns with Highest ROI", this::query15);
        addQueryButton("Campaign Reports by Region", this::query20);
        addQueryButton("Ads Running at Loss", this::query21);
        addQueryButton("Ads with High Conversion", this::query22);
        
        addSeparator();
        
        // Section 5: Inventory & Stock
        addSectionHeader("INVENTORY MANAGEMENT");
        addQueryButton("Products Not Sold (1 Month)", this::query17);
        addQueryButton("Trending Products", this::query18);
        addQueryButton("Restock Priority List", this::query24);
        addQueryButton("Low Stock Alerts", this::query25);
        
        addSeparator();
        
        // Section 6: Advanced Analytics
        addSectionHeader("ADVANCED ANALYTICS");
        addQueryButton("High Bounce Rate Products", this::query19);
        addQueryButton("Calculate Discounted Orders", this::query23);
        
        addSeparator();
        
        // Control buttons
        JButton clearBtn = createControlButton("Clear Results", new Color(231, 76, 60));
        clearBtn.addActionListener(e -> clearTable());
        
        JButton refreshBtn = createControlButton("Refresh Connection", new Color(39, 174, 96));
        refreshBtn.addActionListener(e -> refreshConnection());
        
        JButton exitBtn = createControlButton("Exit Application", new Color(52, 73, 94));
        exitBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                closeApplication();
                System.exit(0);
            }
        });
        
        buttonPanel.add(clearBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(exitBtn);
        
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        return scrollPane;
    }
    
    private void addSectionHeader(String text) {
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel header = new JLabel(text);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(new Color(52, 73, 94));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(header);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
    
    private void addSeparator() {
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(280, 1));
        buttonPanel.add(sep);
    }
    
    private void addQueryButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(270, 32));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        
        button.addActionListener(e -> {
            try {
                updateStatus("Executing query...");
                action.run();
                updateStatus("Query completed successfully");
            } catch (Exception ex) {
                showError("Query Error", ex.getMessage());
                updateStatus("Query failed");
            }
        });
        
        buttonPanel.add(button);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 4)));
    }
    
    private JButton createControlButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(270, 35));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table title
        JLabel tableTitle = new JLabel("Query Results");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        // Table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(28);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(new Color(189, 195, 199));
        resultTable.setSelectionBackground(new Color(52, 152, 219));
        resultTable.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = resultTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.setAutoCreateRowSorter(true);
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        JLabel logLabel = new JLabel("📋 Activity Log:");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        
        logArea = new JTextArea(6, 0);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(236, 240, 241));
        
        panel.add(logPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Query Methods
    private void query01() {
        executeQuery(() -> dbManager.getTopSearchedProductsByAgeGroup(), 
                    "Top Searched Products by Age Group");
    }
    
    private void query02() {
        String minStr = showInputDialog("Enter Minimum Price:", "1000");
        if (minStr != null) {
            String maxStr = showInputDialog("Enter Maximum Price:", "50000");
            if (maxStr != null) {
                try {
                    double min = Double.parseDouble(minStr);
                    double max = Double.parseDouble(maxStr);
                    executeQuery(() -> dbManager.getMostPurchasedInPriceRange(min, max),
                               "Most Purchased Products (₹" + min + " - ₹" + max + ")");
                } catch (NumberFormatException e) {
                    showError("Input Error", "Please enter valid numbers");
                }
            }
        }
    }
    
    private void query03() {
        executeQuery(() -> dbManager.getRevenueByPlatform(), "Revenue by Platform");
    }
    
    private void query04() {
        String product = showInputDialog("Enter Product Name:", "Necklace");
        if (product != null && !product.trim().isEmpty()) {
            executeQuery(() -> dbManager.getCategoriesOfProduct(product),
                       "Categories of Product: " + product);
        }
    }
    
    private void query05() {
        String material = showInputDialog("Enter Material Name:", "gold");
        if (material != null && !material.trim().isEmpty()) {
            executeQuery(() -> dbManager.searchProductByMaterial(material),
                       "Products with Material: " + material);
        }
    }
    
    private void query06() {
        executeQuery(() -> dbManager.sortProductsByPrice(), "Products Sorted by Price");
    }
    
    private void query07() {
        String city = showInputDialog("Enter City Name:", "Pune");
        if (city != null && !city.trim().isEmpty()) {
            executeQuery(() -> dbManager.getCustomersInCity(city),
                       "Customers in " + city);
        }
    }
    
    private void query08() {
        String product = showInputDialog("Enter Product Name:", "Gold Ring");
        if (product != null && !product.trim().isEmpty()) {
            executeQuery(() -> dbManager.getSaleOfProduct(product),
                       "Sales of " + product);
        }
    }
    
    private void query09() {
        String category = showInputDialog("Enter Category Name:", "Earrings");
        if (category != null && !category.trim().isEmpty()) {
            executeQuery(() -> dbManager.getSaleOfCategory(category),
                       "Sales of Category: " + category);
        }
    }
    
    private void query10() {
        executeQuery(() -> dbManager.getTop5CustomersBySpending(), "Top 5 Customers by Spending");
    }
    
    private void query11() {
        executeQuery(() -> dbManager.getTotalQuantitySoldPerProduct(), "Total Quantity Sold per Product");
    }
    
    private void query12() {
        executeQuery(() -> dbManager.getAveragePricePerCategory(), "Average Price per Category");
    }
    
    private void query13() {
        executeQuery(() -> dbManager.getTopSellingProductsPerRegion(), "Top Selling Products per Region");
    }
    
    private void query14() {
        executeQuery(() -> dbManager.getTotalRevenuePerCategory(), "Total Revenue per Category");
    }
    
    private void query15() {
        String limitStr = showInputDialog("Enter Number of Campaigns:", "5");
        if (limitStr != null) {
            try {
                int limit = Integer.parseInt(limitStr);
                executeQuery(() -> dbManager.getCampaignsWithHighestROI(limit),
                           "Top " + limit + " Campaigns by ROI");
            } catch (NumberFormatException e) {
                showError("Input Error", "Please enter a valid number");
            }
        }
    }
    
    private void query16() {
        executeQuery(() -> dbManager.getTop10CustomersByYear(), "Top 10 Customers by Year");
    }
    
    private void query17() {
        executeQuery(() -> dbManager.getProductsNotSoldLastMonth(), "Products Not Sold in Last Month");
    }
    
    private void query18() {
        executeQuery(() -> dbManager.getTrendingProducts(), "Trending Products");
    }
    
    private void query19() {
        executeQuery(() -> dbManager.getHighBounceRateProducts(), "High Bounce Rate Products");
    }
    
    private void query20() {
        executeQuery(() -> dbManager.getCampaignReportsPerRegion(), "Campaign Reports by Region");
    }
    
    private void query21() {
        executeQuery(() -> dbManager.getAdsRunningAtLoss(), "Ads Running at Loss");
    }
    
    private void query22() {
        executeQuery(() -> dbManager.getAdsWithHighConversion(), "Ads with High Conversion Rate");
    }
    
    private void query23() {
        try {
            updateStatus("Calling stored procedure...");
            dbManager.callDiscountedOrderProcedure();
            logMessage("Discounted Order procedure executed successfully");
            JOptionPane.showMessageDialog(this,
                "Discounted order calculation completed!\nCheck your database for results.",
                "Procedure Success",
                JOptionPane.INFORMATION_MESSAGE);
            updateStatus("Procedure completed");
        } catch (SQLException e) {
            showError("Procedure Error", e.getMessage());
            updateStatus("Procedure failed");
        }
    }
    
    private void query24() {
        executeQuery(() -> dbManager.getRestockPriorityList(), "Restock Priority List");
    }
    
    private void query25() {
        executeQuery(() -> dbManager.getLowStockAlerts(), "Low Stock Alerts");
    }
    
    // Helper Methods
    private void executeQuery(QueryExecutor executor, String queryName) {
        try {
            List<Map<String, Object>> results = executor.execute();
            displayResults(results, queryName);
        } catch (SQLException e) {
            showError("Query Error", e.getMessage());
        }
    }
    
    @FunctionalInterface
    interface QueryExecutor {
        List<Map<String, Object>> execute() throws SQLException;
    }
    
    private void displayResults(List<Map<String, Object>> results, String queryName) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        if (results.isEmpty()) {
            logMessage("No results found for: " + queryName);
            JOptionPane.showMessageDialog(this, 
                "No results found for this query!", 
                "No Data", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Set column names in a stable order (preserved by LinkedHashMap in Database)
        Map<String, Object> firstRow = results.get(0);
        List<String> columns = new ArrayList<>(firstRow.keySet());
        for (String columnName : columns) {
            tableModel.addColumn(columnName);
        }

        // Add rows using the same column order
        for (Map<String, Object> row : results) {
            Object[] rowData = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                rowData[i] = row.get(columns.get(i));
            }
            tableModel.addRow(rowData);
        }
        
        // Auto-resize columns
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
        
        logMessage("✓ " + queryName + " - " + results.size() + " records retrieved");
    }
    
    private void clearTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        logMessage("Table cleared");
        updateStatus("Table cleared");
    }
    
    private void refreshConnection() {
        try {
            updateStatus("Refreshing connection...");
            dbManager.closeConnection();
            dbManager = new Database();
            logMessage("Database connection refreshed");
            updateStatus("Connection refreshed");
            JOptionPane.showMessageDialog(this,
                "Database connection refreshed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showError("Connection Error", e.getMessage());
            updateStatus("Connection refresh failed");
        }
    }
    
    private String showInputDialog(String message, String defaultValue) {
        return (String) JOptionPane.showInputDialog(
            this,
            message,
            "Input Required",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            defaultValue
        );
    }
    
    private void logMessage(String message) {
        if (logArea != null) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        } else {
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + message);
        }
    }
    
    private void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        logMessage("Error: " + message);
    }
    
    private void closeApplication() {
        try {
            dbManager.closeConnection();
            logMessage("Application closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            try {
                VulcynyxApp frame = new VulcynyxApp();
                frame.setVisible(true);
                System.out.println("GUI initialized successfully!");
            } catch (Exception e) {
                System.err.println("Error initializing GUI: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing application:\n" + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
