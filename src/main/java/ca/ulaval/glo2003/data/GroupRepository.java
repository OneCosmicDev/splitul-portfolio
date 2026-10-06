package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.Group;

import java.util.List;

public interface GroupRepository {
    Group findByName(String name);

    boolean exists(String name);

    Group save(Group group);

    void delete(String name);

    List<Group> findAll();

    void clear();
}
