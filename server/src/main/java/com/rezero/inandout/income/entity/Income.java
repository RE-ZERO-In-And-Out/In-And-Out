package com.rezero.inandout.income.entity;


import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_income_category_id")
    private DetailIncomeCategory detailIncomeCategory;

    private LocalDate incomeDt;
    private String incomeItem;
    private Integer incomeAmount;
    private String incomeMemo;

    public static IncomeDto toDto(Income income) {

        return IncomeDto.builder()
            .incomeId(income.getIncomeId())
            .detailIncomeCategoryId(income.getDetailIncomeCategory().getDetailIncomeCategoryId())
            .incomeDt(income.getIncomeDt())
            .incomeItem(income.getIncomeItem())
            .incomeAmount(income.getIncomeAmount())
            .incomeMemo(income.getIncomeMemo())
            .build();
    }

    public static List<IncomeDto> toDtoList (List<Income> incomeList) {
        List<IncomeDto> incomeDtoList = new ArrayList<>();

        for (Income item : incomeList) {
            incomeDtoList.add(Income.toDto(item));
        }

        return incomeDtoList;
    }
}
