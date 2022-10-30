package com.rezero.inandout.calendar.service.Impl;

import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.calendar.model.CalendarMonthlyDto;
import com.rezero.inandout.calendar.service.CalendarService;
import com.rezero.inandout.expense.service.base.impl.ExpenseServiceImpl;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final IncomeServiceImpl incomeService;
    private final ExpenseServiceImpl expenseService;

    @Override
    public CalendarMonthlyDto getCalendarIncomeAndExpenseList(String email, LocalDate startDt, LocalDate endDt) {

        List<CalendarIncomeDto> calendarIncomeDtoList
            = incomeService.getMonthlyIncomeCalendar(email, startDt, endDt);
        List<CalendarExpenseDto> calendarExpenseDtoList
            = expenseService.getMonthlyExpenseCalendar(email, startDt, endDt);

        return CalendarMonthlyDto.builder()
            .year(startDt.getYear())
            .month(startDt.getMonthValue())
            .calendarIncomeDtoList(calendarIncomeDtoList)
            .calendarExpenseDtoList(calendarExpenseDtoList)
            .build();
    }
}
