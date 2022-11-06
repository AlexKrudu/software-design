package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.db.MockProductDatabaseManager;
import ru.akirakozov.sd.refactoring.models.Product;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.mockito.Mockito.when;

public class ProductsTests {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AddProductServlet addProductServlet;

    private GetProductsServlet getProductsServlet;

    private QueryServlet queryServlet;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        IProductDatabaseManager databaseManager = new MockProductDatabaseManager();
        addProductServlet = new AddProductServlet(databaseManager);
        getProductsServlet = new GetProductsServlet(databaseManager);
        queryServlet = new QueryServlet(databaseManager);
    }

    private String addProduct(Product product) throws IOException {
        when(request.getParameter("name")).thenReturn(product.getName());
        when(request.getParameter("price")).thenReturn(String.valueOf(product.getPrice()));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        addProductServlet.doGet(request, response);
        writer.flush();
        return stringWriter.toString();
    }

    private String getProducts() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        getProductsServlet.doGet(request, response);
        writer.flush();
        return stringWriter.toString();
    }

    private String getQueryResult(String query) throws IOException{
        when(request.getParameter("command")).thenReturn(query);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        queryServlet.doGet(request, response);
        writer.flush();
        return stringWriter.toString();
    }

    @Test
    public void testAddProductSimple() throws IOException {
        String response = addProduct(new Product("iphone6", 600));
        Assertions.assertEquals(response, "OK\n");
    }

    @Test
    public void testGetEmptyProducts() throws IOException {
        String response = getProducts();
        Assertions.assertEquals(response, "<html><body>\n" +
                "\n" +
                "</body></html>\n");
    }

    @Test
    public void testGetAfterMultipleAdd() throws IOException {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            Product curProduct = new Product("product" + i, i + 100);
            productList.add(curProduct);
            String response = addProduct(curProduct);
            Assertions.assertEquals(response, "OK\n");
        }
        String getProductsResp = getProducts();
        String[] products = getProductsResp.split("\\r?\\n");
        Assertions.assertEquals(102, products.length);
        Assertions.assertEquals(products[0], "<html><body>");
        Assertions.assertEquals(products[101], "</body></html>");
        for (int i = 1; i < 101; i++){
            Product cur_product = productList.get(i - 1);
            Assertions.assertEquals(String.format("%s\t%s</br>", cur_product.getName(), cur_product.getPrice()), products[i]);
        }
    }

    @Test
    public void testQueries() throws IOException{
        for (int i = 0; i < 100; i++){
            Product curProduct = new Product("product" + i, i + 100);
            String response = addProduct(curProduct);
            Assertions.assertEquals(response, "OK\n");
        }
        String[] minResponse = getQueryResult("min").split("\\r?\\n");
        Assertions.assertEquals(4, minResponse.length);
        Assertions.assertEquals(minResponse[0], "<html><body>");
        Assertions.assertEquals(minResponse[3], "</body></html>");
        Assertions.assertEquals(minResponse[1], "<h1>Product with min price: </h1>");
        Assertions.assertEquals(minResponse[2], "product0\t100</br>");

        String[] maxResponse = getQueryResult("max").split("\\r?\\n");
        Assertions.assertEquals(4, maxResponse.length);
        Assertions.assertEquals(maxResponse[0], "<html><body>");
        Assertions.assertEquals(maxResponse[3], "</body></html>");
        Assertions.assertEquals(maxResponse[1], "<h1>Product with max price: </h1>");
        Assertions.assertEquals(maxResponse[2], "product99\t199</br>");

        String[] sumResponse = getQueryResult("sum").split("\\r?\\n");
        Assertions.assertEquals(4, sumResponse.length);
        Assertions.assertEquals(sumResponse[0], "<html><body>");
        Assertions.assertEquals(sumResponse[3], "</body></html>");
        Assertions.assertEquals(sumResponse[1], "Summary price: ");
        Assertions.assertEquals(sumResponse[2], String.valueOf((100 + 199) * (100 / 2)));

        String[] countResponse = getQueryResult("count").split("\\r?\\n");
        Assertions.assertEquals(4, countResponse.length);
        Assertions.assertEquals(countResponse[0], "<html><body>");
        Assertions.assertEquals(countResponse[3], "</body></html>");
        Assertions.assertEquals(countResponse[1], "Number of products: ");
        Assertions.assertEquals(countResponse[2], String.valueOf(100));
    }

}