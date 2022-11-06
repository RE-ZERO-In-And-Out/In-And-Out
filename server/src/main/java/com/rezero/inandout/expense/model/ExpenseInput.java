package com.rezero.inandout.expense.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseInput {

    private Long expenseId;
    @PastOrPresent(message = "지출일(과거 또는 현재)을 입력하세요.")
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
