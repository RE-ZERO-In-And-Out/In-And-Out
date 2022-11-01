package com.rezero.inandout.member.model;

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
public class UpdateMemberInput {

    private String nickName;
    private String phone;
    private LocalDate birth;
    private String address;
    private String gender;

}
