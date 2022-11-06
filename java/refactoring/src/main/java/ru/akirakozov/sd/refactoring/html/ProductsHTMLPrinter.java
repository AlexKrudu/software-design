package ru.akirakozov.sd.refactoring.html;

import ru.akirakozov.sd.refactoring.models.Product;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductsHTMLPrinter {

    private final PrintWriter printer;

    public ProductsHTMLPrinter(PrintWriter printer) {
        this.printer = printer;
    }

    public void printProductsList(List<Product> products) {
        printer.println("<html><body>");
        printer.println(products.stream().map((p) -> p.getName() + "\t" + p.getPrice() + "</br>").collect(Collectors.joining()));
        printer.println("</body></html>");
    }

    public void printOK(){
        printer.println("OK");
    }

    public void printAggResult(long price, String header){
        printer.println("<html><body>");
        printer.println(header);
        printer.println(price);
        printer.println("</body></html>");
    }

    public void printProductSelectResult(Optional<Product> product, String header){
        printer.println("<html><body>");
        printer.println(String.format("<h1>%s</h1>", header));
        product.ifPresent(value -> printer.println(value.getName() + "\t" + value.getPrice() + "</br>"));
        printer.println("</body></html>");
    }

}
