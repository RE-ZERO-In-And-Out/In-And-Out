package com.rezero.inandout.income.service;


import com.rezero.inandout.income.model.IncomeInput;
import java.util.List;

public interface IncomeService {

    void addIncome(String email, List<IncomeInput> incomeInputList);

}
