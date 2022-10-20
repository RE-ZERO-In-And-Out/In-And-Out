package com.rezero.inandout.income.service;


import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    void addIncome(String email, List<IncomeInput> incomeInputList);
    List<IncomeDto> getIncomeList(String email, LocalDate startDt, LocalDate endDt);
    List<IncomeCategoryDto> getIncomeCategoryList();
    List<DetailIncomeCategoryDto> getDetailIncomeCategory();
    void updateIncome(String email, List<IncomeInput> incomeInputList);
}
