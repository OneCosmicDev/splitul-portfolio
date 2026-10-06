package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import java.util.Map;

public class CustomSplitStrategy implements SplitStrategy {
    @Override
    public ExpenseSplit calculateSplit(Group group, double amount, Map<String, Double> customPercentages) {
        if (customPercentages == null) {
            throw new IllegalArgumentException("Les pourcentages sont requis pour la répartition personnalisée");
        }

        double totalPercentage = customPercentages.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
                
        if (Math.abs(totalPercentage - 100.0) > 0.0001) {
            throw new IllegalArgumentException("La somme des pourcentages doit être égale à 100%");
        }

        for (String memberName : customPercentages.keySet()) {
            if (!group.hasMember(memberName)) {
                throw new IllegalArgumentException("Le membre " + memberName + " n'existe pas dans ce groupe");
            }
        }

        ExpenseSplit split = new ExpenseSplit();
        customPercentages.forEach(split::addMemberShare);
        return split;
    }
} 