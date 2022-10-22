package com.rezero.inandout.member.controller;

import com.rezero.inandout.member.model.FindPasswordMemberInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody JoinMemberInput memberInput) {
        memberService.join(memberInput);
        String message = "회원 가입이 완료됐습니다.";
        return new ResponseEntity(message, HttpStatus.OK);
    }


    @PostMapping("/password/email")
    public ResponseEntity<?> checkEmail(
        @RequestBody FindPasswordMemberInput findPasswordMemberInput) {
        memberService.validateEmail(findPasswordMemberInput.getEmail());
        return new ResponseEntity(findPasswordMemberInput.getEmail(), HttpStatus.OK);
    }


    @PostMapping("/password/email/phone")
    public ResponseEntity<?> checkPhone(
        @RequestBody FindPasswordMemberInput findPasswordMemberInput) {
        memberService.validatePhone(findPasswordMemberInput.getEmail(),
            findPasswordMemberInput.getPhone());
        String message = "비밀번호 찾기 완료";
        return new ResponseEntity(message, HttpStatus.OK);
    }


    @GetMapping("/member/info")
    public ResponseEntity<?> getInfo(/*Principal principal*/) {
//        String email = Principal.getName();
        String email = "egg@naver.com";
        MemberDto memberDto = memberService.getInfo(email);
        return new ResponseEntity<>(memberDto, HttpStatus.OK);
    }

    @PutMapping("/member/info")
    public ResponseEntity<?> updateInfo(/*Principal principal*/
        @RequestBody UpdateMemberInput input) {
//        String email = principal.getName();
        String email = "egg@naver.com";
        memberService.updateInfo(email, input);
        String message = "회원 정보를 변경했습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


}
