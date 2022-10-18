package com.rezero.inandout.expense.model;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
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
public class ExpenseInput {

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
