package com.rezero.inandout.diary.entity;


import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import javax.persistence.*;

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
public class Diary extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    // 멤버랑 연관관계 매핑하기  => 양방향 or 단방향 ??
    @ManyToOne @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate diaryDt;
    private String text;

    @Column(name = "diary_s3_image_key")
    private String diaryS3ImageKey;
}
