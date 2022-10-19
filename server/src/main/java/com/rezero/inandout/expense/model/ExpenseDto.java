package com.rezero.inandout.expense.model;

import com.rezero.inandout.expense.entity.Expense;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private DetailExpenseCategoryDto detailExpenseCategoryDto;
    private LocalDate expenseDt;
    private String expenseItem;
    private Integer expenseCash;
    private Integer expenseCard;
    private String expenseMemo;

    public static ExpenseDto toDto(Expense expense) {
        return ExpenseDto.builder()
            .expenseId(expense.getExpenseId())
            .detailExpenseCategoryDto(
                DetailExpenseCategoryDto.toDto(
                    expense.getDetailExpenseCategory()
                )
            )
            .expenseDt(expense.getExpenseDt())
            .expenseItem(expense.getExpenseItem())
            .expenseCash(expense.getExpenseCash())
            .expenseCard(expense.getExpenseCard())
            .expenseMemo(expense.getExpenseMemo())
            .build();
    }

    public static List<ExpenseDto> toDtos(List<Expense> expenses) {
        List<ExpenseDto> dtos = new ArrayList<>();
        for (Expense expense : expenses) {
            dtos.add(toDto(expense));
        }
        return dtos;
    }
}
