package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.GroupDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GroupConverterTest {

    private static final String GROUP_NAME = "testGroup";
    private static final String ANOTHER_GROUP_NAME = "anotherGroup";
    private static final String MEMBER_ONE = "Crystale";
    private static final String MEMBER_TWO = "Croquette";
    private static final String EXPENSE_DESCRIPTION = "Groceries";
    private static final double EXPENSE_AMOUNT = 60.0;
    private static final LocalDate EXPENSE_DATE = LocalDate.of(2023, 1, 1);
    private static final double EQUAL_POURCENTAGE = 50.0;

    private GroupConverter converter;
    private Group group;

    @BeforeEach
    void setUp() {
        converter = new GroupConverter();

        group = new Group(GROUP_NAME);
        group.addMember(MEMBER_ONE);
        group.addMember(MEMBER_TWO);

        ExpenseSplit split = new ExpenseSplit();
        split.addMemberShare(MEMBER_ONE, EQUAL_POURCENTAGE);
        split.addMemberShare(MEMBER_TWO, EQUAL_POURCENTAGE);
        Expense expense = new Expense(EXPENSE_DESCRIPTION, EXPENSE_AMOUNT, EXPENSE_DATE, MEMBER_ONE, split);
        group.addExpense(expense);
    }

    @Test
    void givenGroup_whenConvertToDTO_thenDTOFieldsMatchGroup() {
        GroupDTO dto = converter.toDTO(group);

        assertEquals(GROUP_NAME, dto.getName());
        assertEquals(2, dto.getMembers().size());

        assertTrue(dto.getMembers().stream().anyMatch(m -> m.getMemberName().equals(MEMBER_ONE)));
        assertTrue(dto.getMembers().stream().anyMatch(m -> m.getMemberName().equals(MEMBER_TWO)));

        assertEquals(1, dto.getExpenses().size());
        assertEquals(EXPENSE_DESCRIPTION, dto.getExpenses().get(0).getDescription());
        assertEquals(EXPENSE_AMOUNT, dto.getExpenses().get(0).getAmount());
    }

    @Test
    void givenMultipleGroups_whenConvertToDTOs_thenAllGroupsAreConverted() {
        Group anotherGroup = new Group(ANOTHER_GROUP_NAME);
        List<Group> groups = List.of(group, anotherGroup);

        List<GroupDTO> dtos = converter.toDTOs(groups);

        assertEquals(groups.size(), dtos.size());
    }
}

