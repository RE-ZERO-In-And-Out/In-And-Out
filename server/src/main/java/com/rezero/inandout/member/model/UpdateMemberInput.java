package com.rezero.inandout.member.model;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
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


    @NotBlank(message = "닉네임을 정확하게 입력하세요.")
    private String nickName;

    @NotBlank(message = "연락처를 정확하게 입력하세요.")
    private String phone;

    @PastOrPresent(message = "생년월일을 yyyy-mm-dd 형태로 정확하게 입력하세요.")
    private LocalDate birth;

    @NotBlank(message = "주소를 정확하게 입력하세요")
    private String address;

    @NotBlank(message = "성별을 정확하게 입력하세요")
    private String gender;

}
