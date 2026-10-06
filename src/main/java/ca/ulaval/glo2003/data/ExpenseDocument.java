package ca.ulaval.glo2003.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Map;
import java.util.UUID;

@Entity("expenses")
public class ExpenseDocument {
    @Id
    private String id;
    private String description;
    private double amount;
    private String purchaseDate;
    private String paidBy;
    private Map<String, Double> split;

    public ExpenseDocument() {}

    public ExpenseDocument(String description, double amount, String purchaseDate, String paidBy, Map<String, Double> split) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.paidBy = paidBy;
        this.split = split;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public Map<String, Double> getSplit() {
        return split;
    }
}
