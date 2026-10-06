package ca.ulaval.glo2003.api;

import java.util.List;

public record ExpenseHistoryDTO(double total, List<ExpenseDTO> expenses) { }