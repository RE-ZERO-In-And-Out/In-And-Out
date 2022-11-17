package com.rezero.inandout.income.entity;


import com.rezero.inandout.income.model.IncomeCategoryDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeCategory {

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
