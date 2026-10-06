package ca.ulaval.glo2003.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    private static final String DESCRIPTION = "Groceries";
    private static final double AMOUNT = 50.0;
    private static final LocalDate PURCHASE_DATE = LocalDate.of(2023, 1, 1);
    private static final String PAYER = "john";
    private static final String MEMBER = "jane";
    private static final double SHARE_1 = 60.0;
    private static final double SHARE_2 = 40.0;

    private Expense expense;
    private ExpenseSplit split;

    @BeforeEach
    void setUp() {
        split = new ExpenseSplit();
        split.addMemberShare(PAYER, SHARE_1);
        split.addMemberShare(MEMBER, SHARE_2);
        expense = new Expense(DESCRIPTION, AMOUNT, PURCHASE_DATE, PAYER, split);
    }

    @Test
    public void whenCreateExpense_thenGeneratesUUID() {
        UUID id = expense.getId();

        assertNotNull(id);
    }

    @Test
    void givenNewExpense_whenCreated_thenAllFieldsAreSetCorrectly() {
        assertEquals(DESCRIPTION, expense.getDescription());
        assertEquals(AMOUNT, expense.getAmount());
        assertEquals(PURCHASE_DATE, expense.getPurchaseDate());
        assertEquals(PAYER, expense.getPaidBy());
        assertSame(split, expense.getSplit());
    }
}
