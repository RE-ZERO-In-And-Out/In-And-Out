package com.rezero.inandout.calendar.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarMonthlyDto {
    int year;
    int month;

    int incomeSum;
    int expenseSum;

    List<CalendarIncomeDto> calendarIncomeDtoList;
    List<CalendarExpenseDto> calendarExpenseDtoList;

}
