package com.rezero.inandout.income.entity;


import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.income.model.IncomeCategoryDto;
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
public class IncomeCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeCategoryId;

    private String incomeCategoryName;

    public static IncomeCategoryDto toDto(IncomeCategory incomeCategory) {
        return IncomeCategoryDto.builder()
            .incomeCategoryId(incomeCategory.getIncomeCategoryId())
            .incomeCategoryName(incomeCategory.getIncomeCategoryName())
            .build();
    }
}
