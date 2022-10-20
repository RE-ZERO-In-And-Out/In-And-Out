package com.rezero.inandout.income.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.entity.IncomeCategory;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    private DetailIncomeCategoryRepository detailIncomeCategoryRepository;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    @Nested
    @DisplayName("수입내역 추가")
    class addIncomeMethod {

        Member member = Member.builder()
            .memberId(10L)
            .build();

        IncomeInput incomeInput1 = IncomeInput.builder()
            .incomeDt(LocalDate.now().minusMonths(1))
            .incomeItem("초콜릿")
            .detailIncomeCategoryId(100L)
            .incomeAmount(2000)
            .incomeMemo("income1-memo")
            .build();

        IncomeInput incomeInput2 = IncomeInput.builder()
            .incomeDt(LocalDate.now())
            .incomeItem("새우깡")
            .detailIncomeCategoryId(100L)
            .incomeAmount(3500)
            .incomeMemo("income2-memo")
            .build();

        DetailIncomeCategory detailIncomeCategory = DetailIncomeCategory.builder()
            .detailIncomeCategoryId(99L)
            .detailIncomeCategoryName("간식")
            .build();
        List<IncomeInput> incomes;


        @Test
        @DisplayName("수입내역 추가 - 성공")
        void addIncome_success() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(detailIncomeCategoryRepository.findByDetailIncomeCategoryId(any()))
                .willReturn(Optional.of(detailIncomeCategory));

            incomes = new ArrayList<>();
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            //when
            String email = "test@email.com";
            incomeService.addIncome(email, incomes);

            //then
            verify(incomeRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("수입내역 추가 실패 - member 없음")
        void addIncome_fail_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            String email = "test@email.com";

            //then
            assertThrows(RuntimeException.class, () -> incomeService.addIncome(email, incomes));
        }

        @Test
        @DisplayName("수입내역 추가 실패 - category 없음")
        void addIncome_fail_no_category() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            //when
            String email = "test@email.com";

            //then
            assertThrows(RuntimeException.class, () -> incomeService.addIncome(email, incomes));
        }

    }


    @Nested
    @DisplayName("수입리스트 가져오기")
    class getIncomeList {
        Member member = Member.builder()
            .memberId(10L)
            .build();

        DetailIncomeCategory detailIncomeCategory = DetailIncomeCategory.builder()
            .detailIncomeCategoryId(1000L)
            .detailIncomeCategoryName("testDetailIncomeCategoryName")
            .build();
        List<Income> incomeList = Arrays.asList(
            Income.builder()
                .incomeId(99L)
                .detailIncomeCategory(detailIncomeCategory)
                .incomeDt(LocalDate.now())
                .incomeItem("당근마켓판매")
                .incomeAmount(2000)
                .incomeMemo("TestMemo")
                .build(),
            Income.builder()
                .incomeId(98L)
                .detailIncomeCategory(detailIncomeCategory)
                .incomeDt(LocalDate.now())
                .incomeItem("중고나라판매")
                .incomeAmount(12000)
                .incomeMemo("TestMemo")
                .build()
        );

        @Test
        @DisplayName("성공")
        void getIncomeList_success() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findAllByMemberAndIncomeDtBetween(any(), any(), any()))
                .willReturn(incomeList);

            //when
            List<IncomeDto> incomeDtoList = incomeService.getIncomeList(
                "test",
                LocalDate.of(2020, 10, 1),
                LocalDate.of(2020, 10, 1)
            );

            //then
            assertEquals(incomeDtoList.size(), 2);
            assertEquals(incomeDtoList.get(0).getIncomeAmount(), 2000);
            assertEquals(incomeDtoList.get(1).getIncomeItem(), "중고나라판매");
        }

        @Test
        @DisplayName("실패 - member 없음")
        void getIncomeList_fail_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when

            //then
            assertThrows(RuntimeException.class, () -> incomeService.getIncomeList(
                "test",
                LocalDate.of(2020, 10, 1),
                LocalDate.of(2020, 10, 1)
            ));
        }
    }


    @Nested
    @DisplayName("수입카테고리리스트 가져오기")
    class getIncomeCategoryList {

        List<DetailIncomeCategory> detailIncomeCategoryList = Arrays.asList(
            DetailIncomeCategory.builder()
                .detailIncomeCategoryId(1L)
                .detailIncomeCategoryName("월급")
                .build(),
            DetailIncomeCategory.builder()
                .detailIncomeCategoryId(2L)
                .detailIncomeCategoryName("아르바이트")
                .build()
        );

        List<IncomeCategory> incomeCategoryList = Arrays.asList(
                IncomeCategory.builder()
                    .incomeCategoryId(1L)
                    .incomeCategoryName("주수입")
                .build()
            );

        @Test
        @DisplayName("성공")
        void getIncomeCategoryList_success() {
            //given
            given(incomeCategoryRepository.findAll())
                .willReturn(incomeCategoryList);

            given(detailIncomeCategoryRepository.findAllByIncomeCategory(any()))
                .willReturn(detailIncomeCategoryList);

            //when
            List<IncomeCategoryDto> incomeCategoryDtoList = incomeService.getIncomeCategoryList();

            //then
            assertEquals(incomeCategoryDtoList.get(0).getIncomeCategoryName(), "주수입");
            assertEquals(incomeCategoryDtoList.get(0).getDetailIncomeCategoryDtoList()
                .get(1).getDetailIncomeCategoryName(), "아르바이트");

        }
    }
}