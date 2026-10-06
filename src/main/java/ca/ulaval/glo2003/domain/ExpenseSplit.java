package ca.ulaval.glo2003.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpenseSplit {
    private final Map<String, Double> memberPercentages;
    private static final double EPSILON = 0.0001;

    public ExpenseSplit() {
        this.memberPercentages = new HashMap<>();
    }

    public void addMemberShare(String memberName, double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new InvalidPercentageException();
        }
        memberPercentages.put(memberName, percentage);
    }

    public boolean isValid() {
        if (memberPercentages.isEmpty()) {
            return false;
        }
        
        double totalPercentage = memberPercentages.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        
        return Math.abs(totalPercentage - 100.0) < EPSILON;
    }

    public double getMemberShare(String memberName) {
        return memberPercentages.getOrDefault(memberName, 0.0);
    }

    public Set<String> getParticipatingMembers() {
        return memberPercentages.keySet();
    }

    public static class InvalidPercentageException extends RuntimeException {
        public InvalidPercentageException() {
            super("Le pourcentage doit être entre 0 et 100");
        }
    }
} 