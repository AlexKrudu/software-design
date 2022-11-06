package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.db.ProductDatabaseManager;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        IProductDatabaseManager dbManager = new ProductDatabaseManager("jdbc:sqlite:test.db");
        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(dbManager)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(dbManager)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(dbManager)),"/query");

        server.start();
        server.join();
    }
}
