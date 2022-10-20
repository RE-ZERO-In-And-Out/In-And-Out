package com.rezero.inandout.income.controller;

import com.rezero.inandout.income.model.CategoryAndIncomeDto;
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
        incomeService.addIncome(principal.getName(), incomeInputList);
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

}
