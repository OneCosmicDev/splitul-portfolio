package ca.ulaval.glo2003.api;

import java.util.Map;

public record ExpenseSplitDTO(Map<String, Double> memberPercentages) {
    public Map<String, Double> getMemberPercentages() {
        return memberPercentages;
    }
} 