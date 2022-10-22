package com.rezero.inandout.expense.controller;

import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> writeExpense(Principal principal, @Valid @RequestBody List<ExpenseInput> inputs) {

        List<ExpenseInput> addExpenseInputs = new ArrayList<>();
        List<ExpenseInput> updateExpenseInputs = new ArrayList<>();

        for (ExpenseInput expenseInput : inputs) {
            if (expenseInput.getExpenseId() != null) {
                updateExpenseInputs.add(expenseInput);
            } else {
                addExpenseInputs.add(expenseInput);
            }
        }

        expenseService.updateExpense(/*principal.getName()*/"hgd@gmail.com", updateExpenseInputs);
        expenseService.addExpense(/*principal.getName()*/"hgd@gmail.com", addExpenseInputs);

        return ResponseEntity.ok("지출이 정상적으로 등록되었습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getExpense(Principal principal,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        CategoryAndExpenseDto categoryAndExpenseDto = new CategoryAndExpenseDto();
        categoryAndExpenseDto.setExpenseCategoryDtos(expenseService.getExpenseCategories());
        categoryAndExpenseDto.setExpenseDtos(expenseService.getExpenses(/*principal.getName()*/"hgd@gmail.com", startDt, endDt));

        return ResponseEntity.ok(categoryAndExpenseDto);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExpense(Principal principal, @RequestBody List<DeleteExpenseInput> inputs) {
        expenseService.deleteExpense(/*principal.getName()*/"hgd@gmail.com", inputs);
        return ResponseEntity.ok("지출이 정상적으로 삭제되었습니다.");
    }
}
