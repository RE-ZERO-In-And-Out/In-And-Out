package com.rezero.inandout.income.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
public class DeleteIncomeInput {

    @NotNull(message = "삭제할 수입아이디를 입력하세요.")
    @Min(value = 1, message = "수입아이디는 1이상입니다.")
    private Long incomeId;
}
