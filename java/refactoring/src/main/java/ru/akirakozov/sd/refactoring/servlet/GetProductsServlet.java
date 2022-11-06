package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.html.ProductsHTMLPrinter;
import ru.akirakozov.sd.refactoring.models.Product;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    public GetProductsServlet(IProductDatabaseManager dbManager){
        this.dbManager = dbManager;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ProductsHTMLPrinter printer = new ProductsHTMLPrinter(response.getWriter());
        List<Product> productList = dbManager.getProducts();
        printer.printProductsList(productList);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    IProductDatabaseManager dbManager;
}
