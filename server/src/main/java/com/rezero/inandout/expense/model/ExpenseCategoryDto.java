package com.rezero.inandout.expense.model;

import com.rezero.inandout.expense.entity.ExpenseCategory;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ExpenseCategoryDto {
    private Long expenseCategoryId;
    private String expenseCategoryName;
    private List<DetailExpenseCategoryDto> detailExpenseCategoryDtos;

    public static ExpenseCategoryDto toDto (ExpenseCategory expenseCategory) {
        return ExpenseCategoryDto.builder()
            .expenseCategoryId(expenseCategory.getExpenseCategoryId())
            .expenseCategoryName(expenseCategory.getExpenseCategoryName())
            .build();
    }
}
