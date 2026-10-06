package ca.ulaval.glo2003.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupMember {
    private String name;
    private ArrayList<MemberDebt> debts;

    public GroupMember( String name) {
        this.debts  = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<MemberDebt> getDebts() {
        return debts;
    }

    public double getAmountDueToMember(GroupMember member){
        double sum = 0;
        for (MemberDebt debt : debts){
            if (debt.isDueTo(member)){
                sum += debt.getAmount();
            }
        }
        return sum;
    }

    public void clearDebtsToMember(GroupMember member){
        debts.removeIf(debt -> (debt.isDueTo(member)));
    }
    private void clearDebts(){
        debts.clear();
    }
    public void addDebt(MemberDebt debt){
        debts.add(debt);
    }

    public void addDebt(double amount, GroupMember dueTo){
        addDebt(new MemberDebt(amount,dueTo));
    }

    public Map<String,Double> getAmountsDueByName(){
        Map<String,Double> map = new HashMap<>();
        Map<GroupMember,Double> mapByMembers = getAmountsDueByGroupMember();

        for(GroupMember member : mapByMembers.keySet()){
            map.put(member.name, mapByMembers.get(member));
        }

        return map;
    }

    private Map<GroupMember,Double> getAmountsDueByGroupMember(){
        Map<GroupMember,Double> map = new HashMap<>();

        for (MemberDebt debt : debts){
            GroupMember dueTo = debt.getDueTo();
            if (map.containsKey(dueTo)){
                Double newAmount = debt.getAmount() + map.get(dueTo);
                map.replace(dueTo, newAmount);
            }
            else{
                map.put(dueTo,debt.getAmount());
            }
        }
        return map;
    }
    public void fuseDebts(){
        Map<GroupMember,Double> mapByMembers = getAmountsDueByGroupMember();
        clearDebts();
        for(GroupMember member : mapByMembers.keySet()){
            double totalAmount = mapByMembers.get(member);
            addDebt(totalAmount , member);
        }
    }
}
