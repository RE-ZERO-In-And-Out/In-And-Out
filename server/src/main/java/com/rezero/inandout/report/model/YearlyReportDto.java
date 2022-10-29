package com.rezero.inandout.report.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearlyReportDto {

    int year;

    int month;

    int monthlySum;

    List<ReportDto> report;

}
