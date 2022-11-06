package ru.akirakozov.sd.refactoring.db;

import ru.akirakozov.sd.refactoring.db.IProductDatabaseManager;
import ru.akirakozov.sd.refactoring.models.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MockProductDatabaseManager implements IProductDatabaseManager {

    public MockProductDatabaseManager(){
        productsStorage = new ArrayList<>();
    }
    @Override
    public void addProduct(Product product) throws RuntimeException {
        productsStorage.add(product);
    }

    @Override
    public List<Product> getProducts() throws RuntimeException {
        return productsStorage;
    }

    @Override
    public long getAggStats(ProductQuery query) throws RuntimeException {
        switch (query){
            case SUM:
                return productsStorage.stream().mapToLong(Product::getPrice).sum();
            case COUNT:
                return productsStorage.size();
            case MAX:
            case MIN:
                throw new RuntimeException("Unexpected query: " + query);
        }
        return -1;
    }

    @Override
    public Optional<Product> getProductWithOption(ProductQuery opt) throws RuntimeException {
        switch (opt){
            case MIN:
                return productsStorage.stream().min(Comparator.comparing(Product::getPrice));
            case MAX:
                return productsStorage.stream().max(Comparator.comparing(Product::getPrice));
            case COUNT:
            case SUM:
                throw new RuntimeException("Unexpected query: " + opt);
        }
        return Optional.empty();
    }

    private final List<Product> productsStorage;
}
