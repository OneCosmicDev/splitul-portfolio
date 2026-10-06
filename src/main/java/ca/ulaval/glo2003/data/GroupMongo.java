package ca.ulaval.glo2003.data;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

import java.util.*;

@Entity("groups")
public class GroupMongo {
    @Id
    private String id;
    private String name;

    @Reference
    private Set<GroupMemberDocument> members;

    @Reference
    private List<ExpenseDocument> expenses;

    public GroupMongo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<GroupMemberDocument> getMembers() {
        return members;
    }

    public List<ExpenseDocument> getExpenses() {
        return expenses;
    }

    public void setMembers(Set<GroupMemberDocument> members) {
        this.members = members != null ? members: new HashSet<>();
    }

    public void setExpenses(List<ExpenseDocument> expenses) {
        this.expenses = expenses != null ? expenses: new ArrayList<>();
    }
}