package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.Exceptions;
import ca.ulaval.glo2003.domain.Group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

class GroupRepositoryInMemoryTest extends GroupRepositoryInterfaceTest{

    @Override
    protected GroupRepository createGroupPersistence(){
        return GroupRepositoryInMemory.getInstance();
    }
}
