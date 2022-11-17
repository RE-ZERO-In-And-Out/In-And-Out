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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final IncomeServiceImpl incomeService;
    private final ExpenseServiceImpl expenseService;

    @Override
    @Transactional
    public CalendarMonthlyDto getCalendarIncomeAndExpenseList(String email, LocalDate startDt, LocalDate endDt) {

        List<CalendarIncomeDto> calendarIncomeDtoList
            = incomeService.getMonthlyIncomeCalendar(email, startDt, endDt);
        List<CalendarExpenseDto> calendarExpenseDtoList
            = expenseService.getMonthlyExpenseCalendar(email, startDt, endDt);

        int incomeSum = 0;
        for (CalendarIncomeDto item : calendarIncomeDtoList) {
            incomeSum += item.getAmount();
        }

        int expenseSum = 0;
        for (CalendarExpenseDto item : calendarExpenseDtoList) {
            expenseSum += item.getAmount();
        }


        return CalendarMonthlyDto.builder()
            .year(startDt.getYear())
            .month(startDt.getMonthValue())
            .incomeSum(incomeSum)
            .expenseSum(expenseSum)
            .calendarIncomeDtoList(calendarIncomeDtoList)
            .calendarExpenseDtoList(calendarExpenseDtoList)
            .build();
    }
}
