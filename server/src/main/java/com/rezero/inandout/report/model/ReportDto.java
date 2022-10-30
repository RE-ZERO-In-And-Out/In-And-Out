package com.rezero.inandout.report.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDto {

    String category;

    int categorySum;

    double categoryRatio;

}
