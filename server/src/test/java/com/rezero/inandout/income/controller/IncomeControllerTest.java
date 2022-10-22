package com.rezero.inandout.income.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.income.model.DeleteIncomeInput;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.income.service.IncomeServiceImpl;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.MemberService;
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

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("수입내역 추가 및 수정")
    void addIncome() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("testMember@gmail.com")
            .password("1234")
            .build();

        List<IncomeInput> incomeInputList = new ArrayList<>();
        incomeInputList.add(IncomeInput.builder()
                .detailIncomeCategoryId(99L)
                .incomeDt(LocalDate.now())
                .incomeItem("당근마켓판매")
                .incomeAmount(2000)
                .incomeMemo("TestMemo")
            .build());
        incomeInputList.add(IncomeInput.builder()
                .incomeId(1L)
                .detailIncomeCategoryId(99L)
                .incomeDt(LocalDate.now())
                .incomeItem("중고나라판매")
                .incomeAmount(13000)
                .incomeMemo("TestMemo2")
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
        ArgumentCaptor<List<IncomeInput>> captor1 = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<IncomeInput>> captor2 = ArgumentCaptor.forClass(List.class);

        //then
        verify(incomeService, times(1)).addIncome(any(), captor1.capture());
        verify(incomeService, times(1)).updateIncome(any(), captor2.capture());
        assertEquals(captor1.getValue().get(0).getIncomeItem(), "당근마켓판매");
        assertEquals(captor2.getValue().get(0).getIncomeItem(), "중고나라판매");

    }


    @Test
    @DisplayName("수입내역 조회 및 카테고리내역 조회")
    void getIncomeListAndDetailCategoryList() throws Exception {
        Member member = Member.builder()
            .memberId(1L)
            .email("testMember@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user,null);

        List<IncomeDto> incomeDtoList = new ArrayList<>();
        incomeDtoList.add(IncomeDto.builder()
                .incomeId(99L)
                .incomeDt(LocalDate.now())
                .incomeItem("당근마켓판매")
                .incomeAmount(2000)
                .incomeMemo("TestMemo")
            .build());
        incomeDtoList.add(IncomeDto.builder()
            .incomeId(98L)
            .incomeDt(LocalDate.now())
            .incomeItem("중고나라판매")
            .incomeAmount(12000)
            .incomeMemo("TestMemo")
            .build());

        given(incomeService.getIncomeList(any(), any(), any()))
            .willReturn(incomeDtoList);

        List<DetailIncomeCategoryDto> detailIncomeCategoryDtoList = new ArrayList<>();
        detailIncomeCategoryDtoList.add(DetailIncomeCategoryDto.builder()
                .detailIncomeCategoryId(10L)
                .detailIncomeCategoryName("월급")
            .build());
        detailIncomeCategoryDtoList.add(DetailIncomeCategoryDto.builder()
                .detailIncomeCategoryId(11L)
                .detailIncomeCategoryName("아르바이트")
            .build());

        List<IncomeCategoryDto> incomeCategoryDtoList = new ArrayList<>();
        incomeCategoryDtoList.add(IncomeCategoryDto.builder()
                .incomeCategoryId(1L)
                .incomeCategoryName("주수입")
                .detailIncomeCategoryDtoList(detailIncomeCategoryDtoList)
            .build());

        given(incomeService.getIncomeCategoryList())
            .willReturn(incomeCategoryDtoList);

        //when
        mockMvc.perform(
            get("/api/income?startDt=2022-10-01&endDt=2022-10-30")
                .principal(testingAuthenticationToken))
            .andDo(print());


    }


    @Test
    @DisplayName("수입내역 삭제")
    void deleteIncome() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("testMember@gmail.com")
            .password("1234")
            .build();

        DeleteIncomeInput deleteIncomeInput = DeleteIncomeInput.builder()
            .IncomeId(2L)
            .build();

        List<DeleteIncomeInput> deleteIncomeInputList = new ArrayList<>();

        deleteIncomeInputList.add(deleteIncomeInput);

        String deleteIdListJson = objectMapper.writeValueAsString(deleteIncomeInputList);

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user,null);

        //when
        mockMvc.perform(
                delete("/api/income")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(deleteIdListJson)
                    .principal(testingAuthenticationToken))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        //then
        verify(incomeService, times(1)).deleteIncome(any(), captor.capture());
        assertEquals(captor.getValue().size(), 1);
    }

}