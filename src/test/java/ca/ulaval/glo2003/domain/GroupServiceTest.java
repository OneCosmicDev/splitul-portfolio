package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.DashboardDTO;
import ca.ulaval.glo2003.data.GroupRepository;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.HashMap;

import ca.ulaval.glo2003.domain.Exceptions.*;

public class GroupServiceTest {
    private static final String GROUP_NAME = "testGroup";
    private static final String NON_EXISTING_GROUP = "nonExistingGroup";
    private static final String MEMBER_NAME = "Crystale";
    private static final String SECOND_MEMBER_NAME = "justin";
    private static final String NON_MEMBER_NAME = "peter";
    private static final String EXPENSE_DESCRIPTION = "Groceries";
    private static final double EXPENSE_AMOUNT = 50.0;
    private static final String EXPENSE_DATE = "2023-01-01";
    private static final String FUTURE_DATE = "2100-01-01";
    private static final String INVALID_DATE_FORMAT = "01/01/2023";
    private static final double NEGATIVE_AMOUNT = -50.0;
    private static final int VALIDTIMESLICE = 1;
    private static final String VALIDTIMEFORMAT = "A";
    private static final double EQUAL_POURCENTAGE = 50.0;

    private GroupService service;
    private GroupRepository repository;
    private ExpenseSplit validSplit;

    @BeforeEach
    public void setUp() {
        repository = GroupRepositoryInMemory.getInstance();
        repository.clear();
        service = new GroupService(repository);

        validSplit = new ExpenseSplit();
        validSplit.addMemberShare(MEMBER_NAME, EQUAL_POURCENTAGE);
        validSplit.addMemberShare(SECOND_MEMBER_NAME, EQUAL_POURCENTAGE);
    }

    @Test
    public void whenAddMemberToExistingGroup_thenMemberIsAdded() {
        Group group = service.createGroup(GROUP_NAME);
        Group updatedGroup = service.addMember(GROUP_NAME, MEMBER_NAME);
        assertTrue(updatedGroup.hasMember(MEMBER_NAME));
    }

    @Test
    public void whenCreateGroup_thenGroupExists() {
        Group group = service.createGroup(GROUP_NAME);
        assertEquals(GROUP_NAME, group.getName());
    }

    @Test
    public void whenDeleteExistingGroup_thenGroupIsDeleted() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);

        service.deleteGroup(GROUP_NAME, MEMBER_NAME);

        assertThrows(GroupNotFoundException.class, () -> service.getGroup(GROUP_NAME));
    }

    @Test
    public void whenDeleteNonExistingGroup_thenThrowsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> service.deleteGroup(NON_EXISTING_GROUP, MEMBER_NAME));
    }

    @Test
    public void whenNonMemberTriesToDeleteGroup_thenThrowsMemberNotFoundException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);

        assertThrows(MemberNotFoundException.class, () -> service.deleteGroup(GROUP_NAME, NON_MEMBER_NAME));
    }

    @Test
    public void whenDeleteGroupWithUnresolvedDebts_thenThrowsGroupHasUnresolvedDebtsException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        service.addMember(GROUP_NAME, SECOND_MEMBER_NAME);
        service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, validSplit);

        assertThrows(GroupHasUnresolvedDebtsException.class, () -> service.deleteGroup(GROUP_NAME, MEMBER_NAME));
    }

    @Test
    public void whenAddExpenseToExistingGroup_thenExpenseIsAdded() {
        Group group = service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        service.addMember(GROUP_NAME, SECOND_MEMBER_NAME);

        Expense expense = service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, validSplit);

        assertAll(
                () -> assertNotNull(expense.getId()),
                () -> assertEquals(EXPENSE_DESCRIPTION, expense.getDescription()),
                () -> assertEquals(EXPENSE_AMOUNT, expense.getAmount()),
                () -> assertEquals(LocalDate.parse(EXPENSE_DATE), expense.getPurchaseDate()),
                () -> assertEquals(MEMBER_NAME, expense.getPaidBy()),
                () -> assertSame(validSplit, expense.getSplit())
        );
    }

    @Test
    public void whenAddExpenseToNonExistingGroup_thenThrowsException() {
        assertThrows(GroupNotFoundException.class, () ->
                service.addExpense(NON_EXISTING_GROUP, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, validSplit));
    }

    @Test
    public void whenAddExpenseWithNonExistingMember_thenThrowsException() {
        service.createGroup(GROUP_NAME);

        assertThrows(MemberNotFoundException.class, () ->
                service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, validSplit));
    }

    @Test
    public void whenAddExpenseWithNegativeAmount_thenThrowsException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        service.addMember(GROUP_NAME, SECOND_MEMBER_NAME);

        assertThrows(InvalidAmountException.class, () ->
                service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, NEGATIVE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, validSplit));
    }

    @Test
    public void whenAddExpenseWithFutureDate_thenThrowsException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        service.addMember(GROUP_NAME, SECOND_MEMBER_NAME);

        assertThrows(InvalidDateException.class, () ->
                service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, FUTURE_DATE, MEMBER_NAME, validSplit));
    }

    @Test
    public void whenAddExpenseWithInvalidDateFormat_thenThrowsException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        service.addMember(GROUP_NAME, SECOND_MEMBER_NAME);

        assertThrows(InvalidDateFormatException.class, () ->
                service.addExpense(GROUP_NAME, EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, INVALID_DATE_FORMAT, MEMBER_NAME, validSplit));
    }


    @Test
    public void whenSettleDebtToInexistingMember_thenThrowsMemberNotFoundException() {
        service.createGroup(GROUP_NAME);
        assertThrows(MemberNotFoundException.class, () -> service.settleDebts(GROUP_NAME, NON_MEMBER_NAME, MEMBER_NAME));
    }

    @Test
    public void whenSettleDebtToInexistingGroup_thenThrowsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> service.settleDebts(NON_EXISTING_GROUP, SECOND_MEMBER_NAME, MEMBER_NAME));
    }

    @Test
    public void whenGetDashboard_thenReturnsDashBoard() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);
        DashboardDTO dashboard = service.getDashboardAsDTO(GROUP_NAME,MEMBER_NAME,VALIDTIMEFORMAT,VALIDTIMESLICE);
        DashboardDTO emptyDashboard = new DashboardDTO(new HashMap(),null,0.0,new HashMap());

        assertEquals(dashboard,emptyDashboard);
    }
    @Test
    public void whenGetDashboardWithInvalidMember_thenThrowMemberNOtFoundException() {
        service.createGroup(GROUP_NAME);
        service.addMember(GROUP_NAME, MEMBER_NAME);

        assertThrows(MemberNotFoundException.class,
                () -> service.getDashboardAsDTO(GROUP_NAME,NON_MEMBER_NAME,VALIDTIMEFORMAT,VALIDTIMESLICE)) ;

    }

}
