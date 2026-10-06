package ca.ulaval.glo2003.data;

import ca.ulaval.glo2003.domain.GroupMember;
import ca.ulaval.glo2003.domain.MemberDebt;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

@Entity("memberDebts")
public class MemberDebtDocument {
    @Id
    private String id;

    @Reference
    private GroupMemberDocument dueTo;

    private double amount;

    public MemberDebtDocument(double amount, GroupMemberDocument dueTo) {
        this.amount = amount;
        this.dueTo = dueTo;
    }

    public double getAmount() {
        return amount;
    }

    public GroupMemberDocument getDueTo() {
        return dueTo;
    }

    public void setDueTo(GroupMemberDocument dueTo) {
        this.dueTo = dueTo;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
