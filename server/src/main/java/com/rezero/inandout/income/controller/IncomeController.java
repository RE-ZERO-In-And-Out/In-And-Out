package com.rezero.inandout.income.controller;

import com.rezero.inandout.income.model.CategoryAndIncomeDto;
import com.rezero.inandout.income.model.DeleteIncomeInput;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.service.table.IncomeTableService;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
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

@Validated
@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeServiceImpl incomeService;
    private final IncomeTableService incomeTableService;

    @ApiOperation(value = "수입 등록 API", notes = "수입내용을 입력하면 저장됩니다.")
    @PostMapping
    public ResponseEntity<?> addIncome(Principal principal,
                                        @RequestBody @Valid List<IncomeInput> incomeInputList) {

        incomeTableService.addAndUpdateIncome(principal.getName(), incomeInputList);

        return ResponseEntity.ok().body("저장에 성공했습니다.");
    }

    @ApiOperation(value = "수입 조회 API", notes = "조회할 기간을 입력하면 해당하는 수입내역이 조회됩니다.")
    @GetMapping
    public ResponseEntity<?> getIncomeListAndDetailCategoryList(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt) {

        CategoryAndIncomeDto categoryAndIncomeDto = incomeTableService.getCategoryAndIncomeDto(principal.getName(), startDt, endDt);

        return ResponseEntity.ok(categoryAndIncomeDto);
    }


    @ApiOperation(value = "수입 삭제 API", notes = "삭제할 내역의 아이디값을 입력하면 해당 내역이 삭제됩니다.")
    @DeleteMapping
    public ResponseEntity<?> deleteIncome(Principal principal,
                             @RequestBody @Valid List<DeleteIncomeInput> deleteIncomeInputList) {

        incomeService.deleteIncome(principal.getName(), deleteIncomeInputList);

        return ResponseEntity.ok().body("삭제에 성공했습니다.");
    }

}
