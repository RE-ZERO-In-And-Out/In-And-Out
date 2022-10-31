package com.rezero.inandout.calendar.controller;

import com.rezero.inandout.calendar.model.CalendarMonthlyDto;
import com.rezero.inandout.calendar.service.Impl.CalendarServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarServiceImpl calendarService;

    @GetMapping
    @ApiOperation(value = "달력화면의 수입&지출 내역 조회 API",
        notes = "로그인 시 메인화면. 해당 회원의 한 달의 수입&지출 내역이 조회된다.")
    public ResponseEntity<?> getCalendarIncomeAndExpenseList(Principal principal,
                    @ApiParam(value = "조회 시작 날짜", example = "2022-01-01")
                    @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
                    @ApiParam(value = "조회 끝 날짜", example = "2022-01-31")
                    @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        CalendarMonthlyDto calendarMonthlyDto
            = calendarService.getCalendarIncomeAndExpenseList(principal.getName(), startDt, endDt);

        return ResponseEntity.ok(calendarMonthlyDto);
    }

}
