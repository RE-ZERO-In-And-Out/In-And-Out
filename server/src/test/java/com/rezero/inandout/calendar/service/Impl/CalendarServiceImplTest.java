package com.rezero.inandout.calendar.service.Impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.calendar.model.CalendarMonthlyDto;
import com.rezero.inandout.expense.service.base.impl.ExpenseServiceImpl;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import com.rezero.inandout.member.service.MemberService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {

    @Mock
    private IncomeServiceImpl incomeService;

    @Mock
    private MemberService memberService;

    @Mock
    private ExpenseServiceImpl expenseService;

    @InjectMocks
    private CalendarServiceImpl calendarService;

    @Nested
    @DisplayName("달력 수입&지출 조회 서비스 테스트")
    class getMonthlyIncomeCalendarMethod {

        List<CalendarIncomeDto> calendarIncomeDtoList = new ArrayList<>(Arrays.asList(
            CalendarIncomeDto.builder().incomeDt(LocalDate.of(2022, 10, 2))
                .item("수입1").amount(123456).build(),
            CalendarIncomeDto.builder().incomeDt(LocalDate.of(2022, 10, 28))
                .item("수입2").amount(54321).build()
        ));

        List<CalendarExpenseDto> calendarExpenseDtoList = new ArrayList<>(Arrays.asList(
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 2))
                .item("지출1").amount(98765).build(),
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 16))
                .item("지출2").amount(45678).build()
        ));

        CalendarMonthlyDto calendarMonthlyDto = CalendarMonthlyDto.builder()
            .year(2022).month(10)
            .calendarIncomeDtoList(calendarIncomeDtoList)
            .calendarExpenseDtoList(calendarExpenseDtoList)
            .build();

        @Test
        @DisplayName("성공")
        void getMonthlyIncomeCalendar() {
            //given
            given(incomeService.getMonthlyIncomeCalendar(any(), any(), any()))
                .willReturn(calendarIncomeDtoList);
            given(expenseService.getMonthlyExpenseCalendar(any(), any(), any()))
                .willReturn(calendarExpenseDtoList);

            //when
            CalendarMonthlyDto getCalendarMonthlyDto
                = calendarService.getCalendarIncomeAndExpenseList(
                    "test@naver.com",
                        LocalDate.of(2022, 10, 1),
                        LocalDate.of(2022, 10, 31)
            );

            //then
            verify(incomeService, times(1))
                .getMonthlyIncomeCalendar(any(), any(), any());
            verify(expenseService, times(1))
                .getMonthlyExpenseCalendar(any(), any(), any());

            assertEquals(getCalendarMonthlyDto.getMonth(), calendarMonthlyDto.getMonth());
            assertEquals(getCalendarMonthlyDto.getCalendarIncomeDtoList(), calendarIncomeDtoList);
        }

    }
}