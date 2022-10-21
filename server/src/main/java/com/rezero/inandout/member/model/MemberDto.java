package com.rezero.inandout.member.model;

import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private String nickName;
    private String phone;
    private LocalDate birth;
    private String address;
    private String gender;
    private String memberPhotoUrl;

    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
            .address(member.getAddress())
            .birth(member.getBirth())
            .gender(member.getGender())
            .memberPhotoUrl(member.getMemberPhotoUrl())
            .nickName(member.getNickName())
            .phone(member.getPhone())
            .build();
    }

}
