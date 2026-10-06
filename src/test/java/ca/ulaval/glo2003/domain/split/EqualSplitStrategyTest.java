package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EqualSplitStrategyTest {
    
    private static final String MEMBER1 = "alice";
    private static final String MEMBER2 = "bob";
    private static final String MEMBER3 = "charlie";
    
    private Group group;
    private EqualSplitStrategy strategy;
    
    @BeforeEach
    void setUp() {
        group = new Group("testGroup");
        group.addMember(MEMBER1);
        group.addMember(MEMBER2);
        group.addMember(MEMBER3);
        
        strategy = new EqualSplitStrategy();
    }
    
    @Test
    void whenCalculateSplit_thenSplitEqually() {
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, null);
        
        // Assert
        assertEquals(3, split.getParticipatingMembers().size());
        for (String member : split.getParticipatingMembers()) {
            assertEquals(33.33333333333333, split.getMemberShare(member), 0.0001);
        }
    }
    
    @Test
    void whenGroupHasOneMember_thenGetFullShare() {
        // Arrange
        Group singleMemberGroup = new Group("singleMemberGroup");
        singleMemberGroup.addMember(MEMBER1);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(singleMemberGroup, 100.0, null);
        
        // Assert
        assertEquals(1, split.getParticipatingMembers().size());
        assertEquals(100.0, split.getMemberShare(MEMBER1), 0.0001);
    }
    
    @Test
    void whenTotalPercentagesEqual100_thenSplitValid() {
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, null);
        
        // Assert
        double totalPercentage = split.getParticipatingMembers().stream()
                .mapToDouble(split::getMemberShare)
                .sum();
        assertEquals(100.0, totalPercentage, 0.0001);
    }
} 