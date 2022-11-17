package com.rezero.inandout.expense.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseInput {

    private Long expenseId;
    @NotNull(message = "지출일을 입력하세요.")
    private LocalDate expenseDt;
    @NotBlank(message = "지출한 물건을 입력하세요.")
    private String expenseItem;
    private Integer expenseCash;
    private Integer expenseCard;
    @NotNull(message = "세부카테고리아이디를 입력하세요.")
    @Min(value = 1, message = "세부카테고리아이디는 1이상입니다.")
    private Long detailExpenseCategoryId;
    private String expenseMemo;
}
