package com.rezero.inandout.report.model;

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
public class YearlyReportDto {
    int year;
    int month;

    String category;
    int categorySum;
    double categoryRatio;

}
