package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.db.ProductDatabaseManager;

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

    public QueryServlet(IProductDatabaseManager dbManager){
        this.dbManager = dbManager;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        try {
            IProductDatabaseManager.ProductQuery productQuery =
                    IProductDatabaseManager.ProductQuery.valueOf(command.toUpperCase());
            switch (productQuery){
                case MAX:
                case MIN:
                    dbManager.getAggStats(productQuery);
                    break;
                case SUM:
                case COUNT:
                    dbManager.getProductWithOption(productQuery);
                    break;
            }
        } catch (IllegalArgumentException e){
            throw new IOException("Unsupported operation: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    IProductDatabaseManager dbManager;
}
