package com.rezero.inandout.expense.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberService;
import java.time.LocalDate;
import java.util.ArrayList;
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

}