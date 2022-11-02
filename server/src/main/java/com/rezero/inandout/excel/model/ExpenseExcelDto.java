package com.rezero.inandout.excel.model;

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
public class ExpenseExcelDto {

    private LocalDate expenseDt;

    private String ExpenseCategoryName;
    private String detailExpenseCategoryName;

    private String expenseItem;
    private Integer expenseCard;
    private Integer expenseCash;
    private String expenseMemo;
}
