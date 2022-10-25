package com.rezero.inandout.income.service.table.impl;

import com.rezero.inandout.income.model.*;
import com.rezero.inandout.income.service.base.IncomeService;
import com.rezero.inandout.income.service.table.IncomeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeTableServiceImpl implements IncomeTableService {

    private final IncomeService incomeService;

    @Override
    public void addAndUpdateIncome(String email, List<IncomeInput> incomeInputList) {
        List<IncomeInput> addIncomeInputList = new ArrayList<>();
        List<IncomeInput> updateIncomeInputList = new ArrayList<>();

        for (IncomeInput item : incomeInputList) {
            if(item.getIncomeId() != null) {
                updateIncomeInputList.add(item);
            } else {
                addIncomeInputList.add(item);
            }
        }

        incomeService.addIncome(email, addIncomeInputList);
        incomeService.updateIncome(email, updateIncomeInputList);
    }

    @Override
    public CategoryAndIncomeDto getCategoryAndIncomeDto(String email, LocalDate startDt, LocalDate endDt) {
        return CategoryAndIncomeDto.builder()
                .incomeDtoList(incomeService.getIncomeList(email, startDt, endDt))
                .incomeCategoryDtoList(incomeService.getIncomeCategoryList())
                .build();
    }
}
