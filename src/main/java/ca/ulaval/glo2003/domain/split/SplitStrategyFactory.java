package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.SplitStrategy;

public class SplitStrategyFactory {
    public static SplitStrategy createStrategy(String method) {
        return switch (method) {
            case "equally" -> new EqualSplitStrategy();
            case "skewed" -> new SkewedSplitStrategy();
            case "custom" -> new CustomSplitStrategy();
            default -> throw new IllegalArgumentException("Invalid repartition Method: " + method);
        };
    }
} 