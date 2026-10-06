package ca.ulaval.glo2003.api;

public class DebtDTO {
    private final String memberName;
    private final double amount;

    public DebtDTO(String memberName, double amount) {
        this.memberName = memberName;
        this.amount = amount;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getAmount() {
        return amount;
    }
}
