package com.rezero.inandout.report.controller;

import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import com.rezero.inandout.report.model.YearlyTotalReportDto;
import com.rezero.inandout.report.service.ReportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @ApiOperation(value = "월간 수입내역 보고서 조회 API",
        notes = "조회할 기간을 입력하면 해당하는 월간 수입내역 보고서가 조회됩니다.")
    @GetMapping("/month/income")
    public ResponseEntity<List<ReportDto>> getMonthlyIncomeReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt) {
        List<ReportDto> reportDtos =
                reportService.getMonthlyIncomeReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok().body(reportDtos);
    }

    @ApiOperation(value = "연간 수입 보고서 조회 API",
        notes = "조회할 기간을 입력하면 해당하는 연간 수입 보고서가 조회됩니다.")
    @GetMapping("/year/income")
    public ResponseEntity<List<YearlyIncomeReportDto>> getYearlyIncomeReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 시작일", example = "2021-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 마지막일", example = "2022-09-30") LocalDate endDt) {
        List<YearlyIncomeReportDto> yearlyReportDtoList =
            reportService.getYearlyIncomeReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok().body(yearlyReportDtoList);
    }

    @ApiOperation(value = "월간 지출내역 보고서 조회 API",
        notes = "조회할 기간을 입력하면 해당하는 월간 지출내역 보고서가 조회됩니다.")
    @GetMapping("/month/expense")
    public ResponseEntity<List<ReportDto>> getMonthlyExpenseReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 시작일", example = "2021-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-30") LocalDate endDt) {

        List<ReportDto> reportDtos =
                reportService.getMonthlyExpenseReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok(reportDtos);
    }

    @ApiOperation(value = "연간 지출 보고서 조회 API",
        notes = "조회할 기간을 입력하면 해당하는 연간 지출 보고서가 조회됩니다.")
    @GetMapping("/year/expense")
    public ResponseEntity<List<YearlyExpenseReportDto>> getYearlyExpenseReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 시작일", example = "2021-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 마지막일", example = "2022-09-30") LocalDate endDt) {

        List<YearlyExpenseReportDto> yearlyReportDtos =
                reportService.getYearlyExpenseReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok(yearlyReportDtos);
    }

    @ApiOperation(value = "연간 보고서 조회 API",
        notes = "조회할 기간을 입력하면 해당하는 연간 보고서가 조회됩니다.")
    @GetMapping("/year")
    public ResponseEntity<YearlyTotalReportDto> getYearlyTotalReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 시작일", example = "2021-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 마지막일", example = "2022-09-30") LocalDate endDt) {
        YearlyTotalReportDto yearlyTotalReportDto =
            reportService.getYearlyTotalReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok().body(yearlyTotalReportDto);
    }

}
