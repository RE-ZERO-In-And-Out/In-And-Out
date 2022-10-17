package com.rezero.inandout.member.entity;

import com.rezero.inandout.domain.BaseEntity;
import java.time.LocalDate;
import javax.persistence.Entity;
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
    private String address;     // 다음 API 사용 시 주소 3개 필요
    private String gender;
    private String memberPhotoUrl;
    private String status;
    private String resetPasswordKey;
    private String resetPasswordLimitDt;

}
