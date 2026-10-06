package ca.ulaval.glo2003.api;

import java.time.LocalDate;
import java.util.UUID;

public class ExpenseDTO {
    private UUID id;
    private String description;
    private double amount;
    private LocalDate purchaseDate;
    private String paidBy;
    private ExpenseSplitDTO split;

    public ExpenseDTO(UUID id, String description, double amount, LocalDate purchaseDate, String paidBy, ExpenseSplitDTO split) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.paidBy = paidBy;
        this.split = split;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public ExpenseSplitDTO getSplit() {
        return split;
    }
}
