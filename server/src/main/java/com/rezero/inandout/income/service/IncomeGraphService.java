package com.rezero.inandout.income.service;


import com.rezero.inandout.income.model.CategoryAndIncomeDto;
import com.rezero.inandout.income.model.IncomeInput;

import java.time.LocalDate;
import java.util.List;

public interface IncomeGraphService {


    void addAndUpdateIncome(String email, List<IncomeInput> incomeInputList);

    CategoryAndIncomeDto getCategoryAndIncomeDto(String name, LocalDate startDt, LocalDate endDt);
}
