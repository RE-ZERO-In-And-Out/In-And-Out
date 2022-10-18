package com.rezero.inandout.income.model;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IncomeInput {

    @NotBlank
    private Long detailIncomeCategoryId;
    @NotBlank
    private LocalDate incomeDt;
    @NotBlank
    private String incomeItem;
    @NotBlank
    private Integer incomeAmount;
    private String incomeMemo;

}
