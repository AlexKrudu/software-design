package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.models.Product;

import java.util.List;
import java.util.Optional;

public interface IProductDatabaseManager {

    enum ProductQuery {COUNT, SUM, MIN, MAX};

    void addProduct(Product product) throws RuntimeException;

    List<Product> getProducts() throws RuntimeException;

    long getAggStats(ProductQuery query) throws RuntimeException;

    Optional<Product> getProductWithOption(ProductQuery opt) throws RuntimeException;

}
