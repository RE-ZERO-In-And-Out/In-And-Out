package com.rezero.inandout.income.model;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeInput {

    private Long incomeId;

    @NotNull(message = "세부카테고리 아이디를 입력하세요.")
    @Min(value = 1, message = "세부카테고리 아이디는 1 이상입니다.")
    private Long detailIncomeCategoryId;

    @PastOrPresent(message = "수입일(과거 또는 현재)을 입력하세요.")
    private LocalDate incomeDt;

    @NotBlank(message = "수입 항목을 입력하세요.")
    private String incomeItem;

    @NotBlank
    private Integer incomeAmount;
    private String incomeMemo;

}
