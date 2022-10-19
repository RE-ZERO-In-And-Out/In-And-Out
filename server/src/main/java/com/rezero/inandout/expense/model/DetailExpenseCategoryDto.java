package com.rezero.inandout.expense.model;

import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.ExpenseCategory;
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
public class DetailExpenseCategoryDto {
    private Long detailExpenseCategoryId;
    private String detailExpenseCategoryName;

    public static DetailExpenseCategoryDto toDto (DetailExpenseCategory detailExpenseCategory) {
        return DetailExpenseCategoryDto.builder()
            .detailExpenseCategoryId(detailExpenseCategory.getDetailExpenseCategoryId())
            .detailExpenseCategoryName(detailExpenseCategory.getDetailExpenseCategoryName())
            .build();
    }

    public static List<DetailExpenseCategoryDto> toDtos (List<DetailExpenseCategory> detailExpenseCategories) {
        List<DetailExpenseCategoryDto> detailExpenseCategoryDtos = new ArrayList<>();

        for (DetailExpenseCategory detailExpenseCategory : detailExpenseCategories) {
            detailExpenseCategoryDtos.add(toDto(detailExpenseCategory));
        }

        return detailExpenseCategoryDtos;
    }
}
