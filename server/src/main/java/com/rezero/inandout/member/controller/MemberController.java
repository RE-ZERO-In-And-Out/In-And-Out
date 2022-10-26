package com.rezero.inandout.member.controller;

import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.FindPasswordMemberInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @ApiOperation(value = "회원 가입 API", notes = "이메일을 아이디로 사용하여 가입할 수 있다.")
    public ResponseEntity<?> signUp(
        @ApiParam(value = "회원 가입 정보 입력") @RequestBody JoinMemberInput memberInput) {
        memberService.join(memberInput);
        String message = "회원 가입이 완료됐습니다.";
        return new ResponseEntity(message, HttpStatus.OK);
    }


    @GetMapping("/member/info")
    @ApiOperation(value = "회원 정보 조회 API", notes = "회원이 자신의 정보를 조회한다.")
    public ResponseEntity<?> getInfo(Principal principal) {
        String email = principal.getName();
        MemberDto memberDto = memberService.getInfo(email);
        return new ResponseEntity<>(memberDto, HttpStatus.OK);
    }


    @PutMapping("/member/info")
    @ApiOperation(value = "회원 정보 수정 API", notes = "회원이 자신의 정보를 수정한다.")
    public ResponseEntity<?> updateInfo(Principal principal,
        @ApiParam(value = "수정할 회원 정보 입력") @RequestBody UpdateMemberInput input) {
        String email = principal.getName();
        memberService.updateInfo(email, input);
        String message = "회원 정보를 변경했습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @PatchMapping("/member/password")
    @ApiOperation(value = "회원 비밀번호 변경 API", notes = "기존 비밀번호를 확인하고나서 회원 비밀번호를 변경한다.")
    public ResponseEntity<?> updatePassword(Principal principal,
        @ApiParam(value = "기존 비밀번호, 새로운 비밀번호 입력") @RequestBody ChangePasswordInput input) {
        String email = principal.getName();
        memberService.changePassword(email, input);
        String message = "비밀 번호가 변경되었습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @PostMapping("/signin")
    @ApiOperation(value = "회원 로그인 API", notes = "아이디(이메일)와 비밀번호를 입력해서 로그인한다.")
    public ResponseEntity<?> signin(
        @ApiParam(value = "로그인 정보 입력") @RequestBody LoginMemberInput input) {
        memberService.login(input);
        String message = "정상적으로 로그인 완료했습니다.";
        return new ResponseEntity(message, HttpStatus.OK);
    }


    @PostMapping("/signout")
    @ApiOperation(value = "회원 로그아웃 API")
    public ResponseEntity<?> signout() {
        memberService.logout();
        String message = "정상적으로 로그아웃을 완료했습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @PostMapping("/password/email")
    @ApiOperation(value = "아이디(이메일) 찾기 API", notes = "아이디(이메일)를 입력해서 존재하는 회원인지 확인한다.")
    public ResponseEntity<?> checkEmail(
        @ApiParam(value = "아이디(이메일) 입력") @RequestBody FindPasswordMemberInput findPasswordMemberInput) {
        memberService.validateEmail(findPasswordMemberInput.getEmail());
        return new ResponseEntity(findPasswordMemberInput.getEmail(), HttpStatus.OK);
    }


    @PostMapping("/password/email/phone")
    @ApiOperation(value = "비밀번호 찾기 API", notes = "연락처를 입력하여 비밀번호를 찾을 수 있도록 한다.")
    public ResponseEntity<?> checkPhone(
        @ApiParam(value = "연락처 입력") @RequestBody FindPasswordMemberInput findPasswordMemberInput) {
        memberService.validatePhone(findPasswordMemberInput.getEmail(),
            findPasswordMemberInput.getPhone());
        String message = "비밀번호 찾기 완료";
        return new ResponseEntity(message, HttpStatus.OK);
    }


}
