package com.rezero.inandout.income.model;

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
public class DetailIncomeCategoryDto {
    private Long detailIncomeCategoryId;
    private String detailIncomeCategoryName;
}
