package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.ExpenseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseConverterTest {

    private static final String DESCRIPTION = "Groceries";
    private static final double AMOUNT = 50.0;
    private static final LocalDate DATE = LocalDate.of(2023, 1, 1);
    private static final String PAYER = "john";
    private static final String MEMBER = "jane";

    private static final String SECOND_DESCRIPTION = "Rent";
    private static final double SECOND_AMOUNT = 1000.0;
    private static final LocalDate SECOND_DATE = LocalDate.of(2023, 2, 1);
    private static final String SECOND_PAYER = "jane";

    private static final int EXPECTED_NUMBER_OF_EXPENSES = 2;
    private static final double SPLIT_PERCENTAGE = 50.0;

    private ExpenseConverter converter;
    private Expense expense;

    @BeforeEach
    void setUp() {
        converter = new ExpenseConverter();
        expense = createExpense(DESCRIPTION, AMOUNT, DATE, PAYER, MEMBER);
    }

    private Expense createExpense(String description, double amount, LocalDate date, String payer, String member) {
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(payer, SPLIT_PERCENTAGE);
        split.addMemberShare(member, SPLIT_PERCENTAGE);
        return new Expense(description, amount, date, payer, split);
    }

    @Test
    void givenExpense_whenConvertToDTO_thenDescriptionMatches() {
        ExpenseDTO dto = converter.toDTO(expense);
        assertEquals(DESCRIPTION, dto.getDescription());
    }

    @Test
    void givenExpense_whenConvertToDTO_thenAmountMatches() {
        ExpenseDTO dto = converter.toDTO(expense);
        assertEquals(AMOUNT, dto.getAmount());
    }

    @Test
    void givenExpense_whenConvertToDTO_thenPurchaseDateMatches() {
        ExpenseDTO dto = converter.toDTO(expense);
        assertEquals(DATE, dto.getPurchaseDate());
    }

    @Test
    void givenExpense_whenConvertToDTO_thenPaidByMatches() {
        ExpenseDTO dto = converter.toDTO(expense);
        assertEquals(PAYER, dto.getPaidBy());
    }

    @Test
    void givenExpense_whenConvertToDTO_thenSplitMatches() {
        ExpenseDTO dto = converter.toDTO(expense);
        expense.getSplit().getParticipatingMembers().forEach(member ->
                assertEquals(
                        expense.getSplit().getMemberShare(member),
                        dto.getSplit().getMemberPercentages().get(member)
                ));
    }

    @Test
    void givenMultipleExpenses_whenConvertToDTOs_thenAllAreConverted() {
        Expense rentExpense = createExpense(SECOND_DESCRIPTION, SECOND_AMOUNT, SECOND_DATE, SECOND_PAYER, PAYER);
        List<Expense> expenses = List.of(expense, rentExpense);

        List<ExpenseDTO> dtos = converter.toDTOs(expenses);

        assertEquals(EXPECTED_NUMBER_OF_EXPENSES, dtos.size());
    }
}
