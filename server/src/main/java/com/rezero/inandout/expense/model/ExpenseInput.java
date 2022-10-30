package com.rezero.inandout.expense.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseInput {

    private Long expenseId;
    @NotBlank
    private LocalDate expenseDt;
    @NotBlank
    private String expenseItem;
    private Integer expenseCash;
    private Integer expenseCard;
    @NotBlank
    private Long detailExpenseCategoryId;
    private String expenseMemo;
}
