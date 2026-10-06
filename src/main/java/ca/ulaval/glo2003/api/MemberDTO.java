package ca.ulaval.glo2003.api;

import java.util.Map;

public class MemberDTO {
    private final String memberName;
    private final Map<String, Double> debts;

    public MemberDTO(String memberName, Map<String, Double> debts) {
        this.memberName = memberName;
        this.debts = debts;
    }

    public String getMemberName() {
        return memberName;
    }

    public Map<String, Double> getDebts() {
        return debts;
    }
}
