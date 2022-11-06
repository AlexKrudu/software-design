package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDatabaseManager implements IProductDatabaseManager {

    public ProductDatabaseManager(String dbAddr) throws SQLException {
        this.dbAddr = dbAddr;
        try (Connection c = DriverManager.getConnection(dbAddr)) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        }
    }

    @Override
    public void addProduct(Product product) throws RuntimeException {
        try {
            try (Connection c = DriverManager.getConnection(dbAddr)) {
                String sql = "INSERT INTO PRODUCT " +
                        "(NAME, PRICE) VALUES (\"" + product.getName() + "\"," + product.getPrice() + ")";
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to add product info to db due to:" + e.getMessage());
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            List<Product> resultProducts;
            try (Connection c = DriverManager.getConnection(dbAddr)) {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT");
                resultProducts = parseProductsFromRS(rs);
                rs.close();
                stmt.close();
            }
            return resultProducts;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getAggStats(ProductQuery query) {
        String aggFunc = getAggFuncString(query);
        String aggColumn = "agg_val";

        try {
            long aggPrice;
            try (Connection c = DriverManager.getConnection(dbAddr)) {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(String.format("SELECT %s as %s FROM PRODUCT", aggFunc, aggColumn));
                aggPrice = rs.getLong("aggColumn");
                rs.close();
                stmt.close();
            }
            return aggPrice;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Product> getProductWithOption(ProductQuery opt) {
        try {
            String selectFuncOption = getSelectFuncOpt(opt);
            Optional<Product> resultProduct;
            try (Connection c = DriverManager.getConnection(dbAddr)) {
                Statement stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM PRODUCT ORDER BY PRICE %s LIMIT 1",
                        selectFuncOption));

                resultProduct = parseProductsFromRS(rs).stream().findFirst();

                rs.close();
                stmt.close();
            }
            return resultProduct;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Product> parseProductsFromRS(ResultSet resultSet) throws SQLException {
        List<Product> resultProducts = new ArrayList<>();
        while (resultSet.next()) {
            Product product = new Product
                    (resultSet.getString("name"),
                            resultSet.getLong("price"));
            resultProducts.add(product);
        }
        return resultProducts;
    }

    private String getAggFuncString(ProductQuery query) {
        switch (query) {
            case SUM:
                return "SUM(price)";
            case COUNT:
                return "COUNT(*)";
            case MIN:
            case MAX:
                throw new RuntimeException("ProductDatabaseManager::getAggStats received an unexpected query: " + query.toString());
        }
        return "";
    }

    private String getSelectFuncOpt(ProductQuery query) {
        switch (query) {
            case MAX:
                return "DESC";
            case MIN:
                return "";
            case COUNT:
            case SUM:
                throw new RuntimeException("ProductDatabaseManager::getProductWithOption received an unexpected query: " + query.toString());
        }
        return "";
    }
    private final String dbAddr;
}
