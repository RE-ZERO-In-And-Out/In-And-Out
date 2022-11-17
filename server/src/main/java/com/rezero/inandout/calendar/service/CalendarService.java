package com.rezero.inandout.calendar.service;

import com.rezero.inandout.calendar.model.CalendarMonthlyDto;
import java.time.LocalDate;

public interface CalendarService {

    CalendarMonthlyDto getCalendarIncomeAndExpenseList(String email, LocalDate startDt, LocalDate endDt);

}
