package com.rezero.inandout.report.model;

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
public class YearlyIncomeReportDto {
    int year;

    int month;

    int monthlySum;

    List<ReportDto> incomeReport;
}
