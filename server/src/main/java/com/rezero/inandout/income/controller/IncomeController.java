package com.rezero.inandout.income.controller;

import com.rezero.inandout.income.model.CategoryAndIncomeDto;
import com.rezero.inandout.income.model.DeleteIncomeInput;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.service.IncomeServiceImpl;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeServiceImpl incomeService;

    @PostMapping
    public ResponseEntity<?> addIncome(Principal principal,
                                        @RequestBody @Validated List<IncomeInput> incomeInputList) {
        List<IncomeInput> addIncomeInputList = new ArrayList<>();
        List<IncomeInput> updateIncomeInputList = new ArrayList<>();

        for (IncomeInput item : incomeInputList) {
            if(item.getIncomeId() != null) {
                updateIncomeInputList.add(item);
            } else {
                addIncomeInputList.add(item);
            }
        }

        incomeService.addIncome(principal.getName(), addIncomeInputList);
        incomeService.updateIncome(principal.getName(), updateIncomeInputList);

        return ResponseEntity.ok().body("저장에 성공했습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getIncomeListAndDetailCategoryList(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDt) {

        CategoryAndIncomeDto categoryAndIncomeDto = CategoryAndIncomeDto.builder()
            .incomeDtoList(incomeService.getIncomeList(principal.getName(), startDt, endDt))
            .incomeCategoryDtoList(incomeService.getIncomeCategoryList())
            .build();

        return ResponseEntity.ok(categoryAndIncomeDto);
    }


    @DeleteMapping
    public ResponseEntity<?> deleteIncome(Principal principal,
                             @RequestBody List<DeleteIncomeInput> deleteIncomeInputList) {

        incomeService.deleteIncome(principal.getName(), deleteIncomeInputList);

        return ResponseEntity.ok().body("삭제에 성공했습니다.");
    }

}
