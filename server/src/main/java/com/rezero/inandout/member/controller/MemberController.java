package com.rezero.inandout.member.controller;

import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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


}
