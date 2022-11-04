package com.rezero.inandout.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.calendar.model.CalendarMonthlyDto;
import com.rezero.inandout.calendar.service.Impl.CalendarServiceImpl;
import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(CalendarController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CalendarController 테스트")
class CalendarControllerTest {

    @MockBean
    CalendarServiceImpl calendarService;

    @MockBean
    MemberServiceImpl memberService;

    @MockBean
    PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("달력화면의 수입&지출 내역 조회")
    void getCalendarIncomeAndExpenseList() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

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

        given(calendarService.getCalendarIncomeAndExpenseList(any(), any(), any()))
            .willReturn(calendarMonthlyDto);

        //when
        mockMvc.perform(get("/api/calendar?startDt=2020-10-01&endDt=2020-10-31")
                .principal(testingAuthenticationToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.year")
                .value(calendarMonthlyDto.getYear()))
            .andExpect(jsonPath("$.month")
                .value(calendarMonthlyDto.getMonth()))
            .andExpect(jsonPath("$.calendarIncomeDtoList[0].item")
                .value(calendarIncomeDtoList.get(0).getItem()))
            .andExpect(jsonPath("$.calendarExpenseDtoList[1].amount")
                .value(calendarExpenseDtoList.get(1).getAmount()))
            .andDo(print());

        //then
        verify(calendarService, times(1)).getCalendarIncomeAndExpenseList(any(), any(), any());

    }

}