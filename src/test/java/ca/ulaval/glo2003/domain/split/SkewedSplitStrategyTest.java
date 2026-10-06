package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SkewedSplitStrategyTest {
    
    private static final String MEMBER1 = "alice";
    private static final String MEMBER2 = "bob";
    private static final String MEMBER3 = "charlie";
    
    private Group group;
    private SkewedSplitStrategy strategy;
    
    @BeforeEach
    void setUp() {
        group = new Group("testGroup");
        group.addMember(MEMBER1);
        group.addMember(MEMBER2);
        group.addMember(MEMBER3);
        
        strategy = new SkewedSplitStrategy();
    }
    
    @Test
    void whenPercentagesSpecifiedForSomeMembers_thenDistributeRemainingEqually() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 60.0);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        assertEquals(3, split.getParticipatingMembers().size());
        assertEquals(60.0, split.getMemberShare(MEMBER1), 0.0001);
        assertEquals(20.0, split.getMemberShare(MEMBER2), 0.0001);
        assertEquals(20.0, split.getMemberShare(MEMBER3), 0.0001);
    }
    
    @Test
    void whenPercentagesSpecifiedForAllButOne_thenRemainingMemberGetsRest() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 30.0);
        customPercentages.put(MEMBER2, 50.0);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        assertEquals(3, split.getParticipatingMembers().size());
        assertEquals(30.0, split.getMemberShare(MEMBER1), 0.0001);
        assertEquals(50.0, split.getMemberShare(MEMBER2), 0.0001);
        assertEquals(20.0, split.getMemberShare(MEMBER3), 0.0001);
    }
    
    @Test
    void whenNullCustomPercentages_thenThrowException() {
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, null);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("pourcentages personnalisés sont requis"));
    }
    
    @Test
    void whenPercentagesTotalExceeds100_thenThrowException() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 60.0);
        customPercentages.put(MEMBER2, 50.0);
        
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, customPercentages);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("dépasse 100%"));
    }
    
    @Test
    void whenMemberNotInGroup_thenThrowException() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put("unknownMember", 50.0);
        
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, customPercentages);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("n'existe pas dans ce groupe"));
    }
    
    @Test
    void whenTotalPercentagesEqual100_thenSplitValid() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 30.0);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        double totalPercentage = split.getParticipatingMembers().stream()
                .mapToDouble(split::getMemberShare)
                .sum();
        assertEquals(100.0, totalPercentage, 0.0001);
    }
} 