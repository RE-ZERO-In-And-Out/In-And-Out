package com.rezero.inandout.income.entity;

import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.member.entity.Member;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class MonthlyIncomeReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyIncomeReportId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne          // 양방향 매핑도 고려하기
    @JoinColumn(name = "detail_income_category_id")
    private DetailIncomeCategory detailIncomeCategory;

    private Integer monthlyIncomeReportYear;
    private Integer monthlyIncomeReportMonth;
    private Integer monthlyIncomeAmount;
}
