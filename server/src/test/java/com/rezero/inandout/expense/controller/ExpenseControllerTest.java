package com.rezero.inandout.expense.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
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
    void writeExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        List<ExpenseInput> list = new ArrayList<>();
        list.add(
            new ExpenseInput(
            LocalDate.now(), "쌀과자", 1000,
            0, 1L, "냠냠"
            )
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
        verify(expenseService, times(1)).addExpense(any(), captor.capture());
        assertEquals(captor.getValue().get(0).getExpenseMemo(), "냠냠");
    }

    @Test
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
}