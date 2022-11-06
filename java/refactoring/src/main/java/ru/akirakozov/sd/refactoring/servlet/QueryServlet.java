package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.db.ProductDatabaseManager;
import ru.akirakozov.sd.refactoring.html.ProductsHTMLPrinter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    public QueryServlet(IProductDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        ProductsHTMLPrinter printer = new ProductsHTMLPrinter(response.getWriter());
        try {
            IProductDatabaseManager.ProductQuery productQuery =
                    IProductDatabaseManager.ProductQuery.valueOf(command.toUpperCase());
            switch (productQuery) {
                case MAX:
                case MIN:
                    printer.printProductSelectResult(
                            dbManager.getProductWithOption(productQuery),
                            String.format("Product with %s price: ", productQuery.toString().toLowerCase()));
                    break;
                case SUM:
                case COUNT:
                    String header = productQuery == IProductDatabaseManager.ProductQuery.SUM ?
                            "Summary price: " : "Number of products: ";
                    printer.printAggResult(dbManager.getAggStats(productQuery), header);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Unsupported operation: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    IProductDatabaseManager dbManager;
}
