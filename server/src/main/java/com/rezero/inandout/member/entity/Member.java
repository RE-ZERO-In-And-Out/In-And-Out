package com.rezero.inandout.member.entity;

import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.member.model.MemberStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String email;
    private String password;
    private String nickName;
    private String phone;
    private LocalDate birth;
    private String address;
    private String gender;

    @Column(name = "member_s3_image_key")
    private String memberS3ImageKey;

    @Enumerated(value = EnumType.STRING)
    private MemberStatus status;

    private String resetPasswordKey;
    private LocalDateTime resetPasswordLimitDt;
    private String emailAuthKey;

    // oauth 추가
    private String provider;    // 구글, 네이버
    private String providerId;  // 구글 DB 내에서 회원의 pk를 의미


}
