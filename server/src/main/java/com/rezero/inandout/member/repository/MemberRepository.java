package com.rezero.inandout.member.repository;

import com.rezero.inandout.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhone(String phone);

    Optional<Member> findByNickName(String email);

    Optional<Member> findByEmailAndPhone(String email, String phone);

    Optional<Member> findByEmailAuthKey(String emailAuthKey);

    Optional<Member> findByResetPasswordKey (String resetPasswordKey);

}
