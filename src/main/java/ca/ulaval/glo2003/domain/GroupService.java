package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.DashboardDTO;
import ca.ulaval.glo2003.data.GroupRepositoryInMemory;
import ca.ulaval.glo2003.domain.Exceptions.*;
import ca.ulaval.glo2003.data.GroupRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GroupService {
    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public GroupService() {
        this.repository = GroupRepositoryInMemory.getInstance();
    }

    public void deleteGroup(String groupName, String memberName) {

        if (!repository.exists(groupName)) {
            throw new GroupNotFoundException();
        }
        Group group = repository.findByName(groupName);

        if (!group.hasMember(memberName)) {
            throw new Exceptions.MemberNotFoundException(memberName);
        }

        if (group.hasUnresolvedDebts()) {
            throw new Exceptions.GroupHasUnresolvedDebtsException();
        }
        repository.delete(groupName);
    }

    public List<Group> getAllGroups() {
        return repository.findAll();
    }

    public Group getGroup(String name) {
        return repository.findByName(name);
    }

    public Group addMember(String groupName, String memberName) {
        Group group = repository.findByName(groupName);

        if (memberName.contains(" ")) {
            throw new InvalidMemberNameException();
        }

        if (group.hasMember(memberName)) {
            throw new MemberAlreadyExistsException();
        }

        group.addMember(memberName);
        return repository.save(group);
    }

    public Group createGroup(String name) {
        if (repository.exists(name)) {
            throw new GroupAlreadyExistsException();
        }
        if (name.contains(" ")) {
            throw new InvalidGroupNameException();
        }
        Group group = new Group(name);
        repository.save(group);
        return group;
    }

    public Expense addExpense(String groupName, String description, double amount,
                              String purchaseDateStr, String paidBy, ExpenseSplit split) {
        Group group = repository.findByName(groupName);

        if (amount < 0) {
            throw new InvalidAmountException();
        }

        LocalDate purchaseDate;
        try {
            purchaseDate = LocalDate.parse(purchaseDateStr, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }

        if (purchaseDate.isAfter(LocalDate.now())) {
            throw new InvalidDateException();
        }

        if (!group.hasMember(paidBy)) {
            throw new MemberNotFoundException(paidBy);
        }
        for (String memberName : split.getParticipatingMembers()) {
            if (!group.hasMember(memberName)) {
                throw new MemberNotFoundException(memberName);
            }
        }

        Expense expense = new Expense(description, amount, purchaseDate, paidBy, split);
        group.addExpense(expense);

        repository.save(group);

        return expense;
    }

    public void settleDebts(String groupName, String toMember, String fromMember) {

        if (!repository.exists(groupName)) {
            throw new GroupNotFoundException();
        }
        
        Group group = getGroup(groupName);

        if (!group.hasMember(toMember)){
            throw new MemberNotFoundException(toMember);
        }
        else if (!group.hasMember(fromMember)) {
            throw new MemberNotFoundException(fromMember);
        }
        group.settleDebts(fromMember,toMember);
        repository.save(group);
    }

    public ExpenseHistory getExpenseHistory(String groupName, String requestingMember) {
        Group group = repository.findByName(groupName);
        if (!group.hasMember(requestingMember)) {
            throw new MemberNotFoundException(requestingMember);
        }
        List<Expense> expenses = new ArrayList<>(group.getExpenses());
        expenses.sort((e1, e2) -> e2.getPurchaseDate().compareTo(e1.getPurchaseDate()));
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        return new ExpenseHistory(expenses, total);
    }

    public DashboardDTO getDashboardAsDTO(String groupName, String requestingMember,String time,Integer timeslices) {
        Group group = repository.findByName(groupName);
        if(!group.hasMember(requestingMember)) {
            throw new MemberNotFoundException(requestingMember);
        }
        return DashboardFactory.computeDTOfromGroup(group,time,timeslices);
    }

}

