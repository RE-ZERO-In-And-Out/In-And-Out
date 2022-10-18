package com.rezero.inandout.expense.controller;

import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.model.Message;
import com.rezero.inandout.expense.service.ExpenseService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> writeExpense(Principal principal, @Valid @RequestBody List<ExpenseInput> inputs) {
        expenseService.addExpense(principal.getName(), inputs);
        return ResponseEntity.ok(new Message("지출이 정상적으로 등록되었습니다."));
    }

}
