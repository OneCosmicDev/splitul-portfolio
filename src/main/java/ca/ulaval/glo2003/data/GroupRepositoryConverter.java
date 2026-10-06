package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.Expense;
import ca.ulaval.glo2003.domain.ExpenseSplit;
import ca.ulaval.glo2003.domain.Group;
import ca.ulaval.glo2003.domain.GroupMember;
import ca.ulaval.glo2003.domain.MemberDebt;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GroupRepositoryConverter {
    private final Datastore datastore;
    private final Map<String, Group> groups = new HashMap<>();

    public GroupRepositoryConverter() {
        this.datastore = DatastoreProvider.getInstance().provide();
    }

    public Group convertToGroup(GroupMongo groupMongo) {
        if (groups.containsKey(groupMongo.getName())) {
            return groups.get(groupMongo.getName());
        }

        Group group = new Group(groupMongo.getName());
        groups.put(group.getName(), group);

        groupMongo.getMembers().forEach(memberDoc -> {
            GroupMember member = new GroupMember(memberDoc.getMemberName());
            group.addMember(member.getName());
        });

        groupMongo.getMembers().forEach(memberDoc -> {
            group.getMembers().forEach(member -> {
                if (member.getName().equals(memberDoc.getMemberName())) {
                    memberDoc.getDebts().forEach(debtDoc -> {
                        group.getMembers().forEach(dueToMember -> {
                            if (dueToMember.getName().equals(debtDoc.getDueTo().getMemberName())) {
                                member.addDebt(debtDoc.getAmount(), dueToMember);
                            }
                        });
                    });
                }
            });
        });

        groupMongo.getExpenses().forEach(expenseDoc -> {
            Expense expense = convertToExpense(expenseDoc);
            group.addExpense(expense);
        });
        return group;
    }

    public Set<GroupMemberDocument> convertToMemberDocuments(Set<GroupMember> members) {
        Set<GroupMemberDocument> memberDocuments = members.stream()
                .map(member -> new GroupMemberDocument(member.getName()))
                .collect(Collectors.toSet());

        memberDocuments.forEach(datastore::save);

        for (GroupMemberDocument doc : memberDocuments) {
            GroupMember groupMember = members.stream()
                    .filter(member -> member.getName().equals(doc.getMemberName()))
                    .findFirst()
                    .orElse(null);

            if (groupMember != null) {
                List<MemberDebtDocument> debtDocs = new ArrayList<>();
                for (MemberDebt debt : groupMember.getDebts()) {
                    GroupMemberDocument dueToDocument = findMemberDocumentByName(debt.getDueTo().getName());
                    MemberDebtDocument debtDoc = new MemberDebtDocument(debt.getAmount(), dueToDocument);
                    datastore.save(debtDoc);
                    debtDocs.add(debtDoc);
                }
                doc.setDebts(debtDocs);
                datastore.save(doc);
            }
        }
        return memberDocuments;
    }

    public GroupMemberDocument findMemberDocumentByName(String name) {
        return datastore.find(GroupMemberDocument.class)
                .filter(Filters.eq("memberName", name))
                .first();
    }

    public Expense convertToExpense(ExpenseDocument expenseDoc) {
        ExpenseSplit split = new ExpenseSplit();
        expenseDoc.getSplit().forEach(split::addMemberShare);
        
        return new Expense(
                expenseDoc.getDescription(),
                expenseDoc.getAmount(),
                LocalDate.parse(expenseDoc.getPurchaseDate(), DateTimeFormatter.ISO_DATE),
                expenseDoc.getPaidBy(),
                split
        );
    }

    public List<ExpenseDocument> convertToExpenseDocuments(List<Expense> expenses) {
        return expenses.stream().map(expense -> new ExpenseDocument(
                expense.getDescription(),
                expense.getAmount(),
                expense.getPurchaseDate().toString(),
                expense.getPaidBy(),
                expense.getSplit().getParticipatingMembers().stream()
                        .collect(Collectors.toMap(
                                memberName -> memberName,
                                expense.getSplit()::getMemberShare
                        ))
        )).collect(Collectors.toList());
    }
}
