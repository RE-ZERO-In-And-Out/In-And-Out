package com.rezero.inandout.income.service;

import com.rezero.inandout.exception.IncomeException;
import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.entity.IncomeCategory;
import com.rezero.inandout.income.model.*;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.*;

@Service
@RequiredArgsConstructor
public class IncomeGraphServiceImpl implements IncomeGraphService {

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
