package ca.ulaval.glo2003.domain;

public class Exceptions {
    public static class GroupNotFoundException extends RuntimeException {}
    public static class InvalidMemberNameException extends RuntimeException {}
    public static class MemberAlreadyExistsException extends RuntimeException {}
    public static class InvalidGroupNameException extends RuntimeException {}
    public static class GroupAlreadyExistsException extends RuntimeException {}
    public static class MemberNotFoundException extends RuntimeException {
        private final String memberName;

        public MemberNotFoundException(String memberName) {
            this.memberName = memberName;
        }

        public String getMemberName() {
            return memberName;
        }
    }

    public static class InvalidDateException extends RuntimeException {}
    public static class InvalidAmountException extends RuntimeException {}
    public static class InvalidDateFormatException extends RuntimeException {}
    public static class GroupHasUnresolvedDebtsException extends RuntimeException{}
}

