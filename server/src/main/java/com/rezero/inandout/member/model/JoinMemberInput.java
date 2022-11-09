package com.rezero.inandout.member.model;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class JoinMemberInput {

    @Email(message = "email을 정확하게 입력하세요.")
    private String email;

    @NotBlank (message = "비밀번호는 영문자, 숫자, 특수 기호를 한 글자 이상 넣으셔야합니다.")
    private String password;

    @NotBlank(message = "닉네임을 정확하게 입력하세요.")
    private String nickName;

    @NotBlank(message = "연락처를 정확하게 입력하세요.")
    private String phone;

    @PastOrPresent(message = "생년월일을 정확하게 입력하세요.")
    private LocalDate birth;

    @NotBlank(message = "주소를 정확하게 입력하세요")
    private String address;

    @NotBlank(message = "성별을 정확하게 입력하세요")
    private String gender;


    // 낫널일 떄는
    // "" " " => 에서 예외 안 터진다.

    // 낫empty
    // " " => 에서 예외 안 터진다. ""는 예외 터뜨림


    // 핸들러에 밸리드 익셉션

}
