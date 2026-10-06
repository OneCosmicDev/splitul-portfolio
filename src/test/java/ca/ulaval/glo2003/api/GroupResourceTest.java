package ca.ulaval.glo2003.api;

import ca.ulaval.glo2003.data.GroupRepository;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

@SuppressWarnings("resource")
public class GroupResourceTest {
    private static final String BASE_URL = "http://localhost:8080/groups/";
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_FORBIDDEN = 403;
    private static final int STATUS_CONFLICT = 409;
    private static final int STATUS_OK = 200;

    private static final String GROUP_NAME = "testGroup";
    private static final String NON_EXISTENT_GROUP = "nonexistent";
    private static final String MEMBER_NAME = "john";
    private static final String MEMBER_NAME_WITH_SPACE = "j ean";
    private static final String NON_MEMBER_NAME = "paul";
    private static final String SECOND_MEMBER_NAME = "Jean";
    private static final String THIRD_MEMBER_NAME = "Marc";

    private static final String EXPENSE_DESCRIPTION = "Groceries";
    private static final String SECOND_EXPENSE_DESCRIPTION = "Achat 1";
    private static final String THIRD_EXPENSE_DESCRIPTION = "Achat 2";
    private static final double EXPENSE_AMOUNT = 50.0;
    private static final double SECOND_EXPENSE_AMOUNT = 100.0;
    private static final double EXPECTED_TOTAL_EXPENSE = 150.0;
    private static final double NEGATIVE_EXPENSE_AMOUNT = -50.0;
    private static final String EXPENSE_DATE = "2023-01-01";
    private static final String SECOND_EXPENSE_DATE = "2023-02-01";
    private static final String FUTURE_EXPENSE_DATE = "2100-01-01";
    private static final String INVALID_EXPENSE_DATE = "01/01/2023";
    private static final String SPLIT_METHOD = "equally";

    private GroupResource resource;

    @BeforeEach
    public void setUp() {
        GroupRepository repository = GroupRepositoryInMemory.getInstance();
        repository.clear();
        this.resource = new GroupResource();
    }

    private GroupResource.CreateGroupRequest createGroupRequest(String name) {
        GroupResource.CreateGroupRequest request = new GroupResource.CreateGroupRequest();
        request.setName(name);
        return request;
    }

    private GroupResource.MemberRequest createMemberRequest(String memberName) {
        return new GroupResource.MemberRequest(memberName);
    }

    private GroupResource.CreateExpenseRequest createExpenseRequest(String description, double amount,
                                                                    String purchaseDate, String paidBy, String split) {
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription(description);
        request.setAmount(amount);
        request.setPurchaseDate(purchaseDate);
        request.setPaidBy(paidBy);
        
        Map<String, Double> splitMap = new HashMap<>();
        if ("equally".equals(split)) {
            if (paidBy.equals(MEMBER_NAME)) {
                splitMap.put(MEMBER_NAME, 50.0);
                splitMap.put(SECOND_MEMBER_NAME, 50.0);
            } else if (paidBy.equals(SECOND_MEMBER_NAME)) {
                splitMap.put(SECOND_MEMBER_NAME, 50.0);
                splitMap.put(MEMBER_NAME, 50.0);
            } else if (paidBy.equals(THIRD_MEMBER_NAME)) {
                splitMap.put(THIRD_MEMBER_NAME, 33.33);
                splitMap.put(MEMBER_NAME, 33.33);
                splitMap.put(SECOND_MEMBER_NAME, 33.34);
            }
        }
        request.setSplit(splitMap);
        
        return request;
    }

    @Test
    public void whenAddMemberToExistingGroup_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        Response response = resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        assertEquals(STATUS_CREATED, response.getStatus());
    }

    @Test
    public void whenAddMemberToNonExistingGroup_thenReturns404() {
        Response response = resource.addMember(NON_EXISTENT_GROUP, createMemberRequest(MEMBER_NAME));
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenAddMemberWithSpaces_thenReturns400() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        Response response = resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME_WITH_SPACE));
        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenAddExistingMember_thenReturns409() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response response = resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        assertEquals(STATUS_CONFLICT, response.getStatus());
    }

    @Test
    public void whenCreateGroup_thenReturns201() {
        Response response = resource.createGroup(createGroupRequest(GROUP_NAME));
        assertEquals(STATUS_CREATED, response.getStatus());
    }

    @Test
    public void whenDeleteExistingGroup_thenReturns204() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response response = resource.deleteGroup(GROUP_NAME, MEMBER_NAME);
        assertEquals(STATUS_NO_CONTENT, response.getStatus());
    }

    @Test
    public void whenDeleteGroupWithNonMember_thenReturns403() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response response = resource.deleteGroup(GROUP_NAME, NON_MEMBER_NAME);
        assertEquals(STATUS_FORBIDDEN, response.getStatus());
    }

    @Test
    public void whenDeleteNonExistingGroup_thenReturns404() {
        Response response = resource.deleteGroup(NON_EXISTENT_GROUP, MEMBER_NAME);
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenDeleteGroupWithUnresolvedDebts_thenReturns409() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));

        resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        Response response = resource.deleteGroup(GROUP_NAME, MEMBER_NAME);
        assertEquals(STATUS_CONFLICT, response.getStatus());
    }

    @Test
    public void whenGetExistingGroup_thenReturns200() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        Response response = resource.getGroup(GROUP_NAME);
        assertEquals(STATUS_OK, response.getStatus());
    }

    @Test
    public void whenAddMember_thenLocationHeaderIsCorrect() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        Response response = resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        assertEquals(BASE_URL + GROUP_NAME + "/members/" + MEMBER_NAME,
                response.getHeaderString("Location"));
    }

    @Test
    public void whenGetGroup_thenReturnsMembers() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response getResponse = resource.getGroup(GROUP_NAME);
        assertEquals(STATUS_OK, getResponse.getStatus());
        GroupDTO group = (GroupDTO) getResponse.getEntity();
        assertEquals(1, group.getMembers().size());
    }

    @Test
    public void whenListGroups_thenReturnsMembersForEachGroup() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response listResponse = resource.listGroups();
        assertEquals(STATUS_OK, listResponse.getStatus());
        List<GroupDTO> groups = (List<GroupDTO>) listResponse.getEntity();
        assertFalse(groups.isEmpty());
        assertEquals(MEMBER_NAME, groups.getFirst().getMembers().getFirst().getMemberName());
    }
    @Test
    public void whenListGroupMembersWithValidMember_thenReturns200() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.listGroupMembers(GROUP_NAME, MEMBER_NAME);
        assertEquals(STATUS_OK, response.getStatus());
        List<?> members = (List<?>) response.getEntity();
        assertNotNull(members);
        assertEquals(2, members.size());
    }

    @Test
    public void whenListGroupMembersWithInvalidMember_thenReturns403() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        Response response = resource.listGroupMembers(GROUP_NAME, SECOND_MEMBER_NAME);
        assertEquals(STATUS_FORBIDDEN, response.getStatus());
    }

    @Test
    public void whenListGroupMembersForNonExistingGroup_thenReturns404() {
        Response response = resource.listGroupMembers(NON_EXISTENT_GROUP, MEMBER_NAME);
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenAddExpenseToExistingGroup_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_CREATED, response.getStatus());
    }

    @Test
    public void whenAddExpenseToNonExistingGroup_thenReturns404() {
        Response response = resource.addExpense(NON_EXISTENT_GROUP, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithNonExistingMember_thenReturns404() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithNegativeAmount_thenReturns400() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, NEGATIVE_EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithFutureDate_thenReturns400() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, FUTURE_EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithInvalidDateFormat_thenReturns400() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, INVALID_EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenAddExpense_thenLocationHeaderIsCorrect() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        Response response = resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        assertTrue(response.getHeaderString("Location").startsWith(BASE_URL + GROUP_NAME + "/expenses/"));
    }

    @Test
    public void whenGetGroup_thenReturnsExpensesAndDebts() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(THIRD_MEMBER_NAME));
        resource.addExpense(GROUP_NAME, createExpenseRequest(
                EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        Response getResponse = resource.getGroup(GROUP_NAME);
        assertEquals(STATUS_OK, getResponse.getStatus());
        GroupDTO group = (GroupDTO) getResponse.getEntity();
        assertEquals(1, group.getExpenses().size());
        assertEquals(EXPENSE_DESCRIPTION, group.getExpenses().get(0).getDescription());
        assertEquals(EXPENSE_AMOUNT, group.getExpenses().get(0).getAmount());
    }

    @Test
    public void whenGetExpenseHistory_thenReturnsSortedHistoryAndTotal() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));

        resource.addExpense(GROUP_NAME, createExpenseRequest(
                SECOND_EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));
        resource.addExpense(GROUP_NAME, createExpenseRequest(
                THIRD_EXPENSE_DESCRIPTION, SECOND_EXPENSE_AMOUNT, SECOND_EXPENSE_DATE, MEMBER_NAME, SPLIT_METHOD));

        Response response = resource.getExpenseHistory(GROUP_NAME, MEMBER_NAME);
        assertEquals(STATUS_OK, response.getStatus());

        ExpenseHistoryDTO history = (ExpenseHistoryDTO) response.getEntity();
        assertEquals(EXPECTED_TOTAL_EXPENSE, history.total());

        List<ExpenseDTO> expenseDTOs = history.expenses();
        assertEquals(2, expenseDTOs.size());
        assertEquals(THIRD_EXPENSE_DESCRIPTION, expenseDTOs.get(0).getDescription());
        assertEquals(SECOND_EXPENSE_DESCRIPTION, expenseDTOs.get(1).getDescription());
    }

    @Test
    public void whenGetExpenseHistoryForNonExistingGroup_thenReturns404() {
        Response response = resource.getExpenseHistory(NON_EXISTENT_GROUP, MEMBER_NAME);
        assertEquals(STATUS_NOT_FOUND, response.getStatus());
    }

    @Test
    public void whenGetExpenseHistoryWithMemberNotInGroup_thenReturns403() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));

        Response response = resource.getExpenseHistory(GROUP_NAME, NON_MEMBER_NAME);
        assertEquals(STATUS_FORBIDDEN, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithEquallySplitMethod_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription("Test expense");
        request.setAmount(100.0);
        request.setPurchaseDate(LocalDate.now().toString());
        request.setPaidBy(MEMBER_NAME);
        request.setSplitMethod("equally");
        // Pas besoin de définir le split pour equally
        
        Response response = resource.addExpense(GROUP_NAME, request);
        
        assertEquals(STATUS_CREATED, response.getStatus());
    }
    
    @Test
    public void whenAddExpenseWithSkewedSplitMethod_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription("Test expense");
        request.setAmount(100.0);
        request.setPurchaseDate(LocalDate.now().toString());
        request.setPaidBy(MEMBER_NAME);
        request.setSplitMethod("skewed");
        
        Map<String, Double> split = new HashMap<>();
        split.put(MEMBER_NAME, 60.0); // 60% pour le premier membre
        // Le second membre recevra automatiquement 40%
        request.setSplit(split);
        
        Response response = resource.addExpense(GROUP_NAME, request);
        
        assertEquals(STATUS_CREATED, response.getStatus());
    }
    
    @Test
    public void whenAddExpenseWithCustomSplitMethod_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription("Test expense");
        request.setAmount(100.0);
        request.setPurchaseDate(LocalDate.now().toString());
        request.setPaidBy(MEMBER_NAME);
        request.setSplitMethod("custom");
        
        Map<String, Double> split = new HashMap<>();
        split.put(MEMBER_NAME, 30.0);
        split.put(SECOND_MEMBER_NAME, 70.0);
        request.setSplit(split);
        
        Response response = resource.addExpense(GROUP_NAME, request);
        
        assertEquals(STATUS_CREATED, response.getStatus());
    }
    
    @Test
    public void whenAddExpenseWithInvalidSplitMethod_thenReturns400() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription("Test expense");
        request.setAmount(100.0);
        request.setPurchaseDate(LocalDate.now().toString());
        request.setPaidBy(MEMBER_NAME);
        request.setSplitMethod("invalid");
        
        Response response = resource.addExpense(GROUP_NAME, request);
        
        assertEquals(STATUS_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void whenAddExpenseWithDefaultSplitMethod_thenReturns201() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(SECOND_MEMBER_NAME));
        
        GroupResource.CreateExpenseRequest request = new GroupResource.CreateExpenseRequest();
        request.setDescription("Test expense");
        request.setAmount(100.0);
        request.setPurchaseDate(LocalDate.now().toString());
        request.setPaidBy(MEMBER_NAME);
        // Pas besoin de définir le splitMethod, equally est la valeur par défaut
        
        Response response = resource.addExpense(GROUP_NAME, request);
        
        assertEquals(STATUS_CREATED, response.getStatus());
    }
    @Test
    public void whenGetDashboardWithMemberNotInGroup_ThenReturns403() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));

        Response response = resource.getDashboard(GROUP_NAME, NON_MEMBER_NAME, "A", 1);
        assertEquals(STATUS_FORBIDDEN, response.getStatus());
    }
    @Test
    public void whenGetDashboardWithMemberInGroup_ThenReturns200() {
        resource.createGroup(createGroupRequest(GROUP_NAME));
        resource.addMember(GROUP_NAME, createMemberRequest(MEMBER_NAME));

        Response response = resource.getDashboard(GROUP_NAME, MEMBER_NAME, "A", 1);
        assertEquals(STATUS_OK, response.getStatus());
    }
}