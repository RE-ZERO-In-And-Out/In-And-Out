package com.rezero.inandout.income.entity;


import com.fasterxml.jackson.databind.ser.Serializers.Base;
import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
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
public class Income extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "detail_income_category_id")
    private DetailIncomeCategory detailIncomeCategory;

    private LocalDate incomeDt;
    private String incomeItem;
    private Integer incomeAmount;
    private String incomeMemo;
}
