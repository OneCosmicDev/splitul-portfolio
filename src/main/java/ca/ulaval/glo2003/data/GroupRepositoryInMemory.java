package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.Group;
import ca.ulaval.glo2003.domain.Exceptions.GroupNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupRepositoryInMemory implements GroupRepository {
    private static GroupRepositoryInMemory instance;
    private final Map<String, Group> groups = new HashMap<>();

    private GroupRepositoryInMemory() {}

    public static synchronized GroupRepositoryInMemory getInstance() {
        if (instance == null) {
            instance = new GroupRepositoryInMemory();
        }
        return instance;
    }

    @Override
    public Group findByName(String name) {
        if (!groups.containsKey(name)) {
            throw new GroupNotFoundException();
        }
        return groups.get(name);
    }

    @Override
    public boolean exists(String name) {
        return groups.containsKey(name);
    }

    @Override
    public Group save(Group group) {
        groups.put(group.getName(), group);
        return group;
    }

    @Override
    public void delete(String name) {
        groups.remove(name);
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>(groups.values());
    }

    @Override
    public void clear() {
        groups.clear();
    }
}
