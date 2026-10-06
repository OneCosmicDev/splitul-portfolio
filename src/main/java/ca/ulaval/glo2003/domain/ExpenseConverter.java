package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.ExpenseDTO;
import ca.ulaval.glo2003.api.ExpenseSplitDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseConverter {
    public ExpenseDTO toDTO(Expense expense) {
        Map<String, Double> percentages = new HashMap<>();
        ExpenseSplit split = expense.getSplit();
        split.getParticipatingMembers().forEach(member ->
                percentages.put(member, split.getMemberShare(member)));

        return new ExpenseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getPurchaseDate(),
                expense.getPaidBy(),
                new ExpenseSplitDTO(percentages)
        );
    }

    public List<ExpenseDTO> toDTOs(List<Expense> expenses) {
        return expenses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ExpenseSplit fromDTO(Map<String, Double> splitDTO) {
        ExpenseSplit split = new ExpenseSplit();
        splitDTO.forEach(split::addMemberShare);
        return split;
    }
}
