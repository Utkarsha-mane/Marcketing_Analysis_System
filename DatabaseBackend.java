package mini_project_dbms;

import java.util.*;
import java.sql.*;


public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Vulcynyx?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "password";
    
    private Connection connection;
    
    // Constructor - Establish connection
    public Database() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected to database successfully!");
    }
    
    // Get connection
    public Connection getConnection() {
        return connection;
    }
    
    // Close connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }
    
    // Query 1: Top searched products by age group
    public List<Map<String, Object>> getTopSearchedProductsByAgeGroup() throws SQLException {
        String query = "SELECT * FROM (" +
                      "SELECT a.AgeGroup, p.ProductID, p.Name AS ProductName, " +
                      "SUM(a.Impressions) AS Total_Impressions, " +
                      "RANK() OVER (PARTITION BY a.AgeGroup ORDER BY SUM(a.Impressions) DESC) AS rnk " +
                      "FROM Ads a " +
                      "JOIN Products_Ads pa ON a.AdsID = pa.AdsID AND a.Platform = pa.Platform " +
                      "JOIN Product p ON pa.ProductID = p.ProductID " +
                      "GROUP BY a.AgeGroup, p.ProductID, p.Name) ranked " +
                      "WHERE rnk = 1";
        
        return executeQuery(query);
    }
    
    // Query 2: Most purchased products in price range
    public List<Map<String, Object>> getMostPurchasedInPriceRange(double minPrice, double maxPrice) throws SQLException {
        String query = "SELECT p.Name AS ProductName, SUM(b.Quantity) AS TotalSold " +
                      "FROM Business b " +
                      "JOIN Product p ON b.ProductID = p.ProductID " +
                      "WHERE p.Price BETWEEN ? AND ? " +
                      "GROUP BY p.Name " +
                      "ORDER BY TotalSold DESC LIMIT 3";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setDouble(1, minPrice);
        ps.setDouble(2, maxPrice);
        return executeQuery(ps);
    }
    
    // Query 3: Revenue from different platforms
    public List<Map<String, Object>> getRevenueByPlatform() throws SQLException {
        String query = "SELECT a.Platform, SUM(a.Revenue) AS Total_Revenue " +
                      "FROM Ads a " +
                      "GROUP BY a.Platform " +
                      "ORDER BY Total_Revenue DESC";
        
        return executeQuery(query);
    }
    
    // Query 4: Categories of a product
    public List<Map<String, Object>> getCategoriesOfProduct(String productName) throws SQLException {
        String query = "SELECT DISTINCT Category FROM Product WHERE Name = ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, productName);
        return executeQuery(ps);
    }
    
    // Query 5: Search product by material
    public List<Map<String, Object>> searchProductByMaterial(String material) throws SQLException {
        String query = "SELECT ProductID, Name AS ProductName, Price, Stock " +
                      "FROM Product " +
                      "WHERE Material LIKE ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, "%" + material + "%");
        return executeQuery(ps);
    }
    
    // Query 6: Sort products by price
    public List<Map<String, Object>> sortProductsByPrice() throws SQLException {
        String query = "SELECT ProductID, Name AS ProductName, Price " +
                      "FROM Product " +
                      "ORDER BY Price";
        
        return executeQuery(query);
    }
    
    // Query 7: Customers in a city
    public List<Map<String, Object>> getCustomersInCity(String city) throws SQLException {
        String query = "SELECT CustomerID, Name AS CustomerName FROM Customer WHERE City = ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, city);
        return executeQuery(ps);
    }
    
    // Query 8: Sale of particular product
    public List<Map<String, Object>> getSaleOfProduct(String productName) throws SQLException {
        String query = "SELECT p.ProductID, p.Name AS ProductName, " +
                      "SUM(b.Quantity) AS TotalSold, " +
                      "SUM(b.Quantity * p.Price) AS TotalRevenue " +
                      "FROM Product p " +
                      "JOIN Business b ON p.ProductID = b.ProductID " +
                      "WHERE p.Name = ? " +
                      "GROUP BY p.ProductID, p.Name";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, productName);
        return executeQuery(ps);
    }
    
    // Query 9: Sale of particular category
    public List<Map<String, Object>> getSaleOfCategory(String category) throws SQLException {
        String query = "SELECT p.Category, SUM(b.Quantity) AS TotalSold, " +
                      "SUM(b.Quantity * p.Price) AS TotalRevenue " +
                      "FROM Product p " +
                      "JOIN Business b ON p.ProductID = b.ProductID " +
                      "WHERE p.Category = ? " +
                      "GROUP BY p.Category";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, category);
        return executeQuery(ps);
    }
    
    // Query 10: Top 5 customers by spending
    public List<Map<String, Object>> getTop5CustomersBySpending() throws SQLException {
        String query = "SELECT c.CustomerID, c.Name AS CustomerName, " +
                      "SUM(b.Quantity * p.Price) AS TotalSpent " +
                      "FROM Customer c " +
                      "JOIN Business b ON c.CustomerID = b.CustomerID " +
                      "JOIN Product p ON b.ProductID = p.ProductID " +
                      "GROUP BY c.CustomerID, c.Name " +
                      "ORDER BY TotalSpent DESC LIMIT 5";
        
        return executeQuery(query);
    }
    
    // Query 11: Total quantity sold per product
    public List<Map<String, Object>> getTotalQuantitySoldPerProduct() throws SQLException {
        String query = "SELECT p.ProductID, p.Name AS ProductName, " +
                      "SUM(b.Quantity) AS TotalSold " +
                      "FROM Product p " +
                      "JOIN Business b ON p.ProductID = b.ProductID " +
                      "GROUP BY p.ProductID, p.Name " +
                      "ORDER BY TotalSold DESC";
        
        return executeQuery(query);
    }
    
    // Query 12: Average price per category
    public List<Map<String, Object>> getAveragePricePerCategory() throws SQLException {
        String query = "SELECT p.Category, ROUND(AVG(p.Price), 2) AS AvgPrice " +
                      "FROM Product p " +
                      "GROUP BY p.Category " +
                      "ORDER BY AvgPrice DESC";
        
        return executeQuery(query);
    }
    
    // Query 13: Top-selling products per region
    public List<Map<String, Object>> getTopSellingProductsPerRegion() throws SQLException {
        String query = "SELECT RegionName, ProductName, TotalSold FROM (" +
                      "SELECT r.Region AS RegionName, p.Name AS ProductName, " +
                      "SUM(b.Quantity) AS TotalSold, " +
                      "RANK() OVER (PARTITION BY r.Region ORDER BY SUM(b.Quantity) DESC) AS rank_in_region " +
                      "FROM Regional_info r " +
                      "JOIN Customer c ON r.City = c.City " +
                      "JOIN Business b ON b.CustomerID = c.CustomerID " +
                      "JOIN Product p ON b.ProductID = p.ProductID " +
                      "GROUP BY r.Region, p.Name) ranked " +
                      "WHERE rank_in_region <= 5";
        
        return executeQuery(query);
    }
    
    // Query 14: Total revenue per category
    public List<Map<String, Object>> getTotalRevenuePerCategory() throws SQLException {
        String query = "SELECT p.Category, SUM(b.Quantity * p.Price) AS TotalRevenue " +
                      "FROM Product p " +
                      "JOIN Business b ON p.ProductID = b.ProductID " +
                      "GROUP BY p.Category " +
                      "ORDER BY TotalRevenue DESC";
        
        return executeQuery(query);
    }
    
    // Query 15: Campaigns with highest ROI
    public List<Map<String, Object>> getCampaignsWithHighestROI(int limit) throws SQLException {
        String query = "SELECT * FROM Campaign_ROI ORDER BY ROI DESC LIMIT ?";
        
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, limit);
        return executeQuery(ps);
    }
    
    // Query 16: Top 10 customers by year
    public List<Map<String, Object>> getTop10CustomersByYear() throws SQLException {
        String query = "SELECT YEAR(b.PDate) AS Year, b.CustomerID, c.Name, " +
                      "SUM(b.Quantity) AS TotalProductsBought " +
                      "FROM Business b " +
                      "JOIN Customer c ON b.CustomerID = c.CustomerID " +
                      "GROUP BY YEAR(b.PDate), b.CustomerID, c.Name " +
                      "HAVING SUM(b.Quantity) = (" +
                      "SELECT MAX(SUM(b2.Quantity)) " +
                      "FROM Business b2 " +
                      "WHERE YEAR(b2.PDate) = YEAR(b.PDate) " +
                      "GROUP BY b2.CustomerID) " +
                      "ORDER BY Year DESC LIMIT 10";
        
        return executeQuery(query);
    }
    
    // Query 17: Products in stock but not sold in last month
    public List<Map<String, Object>> getProductsNotSoldLastMonth() throws SQLException {
        String query = "SELECT p.ProductID, p.Name, p.Category, p.Stock, p.Price " +
                      "FROM Product p " +
                      "WHERE p.Stock > 0 " +
                      "AND p.ProductID NOT IN (" +
                      "SELECT DISTINCT b.ProductID FROM Business b " +
                      "WHERE b.PDate >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
                      "ORDER BY p.Stock DESC";
        
        return executeQuery(query);
    }
    
    // Query 18: Trending products
    public List<Map<String, Object>> getTrendingProducts() throws SQLException {
        String query = "SELECT * FROM TrendingProducts " +
                      "WHERE TrendStatus = 'Trending' " +
                      "ORDER BY TotalRecentSales DESC";
        
        return executeQuery(query);
    }
    
    // Query 19: High bounce-rate products
    public List<Map<String, Object>> getHighBounceRateProducts() throws SQLException {
        String query = "SELECT p.ProductID, p.Name, a.Impressions, " +
                      "IFNULL(SUM(b.Quantity), 0) AS TotalSales, " +
                      "ROUND((IFNULL(SUM(b.Quantity), 0) / a.Impressions) * 100, 2) AS ConversionRate " +
                      "FROM Product p " +
                      "JOIN Ads a ON p.ProductID = a.ProductID " +
                      "LEFT JOIN Business b ON p.ProductID = b.ProductID " +
                      "AND b.PDate >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH) " +
                      "GROUP BY p.ProductID, p.Name, a.Impressions " +
                      "HAVING ConversionRate < 5 " +
                      "ORDER BY ConversionRate";
        
        return executeQuery(query);
    }
    
    // Query 20: Campaign reports per region
    public List<Map<String, Object>> getCampaignReportsPerRegion() throws SQLException {
        String query = "SELECT Region, COUNT(CampaignName) AS NumCampaigns, " +
                      "ROUND(AVG(ROI), 2) AS AvgROI " +
                      "FROM CampaignReportsPerRegion " +
                      "GROUP BY Region " +
                      "ORDER BY AvgROI DESC";
        
        return executeQuery(query);
    }
    
    // Query 21: Ads running at loss
    public List<Map<String, Object>> getAdsRunningAtLoss() throws SQLException {
        String query = "SELECT AdID, Platform, Revenue, AdCost, ROI " +
                      "FROM Ad_Performance_View " +
                      "WHERE ROI < 0 " +
                      "ORDER BY ROI ASC";
        
        return executeQuery(query);
    }
    
    // Query 22: Ads with high conversion rates
    public List<Map<String, Object>> getAdsWithHighConversion() throws SQLException {
        String query = "SELECT AdID, Platform, ConversionRate, Impressions, Conversions " +
                      "FROM Ad_Performance_View " +
                      "WHERE ConversionRate > 10 " +
                      "ORDER BY ConversionRate DESC";
        
        return executeQuery(query);
    }
    
    // Query 23: Call procedure for discounted orders
    public void callDiscountedOrderProcedure() throws SQLException {
        String query = "{CALL Calc_Discounted_Order()}";
        CallableStatement cs = connection.prepareCall(query);
        cs.execute();
        cs.close();
    }
    
    // Query 24: Products with high sales but low stock (Restock Priority)
    public List<Map<String, Object>> getRestockPriorityList() throws SQLException {
        String query = "SELECT p.ProductID, p.Name AS ProductName, " +
                      "SUM(s.Quantity) AS Total_Sold_LastMonth, " +
                      "p.Stock AS Current_Stock, " +
                      "ROUND(SUM(s.Quantity) / NULLIF(p.Stock + SUM(s.Quantity), 0) * 100, 2) AS Demand_Percentage, " +
                      "c.Name AS CategoryName " +
                      "FROM Product p " +
                      "JOIN Business s ON p.ProductID = s.ProductID " +
                      "JOIN Category c ON p.CategoryID = c.CategoryID " +
                      "WHERE s.PDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) " +
                      "GROUP BY p.ProductID, p.Name, p.Stock, c.Name " +
                      "HAVING p.Stock < 20 AND Total_Sold_LastMonth > 50 " +
                      "ORDER BY Demand_Percentage DESC";
        
        return executeQuery(query);
    }
    
    // Query 25: Low stock alerts
    public List<Map<String, Object>> getLowStockAlerts() throws SQLException {
        String query = "SELECT * FROM LowStockAlerts ORDER BY RemainingStock ASC";
        return executeQuery(query);
    }
    
    // Helper method to execute query and return results as List of Maps
    private List<Map<String, Object>> executeQuery(String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        return executeQuery(ps);
    }
    
    private List<Map<String, Object>> executeQuery(PreparedStatement ps) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        
        rs.close();
        ps.close();
        return results;
    }
    
    // Method to print results
    public void printResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(80));
        for (Map<String, Object> row : results) {
            row.forEach((key, value) -> System.out.println(key + ": " + value));
            System.out.println("-".repeat(80));
        }
    }
}
