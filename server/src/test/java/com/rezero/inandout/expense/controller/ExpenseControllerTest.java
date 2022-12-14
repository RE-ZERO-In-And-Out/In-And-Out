package com.rezero.inandout.expense.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.base.ExpenseService;
import com.rezero.inandout.expense.service.table.ExpenseTableService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.impl.MemberServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ExpenseController ?????????")
class ExpenseControllerTest {

    @MockBean
    private ExpenseService expenseService;
    @MockBean
    private ExpenseTableService expenseTableService;

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("???????????? ?????? ??? ??????")
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
                .expenseItem("?????????")
                .expenseCash(1000)
                .expenseCard(0)
                .detailExpenseCategoryId(1L)
                .expenseMemo("??????")
                .build(),
            ExpenseInput.builder()
                .expenseId(1L)
                .expenseDt(LocalDate.now())
                .expenseItem("??????")
                .expenseCash(5000)
                .expenseCard(0)
                .detailExpenseCategoryId(1L)
                .expenseMemo("????????????")
                .build()
        );

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        //when

        mockMvc.perform(post("/api/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken)
                .content(objectMapper.writeValueAsString(list))
            ).andExpect(status().isOk())
            .andDo(print());

        ArgumentCaptor<List<ExpenseInput>> captor = ArgumentCaptor.forClass(List.class);

        //then
        verify(expenseTableService, times(1)).addAndUpdateExpense(any(), captor.capture());
        assertEquals(captor.getValue().get(0).getExpenseItem(), "?????????");
        assertEquals(captor.getValue().get(1).getExpenseItem(), "??????");
    }

    @Test
    @DisplayName("???????????? ??????")
    void getExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        DetailExpenseCategoryDto detailExpenseCategoryDto =
            DetailExpenseCategoryDto.builder()
                .detailExpenseCategoryId(1L)
                .detailExpenseCategoryName("??????")
                .build();

        List<DetailExpenseCategoryDto> detailExpenseCategoryDtos =
            Arrays.asList(
                detailExpenseCategoryDto
            );

        List<ExpenseCategoryDto> expenseCategoryDtos =
            Arrays.asList(
                ExpenseCategoryDto.builder()
                    .expenseCategoryId(1L)
                    .expenseCategoryName("??????")
                    .detailExpenseCategoryDtos(detailExpenseCategoryDtos)
                    .build()
            );

        List<ExpenseDto> expenseDtos = Arrays.asList(
            ExpenseDto.builder()
                .expenseId(1L)
                .expenseDt(LocalDate.of(2020, 10, 20))
                .expenseItem("????????????")
                .expenseCash(0)
                .expenseCard(1200)
                .detailExpenseCategoryId(1L)
                .expenseMemo("??????")
                .build()
        );

        CategoryAndExpenseDto categoryAndExpenseDto = CategoryAndExpenseDto.builder()
            .expenseCategoryDtos(expenseCategoryDtos)
            .expenseDtos(expenseDtos)
            .build();

        given(expenseTableService.getCategoryAndExpenseDto(anyString(), any(), any()))
            .willReturn(categoryAndExpenseDto);

        //when
        //then
        mockMvc.perform(get("/api/expense?startDt=2020-10-01&endDt=2020-10-31")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken)
            ).andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.expenseCategoryDtos[0].expenseCategoryName").value("??????"))
            .andExpect(jsonPath(
                "$.expenseCategoryDtos[0].detailExpenseCategoryDtos[0].detailExpenseCategoryName").value(
                "??????"))
            .andExpect(jsonPath("$.expenseDtos[0].expenseItem").value("????????????"));
    }

    @Test
    @DisplayName("???????????? ??????")
    void deleteExpenseTest() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

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