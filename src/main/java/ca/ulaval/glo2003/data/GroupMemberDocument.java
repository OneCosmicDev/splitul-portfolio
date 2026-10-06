package ca.ulaval.glo2003.data;

import dev.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Entity("members")
public class GroupMemberDocument {
    @Id
    private String id;

    private String memberName;

    @Reference
    private List<MemberDebtDocument> debts = new ArrayList<>();

    public GroupMemberDocument(){
        this.debts = new ArrayList<>();
    };

    public GroupMemberDocument(String memberName) {
        this.memberName = memberName;
        this.debts  = new ArrayList<>();
    }

    public String getMemberName() {
        return memberName;
    }

    public List<MemberDebtDocument> getDebts() {
        return debts;
    }

    public void setDebts(List<MemberDebtDocument> debts) {
        this.debts = debts;
    }
}
