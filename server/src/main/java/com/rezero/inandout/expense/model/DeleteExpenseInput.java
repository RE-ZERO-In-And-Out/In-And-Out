package com.rezero.inandout.expense.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteExpenseInput {
    @NotNull(message = "삭제할 지출아이디를 입력하세요.")
    @Min(value = 1, message = "지출아이디는 1이상입니다.")
    private Long expenseId;
}
