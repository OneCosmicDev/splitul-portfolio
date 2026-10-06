package ca.ulaval.glo2003.domain;

import java.util.Map;

public interface SplitStrategy {
    ExpenseSplit calculateSplit(Group group, double amount, Map<String, Double> customPercentages);
} 