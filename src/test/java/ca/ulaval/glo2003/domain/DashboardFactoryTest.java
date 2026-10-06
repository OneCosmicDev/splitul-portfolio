package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.DashboardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardFactoryTest {
    private static final String GROUPNAME = "group1";
    private static final String[] MEMBERNAMES = {"member1", "member2", "member3"};
    private static final String[] EXPENSECATEGORYS = {"food", "drink", "car"};
    private static final LocalDate DATENOW = LocalDate.now();
    private static final LocalDate QUARTERAGO = LocalDate.now().minusMonths(5);
    private static final LocalDate MONTHAGO = LocalDate.now().minusMonths(1);
    private static final LocalDate YEARAGO = LocalDate.now().minusYears(1);
    private static final String YEARFORMAT = "A";
    private static final String MONTHFORMAT = "M";
    private static final String INVALIDFORMAT = "INVALID";
    private static final int VALIDTIMESLICE = 12;
    private static final int ONETIMESLICE = 1;
    private static final int INVALIDTIMESLICE = -1;
    private static final double AMOUNT_1 = 30.0;
    private static final double AMOUNT_2 = 60.0;
    private static final double EXPECTED_BIGGEST_SPENDER_AMOUNT = 60.0;
    private static final int EXPECTED_CATEGORY_COUNT = 2;
    private static final int EXPECTED_EMPTY_CATEGORY_COUNT = 0;
    private static final String SPLIT_METHOD = "equally";

    private Group group;
    private Group expenseLessGroup;

    @BeforeEach
    public void setUp() {
        group = new Group(GROUPNAME);
        expenseLessGroup = new Group(GROUPNAME);

        for (String name : MEMBERNAMES) {
            group.addMember(name);
            expenseLessGroup.addMember(name);
        }

        for (String expenseName : EXPENSECATEGORYS) {
            addExpenseToGroup(group, expenseName, AMOUNT_1, DATENOW, MEMBERNAMES[0]);
        }
    }

    private void addExpenseToGroup(Group group, String description, double amount, LocalDate date, String paidBy) {
        ExpenseSplit split = group.calculateExpenseSplit(SPLIT_METHOD, null);
        Expense expense = new Expense(description, amount, date, paidBy, split);
        group.addExpense(expense);
    }

    @Test
    public void WhenComputeWithValidGroup_thenReturnDashboardDTO() {
        assertInstanceOf(DashboardDTO.class, DashboardFactory.computeDTOfromGroup(group, YEARFORMAT, VALIDTIMESLICE));
    }

    @Test
    public void WhenComputeWithInvalidTimeFormat_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DashboardFactory.computeDTOfromGroup(group, INVALIDFORMAT, VALIDTIMESLICE));
    }

    @Test
    public void WhenComputeWithInvalidTimeslice_thenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DashboardFactory.computeDTOfromGroup(group, YEARFORMAT, INVALIDTIMESLICE));
    }

    @Test
    public void WhenComputeDashBoardWithNoExpense_thenBiggestSpenderIsNull() {
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);
        assertNull(dashboardDTO.biggestSpender());
    }

    @Test
    public void WhenComputeDashBoardWithNoExpense_thenBiggestSpenderAmountIs0() {
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);
        assertEquals(0.0, dashboardDTO.biggestSpenderAmount());
    }

    @Test
    public void WhenComputeDashBoardWithExpense_thenBiggestSpenderAmountIsNot0() {
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(group, YEARFORMAT, VALIDTIMESLICE);
        assertTrue(dashboardDTO.biggestSpenderAmount() > 0.0);
    }

    @Test
    public void WhenComputeDashBoardWithExpense_thenBiggestSpenderNameIsNotNull() {
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(group, YEARFORMAT, VALIDTIMESLICE);
        assertNotNull(dashboardDTO.biggestSpender());
    }

    @Test
    public void WhenComputeDashboardWithBiggestSpender_thenReturnsBiggestSpenderInDTO() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_2, DATENOW, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, DATENOW, MEMBERNAMES[1]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);
        assertEquals(MEMBERNAMES[0], dashboardDTO.biggestSpender());
    }

    @Test
    public void WhenComputeDashboard_thenReturnsBiggestSpenderAmount() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, DATENOW, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, DATENOW, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);

        assertEquals(EXPECTED_BIGGEST_SPENDER_AMOUNT, dashboardDTO.biggestSpenderAmount());
    }

    @Test
    public void WhenComputeDashboardWithOnlyLateExpense_thenExpensesIsEmpty() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, YEARAGO, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, YEARAGO, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, MONTHFORMAT, ONETIMESLICE);

        assertTrue(dashboardDTO.expenseByTimeslice().isEmpty());
    }

    @Test
    public void WhenComputeDashboardWithEarlyExpense_thenExpensesIsNotEmpty() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, QUARTERAGO, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, MONTHAGO, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, ONETIMESLICE);

        assertFalse(dashboardDTO.expenseByTimeslice().isEmpty());
    }

    @Test
    public void WhenComputeDashboardWithLateExpense_thenExpenseIsNotIncluded() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, YEARAGO, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, MONTHFORMAT, ONETIMESLICE);

        assertTrue(dashboardDTO.expenseByTimeslice().isEmpty());
    }

    @Test
    public void WhenComputeDashboardWithCategory_thenReturnMappedCategory() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, MONTHAGO, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[1], AMOUNT_1, MONTHAGO, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);

        assertEquals(EXPECTED_CATEGORY_COUNT, dashboardDTO.expensesByCategory().size());
    }

    @Test
    public void WhenComputeDashboardWithNoExpense_thenReturnEmptyCategoryMap() {
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);
        assertEquals(EXPECTED_EMPTY_CATEGORY_COUNT, dashboardDTO.expensesByCategory().size());
    }

    @Test
    public void WhenComputeDashboardWithExpense_thenReturnNotEmptyCategoryMap() {
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[0], AMOUNT_1, MONTHAGO, MEMBERNAMES[0]);
        addExpenseToGroup(expenseLessGroup, EXPENSECATEGORYS[1], AMOUNT_1, MONTHAGO, MEMBERNAMES[0]);
        DashboardDTO dashboardDTO = DashboardFactory.computeDTOfromGroup(expenseLessGroup, YEARFORMAT, VALIDTIMESLICE);
        assertFalse(dashboardDTO.expensesByCategory().isEmpty());
    }
}
