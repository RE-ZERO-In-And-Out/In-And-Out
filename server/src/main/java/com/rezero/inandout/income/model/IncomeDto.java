package com.rezero.inandout.income.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeDto {
    private Long incomeId;
    private Long detailIncomeCategoryId;

    private LocalDate incomeDt;
    private String incomeItem;
    private Integer incomeAmount;
    private String incomeMemo;
}
