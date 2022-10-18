package com.rezero.inandout.income.controller;

import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.service.IncomeServiceImpl;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeServiceImpl incomeService;

    @PostMapping("/income")
    public ResponseEntity<?> addIncome(Principal principal,
                                        @RequestBody @Validated List<IncomeInput> incomeInputList) {
        incomeService.addIncome(principal.getName(), incomeInputList);
        return ResponseEntity.ok().body("저장에 성공했습니다.");
    }

}
