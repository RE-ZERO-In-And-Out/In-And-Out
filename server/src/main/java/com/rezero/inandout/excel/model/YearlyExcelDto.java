package com.rezero.inandout.excel.model;

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
public class YearlyExcelDto {

    String category;

    Integer jan;
    Integer feb;
    Integer mar;
    Integer apr;
    Integer may;
    Integer jun;
    Integer jul;
    Integer aug;
    Integer sep;
    Integer oct;
    Integer nov;
    Integer dec;
}
