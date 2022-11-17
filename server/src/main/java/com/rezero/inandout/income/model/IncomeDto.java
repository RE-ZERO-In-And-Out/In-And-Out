package com.rezero.inandout.income.model;

import com.rezero.inandout.excel.model.IncomeExcelDto;
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
public class IncomeDto {
    private Long incomeId;
    private Long detailIncomeCategoryId;

    private LocalDate incomeDt;
    private String incomeItem;
    private Integer incomeAmount;
    private String incomeMemo;

    public static IncomeExcelDto toExcelDto(IncomeDto incomeDto) {
        return IncomeExcelDto.builder()
            .incomeDt(incomeDto.getIncomeDt())
            .incomeItem(incomeDto.getIncomeItem())
            .incomeAmount(incomeDto.getIncomeAmount())
            .incomeMemo(incomeDto.getIncomeMemo())
            .build();
    }
}
