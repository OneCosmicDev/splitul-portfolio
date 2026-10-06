package ca.ulaval.glo2003.api;

import ca.ulaval.glo2003.domain.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class GroupDTO {
    private final String name;
    private final List<MemberDTO> members;
    private final List<ExpenseDTO> expenses;

    public GroupDTO() {
        this.name = "";
        this.members = new ArrayList<>();
        this.expenses = new ArrayList<>();

    }

    public GroupDTO(String name, List<MemberDTO> members, List<ExpenseDTO> expenses) {
        this.name = name;
        this.members = members;
        this.expenses = expenses;

    }

    public String getName() {
        return name;
    }

    public List<MemberDTO> getMembers() {
        return members;
    }

    public List<ExpenseDTO> getExpenses() {
        return expenses;
    }
}