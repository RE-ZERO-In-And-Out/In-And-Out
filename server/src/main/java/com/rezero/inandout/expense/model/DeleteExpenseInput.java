package com.rezero.inandout.expense.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteExpenseInput {
    private Long expenseId;
}
