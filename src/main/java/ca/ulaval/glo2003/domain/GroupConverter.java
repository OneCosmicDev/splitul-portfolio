package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.GroupDTO;
import ca.ulaval.glo2003.api.MemberDTO;

import ca.ulaval.glo2003.api.ExpenseDTO;
import java.util.List;
import java.util.stream.Collectors;

public class GroupConverter {
    private final ExpenseConverter expenseConverter = new ExpenseConverter();

    public GroupDTO toDTO(Group group) {

        List<ExpenseDTO> expenseDTOs = expenseConverter.toDTOs(group.getExpenses());

        return new GroupDTO(
                group.getName(),
                toMembersDto(group),
                expenseDTOs
        );
    }

    public List<GroupDTO> toDTOs(List<Group> groups) {
        return groups.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MemberDTO> toMembersDto(Group group) {
        return group.getMembers().stream()
                .map(GroupMemberConverter::toDto)
                .collect(Collectors.toList());
    }
}
