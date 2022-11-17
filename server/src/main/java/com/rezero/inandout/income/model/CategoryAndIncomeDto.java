package com.rezero.inandout.income.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAndIncomeDto {
    private List<IncomeCategoryDto> incomeCategoryDtoList;
    private List<IncomeDto> incomeDtoList;
}
