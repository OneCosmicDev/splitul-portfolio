package ca.ulaval.glo2003.domain;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private final UUID id;
    private final String description;
    private final double amount;
    private final LocalDate purchaseDate;
    private final String paidBy;
    private final ExpenseSplit split;

    public Expense(String description, double amount, LocalDate purchaseDate, String paidBy, ExpenseSplit split) {
        if (split == null || !split.isValid()) {
            throw new InvalidSplitException();
        }
        this.id = UUID.randomUUID();
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

    public ExpenseSplit getSplit() {
        return split;
    }

    public double getAmountForMember(String memberName) {
        return amount * (split.getMemberShare(memberName) / 100.0);
    }

    public static class InvalidSplitException extends RuntimeException {
        public InvalidSplitException() {
            super("La répartition des dépenses est invalide");
        }
    }
}
