package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import java.util.*;

public class SkewedSplitStrategy implements SplitStrategy {
    @Override
    public ExpenseSplit calculateSplit(Group group, double amount, Map<String, Double> customPercentages) {
        if (customPercentages == null) {
            throw new IllegalArgumentException("Les pourcentages personnalisés sont requis pour la répartition asymétrique");
        }

        ExpenseSplit split = new ExpenseSplit();
        double specifiedTotal = customPercentages.values().stream().mapToDouble(Double::doubleValue).sum();
        
        if (specifiedTotal >= 100.0) {
            throw new IllegalArgumentException("La somme des pourcentages spécifiés dépasse 100%");
        }

        for (Map.Entry<String, Double> entry : customPercentages.entrySet()) {
            if (!group.hasMember(entry.getKey())) {
                throw new IllegalArgumentException("Le membre " + entry.getKey() + " n'existe pas dans ce groupe");
            }
            split.addMemberShare(entry.getKey(), entry.getValue());
        }

        Set<String> remainingMembers = new HashSet<>();
        for (GroupMember member : group.getMembers()) {
            if (!customPercentages.containsKey(member.getName())) {
                remainingMembers.add(member.getName());
            }
        }

        if (!remainingMembers.isEmpty()) {
            double remainingPercentage = 100.0 - specifiedTotal;
            double equalShare = remainingPercentage / remainingMembers.size();
            for (String memberName : remainingMembers) {
                split.addMemberShare(memberName, equalShare);
            }
        }

        return split;
    }
} 