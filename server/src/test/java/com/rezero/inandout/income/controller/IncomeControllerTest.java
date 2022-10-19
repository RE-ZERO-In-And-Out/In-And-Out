package com.rezero.inandout.income.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.income.service.IncomeServiceImpl;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

@WebMvcTest(IncomeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("IncomeController 테스트")
class IncomeControllerTest {

    @MockBean
    private IncomeServiceImpl incomeService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private IncomeRepository incomeRepository;

    @MockBean
    private DetailIncomeCategoryRepository detailIncomeCategoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void addIncome() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("testMember@gmail.com")
            .password("1234")
            .build();

        List<IncomeInput> incomeInputList = new ArrayList<>();
        incomeInputList.add(IncomeInput.builder()
                .member(member)
                .detailIncomeCategoryId(99L)
                .incomeDt(LocalDate.now())
                .incomeItem("초콜릿")
                .incomeAmount(2000)
                .incomeMemo("TestMemo")
            .build());

        String incomeInputListJson = objectMapper.writeValueAsString(incomeInputList);

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user,null);

        //when
        mockMvc.perform(
            post("/api/income")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken)
                .content(incomeInputListJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<List<IncomeInput>> captor = ArgumentCaptor.forClass(List.class);

        //then
        verify(incomeService, times(1)).addIncome(any(), captor.capture());
        assertEquals(captor.getValue().get(0).getIncomeMemo(), "TestMemo");

    }
}