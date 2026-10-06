package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.Exceptions;
import ca.ulaval.glo2003.domain.Group;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class GroupRepositoryInterfaceTest {
    private GroupRepository groupPersistence;

    protected abstract GroupRepository createGroupPersistence();

    @BeforeEach
    public void setUp() {
        String persistence = System.getProperty("persistence", "inmemory");

        if (persistence.equals("mongo")) {
            groupPersistence = new GroupRepositoryMongo();
        } else {
            groupPersistence = GroupRepositoryInMemory.getInstance();
            groupPersistence.clear();
        }
    }

    private static final String GROUP_NAME = "group1";
    private static final String GROUP_NAME_2 = "group2";
    private static final String GROUP_NAME_3 = "group3";
    private static final String NON_EXISTENT_GROUP_NAME = "nonexistent";

    @Test
    public void givenSavedGroup_whenFindByName_thenReturnGroup() {
        var group = new Group(GROUP_NAME);
        groupPersistence.save(group);

        var foundGroup = groupPersistence.findByName(GROUP_NAME);

        Assertions.assertThat(group).isEqualTo(foundGroup);
    }

    @Test
    public void givenNoSavedGroup_whenFindByName_thenThrowsGroupNotFoundException() {
        assertThrows(Exceptions.GroupNotFoundException.class,
                () -> groupPersistence.findByName(NON_EXISTENT_GROUP_NAME));
    }

    @Test
    public void givenMultipleSavedGroups_whenFindAll_thenReturnGroups() {
        var group1 = new Group(GROUP_NAME);
        groupPersistence.save(group1);
        var group2 = new Group(GROUP_NAME_2);
        groupPersistence.save(group2);
        var group3 = new Group(GROUP_NAME_3);
        groupPersistence.save(group3);

        var foundGroups = groupPersistence.findAll();

        Assertions.assertThat(foundGroups).containsExactlyInAnyOrder(group1, group2, group3);
    }

    @Test
    public void givenNoSavedGroups_whenFindAll_thenReturnEmptyList() {
        var foundGroups = groupPersistence.findAll();
        Assertions.assertThat(foundGroups).isEmpty();
    }

    @Test
    public void whenResavingSameGroup_shouldOverrideGroup(){
        var group1 = new Group(GROUP_NAME);
        groupPersistence.save(group1);
        groupPersistence.save(group1);
        var foundGroups = groupPersistence.findAll();
        Assertions.assertThat(foundGroups).containsExactly(group1);
    }

    @Test
    public void givenSavedGroup_whenExists_thenReturnTrue() {
        var group = new Group(GROUP_NAME);
        groupPersistence.save(group);

        boolean exists = groupPersistence.exists(group.getName());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void givenNoSavedGroup_whenExists_thenReturnFalse() {
        boolean exists = groupPersistence.exists(GROUP_NAME);
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    public void givenSavedGroup_whenDelete_thenShouldNotExist(){
        var group = new Group(GROUP_NAME);
        groupPersistence.save(group);
        groupPersistence.delete(group.getName());

        boolean exists = groupPersistence.exists(group.getName());
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    public void givenSavedGroups_whenClear_thenRepositoryShouldBeEmpty() {
        var group1 = new Group(GROUP_NAME);
        var group2 = new Group(GROUP_NAME_2);
        groupPersistence.save(group1);
        groupPersistence.save(group2);

        groupPersistence.clear();

        Assertions.assertThat(groupPersistence.findAll()).isEmpty();
    }

}
