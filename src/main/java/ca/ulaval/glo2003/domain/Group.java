package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.domain.split.SplitStrategyFactory;
import java.time.LocalDate;
import java.util.*;

public class Group {
    private final UUID id;
    private final String name;
    private final Set<GroupMember> members;
    private final List<Expense> expenses;

    public Group(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.members = new HashSet<>();
        this.expenses = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GroupMember[] getMembersAsArray() {
        return members.toArray(new GroupMember[0]);
    }

    public boolean hasMember(String memberName) {
        return members.stream().anyMatch(member -> Objects.equals(member.getName(), memberName));
    }

    public Set<GroupMember> getMembers() {
        return members;
    }

    public void addMember(String memberName) {
        if(hasMember(memberName)){
            throw new Exceptions.MemberAlreadyExistsException();
        }
        members.add(new GroupMember(memberName));
    }

    private void settleDebts(GroupMember debtor, GroupMember payTo){
        debtor.clearDebtsToMember(payTo);
    }

    public void settleDebts(String debtorName, String payToName){
        settleDebts(getMemberByName(debtorName),getMemberByName(payToName));
    }

    public void addExpense(Expense expense) {
        if (!hasMember(expense.getPaidBy())) {
            throw new Exceptions.MemberNotFoundException(expense.getPaidBy());
        }
        checkSplitMembersAreInGroup(expense);

        expenses.add(expense);
        splitExpenseByMembers(expense);
    }

    private void checkSplitMembersAreInGroup(Expense expense) {
        for (String memberName : expense.getSplit().getParticipatingMembers()) {
            if (!hasMember(memberName)) {
                throw new Exceptions.MemberNotFoundException(memberName);
            }
        }
    }

    public GroupMember getMemberByName(String name){
        return members.stream()
                .filter(member -> member.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void splitExpenseByMembers(Expense expense) {
        GroupMember paidBy = getMemberByName(expense.getPaidBy());
        
        for (GroupMember member : members) {
            if (!member.equals(paidBy)) {
                double memberAmount = expense.getAmountForMember(member.getName());
                if (memberAmount > 0) {
                    member.addDebt(memberAmount, paidBy);
                }
            }
        }
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public boolean hasUnresolvedDebts() {
        return members.stream()
                .anyMatch(member -> !member.getDebts().isEmpty());
    }

    public ExpenseSplit calculateExpenseSplit(String splitMethod, Map<String, Double> customPercentages) {
        SplitStrategy strategy = SplitStrategyFactory.createStrategy(splitMethod);
        return strategy.calculateSplit(this, 0.0, customPercentages);
    }
}
