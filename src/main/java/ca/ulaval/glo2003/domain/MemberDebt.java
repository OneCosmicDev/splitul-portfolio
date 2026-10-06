package ca.ulaval.glo2003.domain;

public class MemberDebt {
    private double amount;
    private GroupMember dueTo;

    public MemberDebt(double amount, GroupMember dueTo) {
        this.amount = amount;
        this.dueTo = dueTo;
    }

    public double getAmount() {
        return amount;
    }

    public GroupMember getDueTo() {
        return dueTo;
    }
    public boolean isDueTo(GroupMember member){
        return member == getDueTo();
    }
}
