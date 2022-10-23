package com.rezero.inandout.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.expense.model.*;
import com.rezero.inandout.expense.service.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ExpenseController 테스트")
class ExpenseControllerTest {

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("지출내역 추가 및 수정")
    void writeExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        List<ExpenseInput> list = Arrays.asList(
            ExpenseInput.builder()
                .expenseDt(LocalDate.now())
                .expenseItem("쌀과자")
                .expenseCash(1000)
                .expenseCard(0)
                .detailExpenseCategoryId(1L)
                .expenseMemo("냠냠")
                .build(),
            ExpenseInput.builder()
                .expenseId(1L)
                .expenseDt(LocalDate.now())
                .expenseItem("만두")
                .expenseCash(5000)
                .expenseCard(0)
                .detailExpenseCategoryId(1L)
                .expenseMemo("만두만두")
                .build()
        );

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        //when

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken)
                .content(objectMapper.writeValueAsString(list))
            ).andExpect(status().isOk())
            .andDo(print());

        ArgumentCaptor<List<ExpenseInput>> captor = ArgumentCaptor.forClass(List.class);

        //then
        verify(expenseService, times(1)).updateExpense(any(), captor.capture());
        assertEquals(captor.getValue().get(0).getExpenseItem(), "만두");
        verify(expenseService, times(1)).addExpense(any(), captor.capture());
        assertEquals(captor.getValue().get(0).getExpenseItem(), "쌀과자");
    }

    @Test
    @DisplayName("지출내역 조회")
    void getExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        DetailExpenseCategoryDto detailExpenseCategoryDto =
            DetailExpenseCategoryDto.builder()
            .detailExpenseCategoryId(1L)
            .detailExpenseCategoryName("간식")
            .build();

        List<DetailExpenseCategoryDto> detailExpenseCategoryDtos =
            Arrays.asList(
                detailExpenseCategoryDto
            );

        List<ExpenseCategoryDto> expenseCategoryDtos =
            Arrays.asList(
                ExpenseCategoryDto.builder()
                    .expenseCategoryId(1L)
                    .expenseCategoryName("식비")
                    .detailExpenseCategoryDtos(detailExpenseCategoryDtos)
                    .build()
            );

        given(expenseService.getExpenseCategories())
            .willReturn(expenseCategoryDtos);

        List<ExpenseDto> expenseDtos = Arrays.asList(
            ExpenseDto.builder()
                .expenseId(1L)
                .expenseDt(LocalDate.of(2020,10,20))
                .expenseItem("초코틴틴")
                .expenseCash(0)
                .expenseCard(1200)
                .detailExpenseCategoryId(1L)
                .expenseMemo("냠냠")
                .build()
        );

        given(expenseService.getExpenses(anyString(), any(), any()))
            .willReturn(expenseDtos);

        CategoryAndExpenseDto categoryAndExpenseDto = new CategoryAndExpenseDto();
        categoryAndExpenseDto.setExpenseCategoryDtos(expenseCategoryDtos);
        categoryAndExpenseDto.setExpenseDtos(expenseDtos);

        //when
        //then
        mockMvc.perform(get("/api/expense?startDt=2020-10-01&endDt=2020-10-31")
            .contentType(MediaType.APPLICATION_JSON)
            .principal(testingAuthenticationToken)
        ).andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.expenseCategoryDtos[0].expenseCategoryName").value("식비"))
            .andExpect(jsonPath("$.expenseCategoryDtos[0].detailExpenseCategoryDtos[0].detailExpenseCategoryName").value("간식"))
            .andExpect(jsonPath("$.expenseDtos[0].expenseItem").value("초코틴틴"));
    }

    @Test
    @DisplayName("지출내역 삭제")
    void deleteExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        List<DeleteExpenseInput> list = Arrays.asList(
                new DeleteExpenseInput(1L)
        );

        //when
        mockMvc.perform(delete("/api/expense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(testingAuthenticationToken)
                        .content(objectMapper.writeValueAsString(list))
                ).andExpect(status().isOk())
                .andDo(print());

        ArgumentCaptor<List<DeleteExpenseInput>> captor = ArgumentCaptor.forClass(List.class);
        //then
        verify(expenseService, times(1)).deleteExpense(any(), captor.capture());
        assertEquals(1L, captor.getValue().get(0).getExpenseId());

    }
}