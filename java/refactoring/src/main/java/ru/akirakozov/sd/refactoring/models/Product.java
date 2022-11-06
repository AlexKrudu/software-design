package ru.akirakozov.sd.refactoring.models;

public class Product {
    private final long price;
    private final String name;

    public long getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public Product(String name, long price){
        this.name = name;
        this.price = price;
    }
}
