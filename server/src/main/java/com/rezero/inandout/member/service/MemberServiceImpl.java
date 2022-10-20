package com.rezero.inandout.member.service;

import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (optionalMember == null) {
            throw new RuntimeException("회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();
        return new User(member.getEmail(), member.getPassword(), null);
    }

    public void validateInput(JoinMemberInput input) {
        Optional<Member> existsMember = memberRepository.findByEmail(input.getEmail());
        String message = "비밀번호는 ";

        // 같은 이메일로 회원 존재
        if (existsMember.isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        existsMember = null;
        existsMember = memberRepository.findByPhone(input.getPhone());
        if (existsMember.isPresent()) {
            throw new RuntimeException("이미 존재하는 휴대폰 번호입니다.");
        }

        existsMember = null;
        existsMember = memberRepository.findByNickName(input.getNickName());
        if (existsMember.isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호
        String password = input.getPassword();
        if (password.length() < 8) {
            throw new RuntimeException("비밀번호는 8자리 이상이어야합니다.(영문자, 숫자, 특수문자를 각각 1글자 이상 포함)");
        }

        Boolean special = false, digit = false, character = false;
        for (int i = 0; i < password.length(); ++i) {
            if (String.valueOf(password.charAt(i)).matches("[^a-zA-Z0-9\\s]")) {
                special = true;
                break;
            }
        }
        for (int i = 0; i < password.length(); ++i) {
            if (Character.isDigit(password.charAt(i))) {
                digit = true;
                break;
            }
        }
        for (int i = 0; i < password.length(); ++i) {
            if ('a' <= password.charAt(i) && password.charAt(i) <= 'z' ||
                'A' <= password.charAt(i) && password.charAt(i) <= 'Z') {
                character = true;
                break;
            }
        }

        if (!character || !digit || !special) {
            if (!character) {
                if (digit && special) {
                    message += "문자";
                } else {
                    message += "문자, ";
                }
            }

            if (!digit) {
                if (special) {
                    message += "숫자";
                } else {
                    message += "숫자, ";
                }
            }

            if (!special) {
                message += "특수문자";
            }
            message += "를 각 한 글자 이상 포함해야 합니다.";
            throw new RuntimeException(message);
        }
    }

    @Override
    public void join(JoinMemberInput input) {

        validateInput(input);
        String password = input.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(password);
        Member member = Member.builder()
            .email(input.getEmail())
            .address(input.getAddress())
            .birth(input.getBirth())
            .gender(input.getGender())
            .password(encPassword)
            .nickName(input.getNickName())
            .phone(input.getPhone())
            .build();
        memberRepository.save(member);
    }


    @Override
    public String findEmail(String email) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("존재하지 않는 아이디(이메일)입니다. 정확하게 입력해주세요.");
        }
        return email;
    }

    @Override
    public void findPhone(String email, String phone) {

        Member optionalMember = memberRepository
            .findByEmailAndPhone(email, phone)
            .orElseThrow(() -> new RuntimeException("존재하는 연락처가 아닙니다. 정확하게 입력해주세요."));
    }

    @Override
    public void update(String email, UpdateMemberInput input) {
    }

    @Override
    public void withdraw(String email, String password) {

    }


}
