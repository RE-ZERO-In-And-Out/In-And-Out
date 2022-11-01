package com.rezero.inandout.member.service;

import static com.rezero.inandout.exception.errorcode.MemberErrorCode.CANNOT_GET_INFO;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.CANNOT_LOGOUT;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.CANNOT_UPLOAD_IMAGE;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.CONTAINS_BLANK;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.EMAIL_AUTH_KEY_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.EMAIL_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.EMAIL_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.MEMBER_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.NICKNAME_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_LENGTH_MORE_THAN_8;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_CHARACTER;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_CHARACTER_AND_SPECIAL;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_DIGIT;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_DIGIT_AND_CHARACTER;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_DIGIT_AND_SPECIAL;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_CONTAIN_SPECIAL;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PASSWORD_NOT_MATCH;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PHONE_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.PHONE_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.RESET_PASSWORD_KEY_EXPIRED;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.RESET_PASSWORD_KEY_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.WITHDRAWAL_MEMBER;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.component.MailComponent;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.MemberStatus;
import com.rezero.inandout.member.model.ResetPasswordInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final MailComponent mailComponent;

    private final AmazonS3Client amazonS3Client;


    @Value(value = "${cloud.aws.bucket.name}")
    private String S3Bucket;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        if (!optionalMember.isPresent()) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_EXIST);
        }

        Member member = optionalMember.get();
        return new User(member.getEmail(), member.getPassword(), AuthorityUtils.NO_AUTHORITIES);
    }


    @Override
    public void login(LoginMemberInput input) {

        UserDetails userDetails = loadUserByUsername(input.getEmail());
        Optional<Member> optionalMember = memberRepository.findByEmail(input.getEmail());
        Member member = optionalMember.get();

        if (!bCryptPasswordEncoder.matches(input.getPassword(), member.getPassword())) {
            throw new MemberException(PASSWORD_NOT_MATCH);
        }
        if (member.getStatus().equals(MemberStatus.REQ)) {
            throw new MemberException(MemberErrorCode.CANNOT_LOGIN_REQ);
        }
        if (member.getStatus().equals(MemberStatus.WITHDRAW)) {
            throw new MemberException(MemberErrorCode.CANNOT_LOGIN_WITHDRAW);
        }
        if (member.getStatus().equals(MemberStatus.STOP)) {
            throw new MemberException(MemberErrorCode.CANNOT_LOGIN_STOP);
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);

    }


    @Override
    public void logout() {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new MemberException(CANNOT_LOGOUT);
        }

        SecurityContextHolder.clearContext();

    }


    public void validateInput(JoinMemberInput input) {

        Optional<Member> existsMember = memberRepository.findByEmail(input.getEmail());
        if (existsMember.isPresent()) {

            if (existsMember.get().getStatus().equals(MemberStatus.WITHDRAW)) {
                throw new MemberException(WITHDRAWAL_MEMBER);
            }
            throw new MemberException(EMAIL_EXIST);
        }

        existsMember = memberRepository.findByPhone(input.getPhone());
        if (existsMember.isPresent()) {
            throw new MemberException(PHONE_EXIST);
        }

        existsMember = memberRepository.findByNickName(input.getNickName());
        if (existsMember.isPresent()) {
            throw new MemberException(NICKNAME_EXIST);
        }

    }


    public void validatePassword(String password) {

        if (password.length() < 8) {
            throw new MemberException(PASSWORD_LENGTH_MORE_THAN_8);
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
            if ('a' <= password.charAt(i) && password.charAt(i) <= 'z'
                || 'A' <= password.charAt(i) && password.charAt(i) <= 'Z') {
                character = true;
                break;
            }
        }

        if (!character || !digit || !special) {

            if (!character && digit && special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_CHARACTER);
            }

            if (character && !digit && special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_DIGIT);
            }

            if (character && digit && !special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_SPECIAL);
            }

            if (!character && !digit && special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_DIGIT_AND_CHARACTER);
            }

            if (character && !digit && !special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_DIGIT_AND_SPECIAL);
            }

            if (!character && digit && !special) {
                throw new MemberException(PASSWORD_NOT_CONTAIN_CHARACTER_AND_SPECIAL);
            }
        }

    }


    @Override
    public void join(JoinMemberInput input) {

        validateInput(input);
        validatePassword(input.getPassword());

        String password = input.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(password);
        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder().email(input.getEmail()).address(input.getAddress())
            .birth(input.getBirth()).gender(input.getGender()).password(encPassword)
            .nickName(input.getNickName()).phone(input.getPhone()).status(MemberStatus.REQ)
            .emailAuthKey(uuid).build();
        memberRepository.save(member);

        String subject = "In and Out 회원 가입을 축하드립니다.";
        String text = "<p>안녕하세요. In And Out 입니다.</p><p>아래 링크를 누르시면 회원 가입이 완료됩니다.</p>"
            + "<div><a href='http://localhost:8080/api/signup/sending?id=" + uuid
            + "'>가입 완료</a></div>";
        mailComponent.send(input.getEmail(), subject, text);

    }


    @Override
    public void emailAuth(String uuid) {

        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);
        if (!optionalMember.isPresent()) {
            throw new MemberException(EMAIL_AUTH_KEY_NOT_EXIST);
        }
        Member member = optionalMember.get();
        member.setStatus(MemberStatus.ING);
        memberRepository.save(member);

    }


    @Override
    public void validateEmail(String email) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new MemberException(MEMBER_NOT_EXIST);
        }
    }


    @Override
    public void validatePhone(String email, String phone) {

        Member member = memberRepository.findByEmailAndPhone(email, phone)
            .orElseThrow(() -> new MemberException(PHONE_NOT_EXIST));

        String uuid = UUID.randomUUID().toString();
        String subject = "In and Out 비밀번호 초기화";
        String text = "<p>안녕하세요. In And Out 입니다.</p><p>아래 링크를 누르시면 비밀번호 초기화가 완료됩니다.</p>"
            + "<div><a href='http://localhost:8080/api/password/email/phone/sending?id=" + uuid
            + "'>비밀번호 초기화</a></div>";
        mailComponent.send(email, subject, text);

        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        member.setResetPasswordKey(uuid);
        memberRepository.save(member);

    }


    @Override
    public void resetPassword(String uuid, ResetPasswordInput input) {

        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            throw new MemberException(RESET_PASSWORD_KEY_NOT_EXIST);
        }

        if (!input.getNewPassword().equals(input.getConfirmNewPassword())) {
            throw new MemberException(MemberErrorCode.CONFIRM_PASSWORD);
        }

        Member member = optionalMember.get();
        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new MemberException(RESET_PASSWORD_KEY_EXPIRED);
        }

        validatePassword(input.getNewPassword());
        String encPassword = bCryptPasswordEncoder.encode(input.getNewPassword());
        member.setPassword(encPassword);
        memberRepository.save(member);

    }


    @Override
    public MemberDto getInfo(String email) {

        if (email == null) {
            throw new MemberException(CANNOT_GET_INFO);
        }
        Member member = memberRepository.findByEmail(email).get();
        return MemberDto.builder()
            .s3ImageUrl(getS3ImageUrl(member.getMemberS3ImageKey()))
            .nickName(member.getNickName())
            .phone(member.getPhone())
            .gender(member.getGender())
            .address(member.getAddress())
            .birth(member.getBirth())
            .build();

    }

    private String getS3ImageUrl(String s3ImageKey) {
        return amazonS3Client.getUrl(S3Bucket, s3ImageKey).toString();
    }

    private String addFileToS3(MultipartFile file) {

        String key = LocalDateTime.now() + " member " + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(
                new PutObjectRequest(S3Bucket, key, file.getInputStream(), objectMetadata)
            );
        } catch (IOException e) {
            throw new MemberException(CANNOT_UPLOAD_IMAGE);
        }

        return key;
    }


    @Override
    public void updateInfo(String email, UpdateMemberInput input, MultipartFile file) {

        Member member = memberRepository.findByEmail(email).get();
        String previousUsedPhone = member.getPhone();
        String previousUsedNickname = member.getNickName();

        if (input.getNickName().contains(" ") || input.getPhone().contains(" ")
            || input.getGender().contains(" ")) {
            throw new MemberException(CONTAINS_BLANK);
        }

        String inputPhone = input.getPhone();
        String inputNickname = input.getNickName();
        if (!previousUsedPhone.equals(inputPhone)) {
            Optional<Member> existPhoneMember = memberRepository.findByPhone(inputPhone);
            if (existPhoneMember.isPresent()) {
                throw new MemberException(PHONE_EXIST);
            }
        }

        if (!previousUsedNickname.equals(inputNickname)) {
            Optional<Member> existNicknameMember = memberRepository.findByNickName(inputNickname);
            if (existNicknameMember.isPresent()) {
                throw new MemberException(NICKNAME_EXIST);
            }
        }

        member.setNickName(input.getNickName());
        member.setPhone(input.getPhone());
        member.setBirth(input.getBirth());
        member.setAddress(input.getAddress());
        member.setGender(input.getGender());
        member.setMemberS3ImageKey(addFileToS3(file));
        memberRepository.save(member);

    }


    @Override
    public void changePassword(String email, ChangePasswordInput input) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new MemberException(EMAIL_NOT_EXIST);

        }

        Member member = optionalMember.get();
        if (!bCryptPasswordEncoder.matches(input.getPassword(), member.getPassword())) {
            throw new MemberException(PASSWORD_NOT_MATCH);
        }

        validatePassword(input.getNewPassword());
        member.setPassword(bCryptPasswordEncoder.encode(input.getNewPassword()));
        memberRepository.save(member);

    }


    @Override
    public void withdraw(String email, String password) {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new MemberException(EMAIL_NOT_EXIST);
        }

        Member member = optionalMember.get();
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(PASSWORD_NOT_MATCH);
        }

        member.setStatus(MemberStatus.WITHDRAW);
        member.setDeleteDt(LocalDateTime.now());
        member.setPassword(null);
        member.setNickName(null);
        member.setPhone(null);
        member.setBirth(null);
        member.setAddress(null);
        member.setGender(null);
        member.setMemberS3ImageKey(null);
        member.setResetPasswordKey(null);
        member.setResetPasswordLimitDt(null);
        member.setEmailAuthKey(null);
        memberRepository.save(member);


    }

}