package ca.ulaval.glo2003.domain;

import ca.ulaval.glo2003.api.MemberDTO;

public class GroupMemberConverter {
    static public MemberDTO toDto(GroupMember member){
        return new MemberDTO(member.getName(), member.getAmountsDueByName());

    }

}
