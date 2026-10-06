package ca.ulaval.glo2003.domain;

import java.util.List;

public record ExpenseHistory(List<Expense> expenses, double total) { }
