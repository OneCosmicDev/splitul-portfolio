package ca.ulaval.glo2003.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class GroupTest {

    private static final String GROUP_NAME = "testGroup";
    private static final String FIRST_MEMBER = "Peter";
    private static final String SECOND_MEMBER = "Crystale";
    private static final String THIRD_MEMBER = "Criquette";

    private static final String EXPENSE_DESCRIPTION = "Groceries";
    private static final String EXPENSE_DESCRIPTION_1 = "Groceries";
    private static final String EXPENSE_DESCRIPTION_2 = "Rent";
    private static final LocalDate DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate DATE_1 = LocalDate.of(2023, 1, 1);
    private static final LocalDate DATE_2 = LocalDate.of(2023, 1, 2);

    private static final double AMOUNT = 60.0;
    private static final double AMOUNT_SPLITED_IN_THREE = 20.0;
    private static final double AMOUNT_1 = 60.0;
    private static final double AMOUNT_1_SPLITED = 30.0;
    private static final double AMOUNT_2 = 120.0;
    private static final double AMOUNT_2_SPLITED = 60.0;
    private static final double NO_DEBT = 0.0;
    private static final double SHARE_50 = 50.0;
    private static final double SHARE_70 = 70.0;
    private static final double SHARE_30 = 30.0;
    private static final double SHARE_40 = 40.0;
    private static final double SHARE_60 = 60.0;
    private static final double SHARE_100 = 100.0;
    private static final double DELTA = 0.01;

    private static final String SPLIT_METHOD_EQUALLY = "equally";
    private static final String SPLIT_METHOD_SKEWED = "skewed";
    private static final String SPLIT_METHOD_CUSTOM = "custom";
    private static final String SPLIT_METHOD_INVALID = "invalid";
    private static final String INVALID_SPLIT_ERROR = "Invalid repartition Method";

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group(GROUP_NAME);
        group.addMember(FIRST_MEMBER);
        group.addMember(SECOND_MEMBER);
    }

    @Test
    void givenNewMember_whenAdded_thenIsInGroup() {
        Group testGroup = new Group(GROUP_NAME);
        testGroup.addMember(THIRD_MEMBER);
        assertTrue(testGroup.hasMember(THIRD_MEMBER));
    }

    @Test
    void givenMultipleMembers_whenGetMembersAsArray_thenCorrectSizeAndContent() {
        GroupMember[] members = group.getMembersAsArray();

        assertEquals(2, members.length);
        assertTrue(group.hasMember(FIRST_MEMBER));
        assertTrue(group.hasMember(SECOND_MEMBER));
    }

    @Test
    void givenExistingMember_whenAddedAgain_thenThrowsException() {
        assertThrows(Exceptions.MemberAlreadyExistsException.class, () -> group.addMember(FIRST_MEMBER));
    }

    @Test
    void givenExpenseWithValidPayer_whenAdded_thenExpenseIsRegistered() {
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(FIRST_MEMBER, SHARE_100);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, AMOUNT, DATE, FIRST_MEMBER, split);
        group.addExpense(expense);

        assertEquals(1, group.getExpenses().size());
        assertTrue(group.getExpenses().contains(expense));
    }

    @Test
    public void whenAddExpense_thenDebtsAreCalculatedCorrectly() {
        ExpenseSplit split1 = new ExpenseSplit();
        split1.addMemberShare(FIRST_MEMBER, SHARE_50);
        split1.addMemberShare(SECOND_MEMBER, SHARE_50);
        Expense expense1 = new Expense(EXPENSE_DESCRIPTION_1, AMOUNT_1, DATE_1, FIRST_MEMBER, split1);
        group.addExpense(expense1);
        assertEquals(AMOUNT_1_SPLITED, group.getMemberByName(SECOND_MEMBER).getAmountsDueByName().get(FIRST_MEMBER));

        ExpenseSplit split2 = new ExpenseSplit();
        split2.addMemberShare(FIRST_MEMBER, SHARE_50);
        split2.addMemberShare(SECOND_MEMBER, SHARE_50);
        Expense expense2 = new Expense(EXPENSE_DESCRIPTION_2, AMOUNT_2, DATE_2, SECOND_MEMBER, split2);
        group.addExpense(expense2);

        assertEquals(AMOUNT_2_SPLITED, group.getMemberByName(FIRST_MEMBER).getAmountsDueByName().get(SECOND_MEMBER));
        assertEquals(AMOUNT_1_SPLITED, group.getMemberByName(SECOND_MEMBER).getAmountsDueByName().get(FIRST_MEMBER));
    }

    @Test
    public void whenNewMemberJoinsAfterExpense_thenTheirDebtIsZero() {
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(FIRST_MEMBER, SHARE_50);
        split.addMemberShare(SECOND_MEMBER, SHARE_50);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, AMOUNT, DATE, SECOND_MEMBER, split);
        group.addExpense(expense);

        group.addMember(THIRD_MEMBER);

        assertTrue(group.getMemberByName(THIRD_MEMBER).getDebts().isEmpty());
    }

    @Test
    public void whenAddExpenseWithNonExistingMember_thenThrowsException() {
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(FIRST_MEMBER, SHARE_50);
        split.addMemberShare(THIRD_MEMBER, SHARE_50);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, AMOUNT, DATE, THIRD_MEMBER, split);

        assertThrows(Exceptions.MemberNotFoundException.class, () -> group.addExpense(expense));
    }

    @Test
    public void whenSettleDebtsToNonIndebtedMember_thenKeepDebts() {
        group.addMember(THIRD_MEMBER);
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(FIRST_MEMBER, 33.33);
        split.addMemberShare(SECOND_MEMBER, 33.33);
        split.addMemberShare(THIRD_MEMBER, 33.34);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, AMOUNT, DATE, FIRST_MEMBER, split);
        group.addExpense(expense);

        group.settleDebts(SECOND_MEMBER, THIRD_MEMBER);
        group.settleDebts(THIRD_MEMBER, SECOND_MEMBER);

        assertEquals(AMOUNT_SPLITED_IN_THREE, group.getMemberByName(SECOND_MEMBER).getAmountDueToMember(group.getMemberByName(FIRST_MEMBER)), DELTA);
        assertEquals(AMOUNT_SPLITED_IN_THREE, group.getMemberByName(THIRD_MEMBER).getAmountDueToMember(group.getMemberByName(FIRST_MEMBER)), DELTA);
    }

    @Test
    public void whenSettleDebtsToIndebtedMember_thenClearDebtsToMember() {
        group.addMember(THIRD_MEMBER);
        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(SECOND_MEMBER, SHARE_50);
        split.addMemberShare(THIRD_MEMBER, SHARE_50);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, AMOUNT, DATE, SECOND_MEMBER, split);
        group.addExpense(expense);

        group.settleDebts(THIRD_MEMBER, SECOND_MEMBER);

        assertEquals(NO_DEBT, group.getMemberByName(THIRD_MEMBER).getAmountDueToMember(group.getMemberByName(SECOND_MEMBER)));
    }

    @Test
    void whenCalculateExpenseSplitWithEqually_thenCreateCorrectSplit() {
        ExpenseSplit split = group.calculateExpenseSplit(SPLIT_METHOD_EQUALLY, null);

        assertEquals(2, split.getParticipatingMembers().size());
        assertEquals(SHARE_50, split.getMemberShare(FIRST_MEMBER), DELTA);
        assertEquals(SHARE_50, split.getMemberShare(SECOND_MEMBER), DELTA);
    }

    @Test
    void whenCalculateExpenseSplitWithSkewed_thenCreateCorrectSplit() {
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(FIRST_MEMBER, SHARE_70);

        ExpenseSplit split = group.calculateExpenseSplit(SPLIT_METHOD_SKEWED, customPercentages);

        assertEquals(2, split.getParticipatingMembers().size());
        assertEquals(SHARE_70, split.getMemberShare(FIRST_MEMBER), DELTA);
        assertEquals(SHARE_30, split.getMemberShare(SECOND_MEMBER), DELTA);
    }

    @Test
    void whenCalculateExpenseSplitWithCustom_thenCreateCorrectSplit() {
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(FIRST_MEMBER, SHARE_40);
        customPercentages.put(SECOND_MEMBER, SHARE_60);

        ExpenseSplit split = group.calculateExpenseSplit(SPLIT_METHOD_CUSTOM, customPercentages);

        assertEquals(2, split.getParticipatingMembers().size());
        assertEquals(SHARE_40, split.getMemberShare(FIRST_MEMBER), DELTA);
        assertEquals(SHARE_60, split.getMemberShare(SECOND_MEMBER), DELTA);
    }

    @Test
    void whenCalculateExpenseSplitWithInvalidMethod_thenThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            group.calculateExpenseSplit(SPLIT_METHOD_INVALID, null);
        });

        assertTrue(exception.getMessage().contains(INVALID_SPLIT_ERROR));
    }
}
