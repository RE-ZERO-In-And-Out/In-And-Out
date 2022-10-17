package com.rezero.inandout.expense.entity;

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
public class MonthlyExpenseReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlyExpenseReportId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "detail_expense_category_id")
    private DetailExpenseCategory detailExpenseCategory;

    private Integer monthlyExpenseReportYear;
    private Integer monthlyExpenseReportMonth;
    private Integer monthlyExpenseAmount;

}
