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
public class IncomeExcelDto {

    private LocalDate incomeDt;

    private String IncomeCategoryName;
    private String detailIncomeCategoryName;

    private String incomeItem;
    private Integer incomeAmount;
    private String incomeMemo;
}
