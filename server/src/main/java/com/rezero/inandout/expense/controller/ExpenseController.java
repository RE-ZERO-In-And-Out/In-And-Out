package com.rezero.inandout.expense.controller;

import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.table.ExpenseTableService;
import com.rezero.inandout.expense.service.base.ExpenseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExpenseTableService expenseTableService;

    @PostMapping
    @ApiOperation(value = "지출내역 저장(수정) API",
    notes = "지출내역 목록을 통해 저장과 수정을 할 수 있다.")
    public ResponseEntity<?> writeExpense(Principal principal,
            @ApiParam(value = "지출내역 목록 (저장은 expenseId 빼고 하면 됨)")
            @Valid @RequestBody List<ExpenseInput> inputs) {

        expenseTableService.addAndUpdateExpense(/*principal.getName()*/"hgd@gmail.com", inputs);

        return ResponseEntity.ok("지출이 정상적으로 등록되었습니다.");
    }

    @GetMapping
    @ApiOperation(value = "지출내역 목록 API",
    notes = "시작 날짜와 끝 날짜를 통해 지출내역 목록을 볼 수 있다.")
    public ResponseEntity<?> getExpense(Principal principal,
        @ApiParam(value = "조회 시작 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
        @ApiParam(value = "조회 끝 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        CategoryAndExpenseDto categoryAndExpenseDto =
                expenseTableService.getCategoryAndExpenseDto(
                        /*principal.getName()*/"hgd@gmail.com", startDt, endDt);

        return ResponseEntity.ok(categoryAndExpenseDto);
    }

    @DeleteMapping
    @ApiOperation(value = "지출내역 삭제 API",
    notes = "지출내역 Id 목록을 통해 지출내역 목록을 삭제할 수 있다.")
    public ResponseEntity<?> deleteExpense(Principal principal,
        @ApiParam(value = "지출내역 Id 목록")
        @RequestBody List<DeleteExpenseInput> inputs) {

        expenseService.deleteExpense(/*principal.getName()*/"hgd@gmail.com", inputs);
        return ResponseEntity.ok("지출이 정상적으로 삭제되었습니다.");
    }
}
