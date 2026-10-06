package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.*;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import java.util.List;
import java.util.Set;

public class GroupRepositoryMongo implements GroupRepository {
    private final Datastore datastore;
    private final GroupRepositoryConverter converter;

    public GroupRepositoryMongo() {
        this.datastore = DatastoreProvider.getInstance().provide();
        this.converter = new GroupRepositoryConverter();
    }

    public GroupRepositoryMongo(Datastore datastore) {
        this.datastore = datastore;
        this.converter = new GroupRepositoryConverter();
    }

    @Override
    public Group findByName(String name) {
        GroupMongo groupMongo = datastore.find(GroupMongo.class)
                .filter(Filters.eq("name", name))
                .first();

        if (groupMongo == null) {
            throw new Exceptions.GroupNotFoundException();
        }

        return converter.convertToGroup(groupMongo);
    }

    @Override
    public boolean exists(String name) {
        return datastore.find(GroupMongo.class)
                .filter(Filters.eq("name", name))
                .count() > 0;
    }

    @Override
    public Group save(Group group) {
        GroupMongo groupMongo = datastore.find(GroupMongo.class)
                .filter(Filters.eq("name", group.getName()))
                .first();

        if (groupMongo == null) {
            groupMongo = new GroupMongo(group.getName());
        }

        Set<GroupMemberDocument> memberDocuments = converter.convertToMemberDocuments(group.getMembers());
        memberDocuments.forEach(datastore::save);

        List<ExpenseDocument> expenseDocuments = converter.convertToExpenseDocuments(group.getExpenses());
        expenseDocuments.forEach(datastore::save);

        groupMongo.setMembers(memberDocuments);
        groupMongo.setExpenses(expenseDocuments);

        datastore.save(groupMongo);
        return group;
    }

    @Override
    public void delete(String name) {
        datastore.find(GroupMongo.class)
                .filter(Filters.eq("name", name))
                .delete();
    }

    @Override
    public List<Group> findAll() {
        return datastore.find(GroupMongo.class)
                .stream()
                .map(groupMongo -> converter.convertToGroup(groupMongo))
                .toList();
    }

    @Override
    public void clear() {
        datastore.find(GroupMongo.class).delete();
    }
}