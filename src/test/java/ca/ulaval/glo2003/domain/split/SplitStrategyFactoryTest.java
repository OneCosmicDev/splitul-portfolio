package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.SplitStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SplitStrategyFactoryTest {
    
    @Test
    void whenMethodIsEqually_thenReturnEqualSplitStrategy() {
        // Act
        SplitStrategy strategy = SplitStrategyFactory.createStrategy("equally");
        
        // Assert
        assertTrue(strategy instanceof EqualSplitStrategy);
    }
    
    @Test
    void whenMethodIsSkewed_thenReturnSkewedSplitStrategy() {
        // Act
        SplitStrategy strategy = SplitStrategyFactory.createStrategy("skewed");
        
        // Assert
        assertTrue(strategy instanceof SkewedSplitStrategy);
    }
    
    @Test
    void whenMethodIsCustom_thenReturnCustomSplitStrategy() {
        // Act
        SplitStrategy strategy = SplitStrategyFactory.createStrategy("custom");
        
        // Assert
        assertTrue(strategy instanceof CustomSplitStrategy);
    }
    
    @Test
    void whenMethodIsInvalid_thenThrowException() {
        // Assert & Act
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SplitStrategyFactory.createStrategy("invalid");
        });
        
        // Assert
        assertTrue(exception.getMessage().contains("Invalid repartition Method"));
    }
} 