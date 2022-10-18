package com.rezero.inandout.expense.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDto {

    private Long expenseId;
    private Long memberId;
    private DetailExpenseCategoryDto detailExpenseCategoryDto;
    private LocalDate expenseDt;
    private String expenseItem;
    private Integer expenseCash;
    private Integer expenseCard;
    private String expenseMemo;

}
