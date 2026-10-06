package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class CustomSplitStrategyTest {
    
    private static final String MEMBER1 = "alice";
    private static final String MEMBER2 = "bob";
    private static final String MEMBER3 = "charlie";
    
    private Group group;
    private CustomSplitStrategy strategy;
    
    @BeforeEach
    void setUp() {
        group = new Group("testGroup");
        group.addMember(MEMBER1);
        group.addMember(MEMBER2);
        group.addMember(MEMBER3);
        
        strategy = new CustomSplitStrategy();
    }
    
    @Test
    void whenValidCustomPercentages_thenCreateCorrectSplit() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 25.0);
        customPercentages.put(MEMBER2, 25.0);
        customPercentages.put(MEMBER3, 50.0);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        assertEquals(3, split.getParticipatingMembers().size());
        assertEquals(25.0, split.getMemberShare(MEMBER1), 0.0001);
        assertEquals(25.0, split.getMemberShare(MEMBER2), 0.0001);
        assertEquals(50.0, split.getMemberShare(MEMBER3), 0.0001);
    }
    
    @Test
    void whenNullCustomPercentages_thenThrowException() {
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, null);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("pourcentages sont requis"));
    }
    
    @Test
    void whenPercentagesTotalNotEqual100_thenThrowException() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 60.0);
        customPercentages.put(MEMBER2, 30.0);
        
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, customPercentages);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("doit être égale à 100%"));
    }
    
    @Test
    void whenMemberNotInGroup_thenThrowException() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 50.0);
        customPercentages.put("unknownMember", 50.0);
        
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            strategy.calculateSplit(group, 100.0, customPercentages);
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("n'existe pas dans ce groupe"));
    }
    
    @Test
    void whenOnlySpecifySomeMembersButTotalIs100_thenCreateCorrectSplit() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 70.0);
        customPercentages.put(MEMBER2, 30.0);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        assertEquals(2, split.getParticipatingMembers().size());
        assertEquals(70.0, split.getMemberShare(MEMBER1), 0.0001);
        assertEquals(30.0, split.getMemberShare(MEMBER2), 0.0001);
        assertFalse(split.getParticipatingMembers().contains(MEMBER3));
    }
    
    @Test
    void whenSmallDeviationUnderThreshold_thenAccept100Percent() {
        // Arrange
        Map<String, Double> customPercentages = new HashMap<>();
        customPercentages.put(MEMBER1, 33.33);
        customPercentages.put(MEMBER2, 33.33);
        customPercentages.put(MEMBER3, 33.34);
        
        // Act
        ExpenseSplit split = strategy.calculateSplit(group, 100.0, customPercentages);
        
        // Assert
        double totalPercentage = split.getParticipatingMembers().stream()
                .mapToDouble(split::getMemberShare)
                .sum();
        assertEquals(100.0, totalPercentage, 0.0001);
    }
} 