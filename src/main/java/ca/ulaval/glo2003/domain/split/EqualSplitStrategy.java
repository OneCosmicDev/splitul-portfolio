package ca.ulaval.glo2003.domain.split;

import ca.ulaval.glo2003.domain.*;
import java.util.Map;

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public ExpenseSplit calculateSplit(Group group, double amount, Map<String, Double> customPercentages) {
        ExpenseSplit split = new ExpenseSplit();
        double percentage = 100.0 / group.getMembers().size();
        
        for (GroupMember member : group.getMembers()) {
            split.addMemberShare(member.getName(), percentage);
        }
        
        return split;
    }
} 