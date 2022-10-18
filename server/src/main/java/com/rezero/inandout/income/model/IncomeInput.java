package com.rezero.inandout.income.model;

import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IncomeInput {
    private Member member;
    @NotBlank
    private Long detailIncomeCategoryId;
    @NotBlank
    private LocalDate incomeDt;
    @NotBlank
    private String incomeItem;
    @NotBlank
    private Integer incomeAmount;
    @NotBlank
    private String incomeMemo;

}
