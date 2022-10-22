package com.rezero.inandout.income.service;

import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NOT_MATCH_MEMBER_AND_INCOME;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_CATEGORY;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_INCOME;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.exception.IncomeException;
import com.rezero.inandout.exception.errorcode.IncomeErrorCode;
import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.entity.IncomeCategory;
import com.rezero.inandout.income.model.DeleteIncomeInput;
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
import org.mockito.ArgumentCaptor;
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
        @DisplayName("성공")
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
        @DisplayName("실패 - member 없음")
        void addIncome_fail_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            String email = "test@email.com";
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.addIncome(email, incomes));

            //then
            assertEquals(exception.getErrorCode(), NO_MEMBER);
        }

        @Test
        @DisplayName("실패 - category 없음")
        void addIncome_fail_no_category() {
            //given
            incomes = new ArrayList<>();
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            //when
            String email = "test@email.com";
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.addIncome(email, incomes));

            //then
            assertEquals(exception.getErrorCode(), IncomeErrorCode.NO_CATEGORY);
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
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.getIncomeList("test",
                LocalDate.of(2020, 10, 1),
                LocalDate.of(2020, 10, 1)
            ));

            //then
            assertEquals(exception.getErrorCode(), NO_MEMBER);
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

    @Nested
    @DisplayName("수입리스트 수정")
    class updateIncomeList {

        Member member = Member.builder()
            .memberId(10L)
            .build();

        IncomeInput incomeInput1 = IncomeInput.builder()
            .incomeId(1L)
            .incomeDt(LocalDate.now().minusMonths(1))
            .incomeItem("초콜릿")
            .detailIncomeCategoryId(100L)
            .incomeAmount(2000)
            .incomeMemo("income1-memo")
            .build();

        IncomeInput incomeInput2 = IncomeInput.builder()
            .incomeId(2L)
            .incomeDt(LocalDate.now())
            .incomeItem("새우깡")
            .detailIncomeCategoryId(100L)
            .incomeAmount(3500)
            .incomeMemo("income2-memo")
            .build();
        DetailIncomeCategory detailIncomeCategory = DetailIncomeCategory.builder()
            .detailIncomeCategoryId(100L)
            .detailIncomeCategoryName("간식")
            .build();

        Income IncomeHistory = Income.builder()
            .incomeId(2L)
            .member(member)
            .incomeDt(LocalDate.now())
            .incomeItem("매운새우깡")
            .detailIncomeCategory(detailIncomeCategory)
            .incomeAmount(113500)
            .incomeMemo("updateIncome-memo")
            .build();

        List<IncomeInput> incomes = new ArrayList<>();

        @Test
        @DisplayName("성공")
        void updateIncome_success() {
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.of(IncomeHistory));

            given(detailIncomeCategoryRepository.findByDetailIncomeCategoryId(any()))
                .willReturn(Optional.of(detailIncomeCategory));

            //when
            incomeService.updateIncome(member.getEmail(), incomes);
            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

            //then
            verify(incomeRepository, times(1)).saveAll(captor.capture());
            assertEquals(captor.getValue().size(), 2);

        }


        @Test
        @DisplayName("실패 - 유저 없음")
        void updateIncome_fail_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            String email = "test@email.com";
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.updateIncome(email, incomes));

            //then
            assertEquals(exception.getErrorCode(), NO_MEMBER);
        }

        @Test
        @DisplayName("실패 - 지출내역 없음")
        void updateIncome_fail_no_incomeHistory() {
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.empty());

            given(detailIncomeCategoryRepository.findByDetailIncomeCategoryId(any()))
                .willReturn(Optional.of(detailIncomeCategory));

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.updateIncome(member.getEmail(), incomes));

            //then
            assertEquals(exception.getErrorCode(), NO_INCOME);
        }


        @Test
        @DisplayName("실패 - 카테고리 없음")
        void updateIncome_fail_no_category() {
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(detailIncomeCategoryRepository.findByDetailIncomeCategoryId(any()))
                .willReturn(Optional.empty());

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.updateIncome(member.getEmail(), incomes));

            //then
            assertEquals(exception.getErrorCode(), NO_CATEGORY);
        }

        @Test
        @DisplayName("실패 - 해당 수입내역의 유저가 아님")
        void updateIncome_fail_not_match_member_and_income() {
            incomes.add(incomeInput1);
            incomes.add(incomeInput2);

            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(Member.builder()
                    .memberId(11L)
                    .build()));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.of(IncomeHistory));

            given(detailIncomeCategoryRepository.findByDetailIncomeCategoryId(any()))
                .willReturn(Optional.of(detailIncomeCategory));

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.updateIncome(member.getEmail(), incomes));

            //then
            assertEquals(exception.getErrorCode(), NOT_MATCH_MEMBER_AND_INCOME);
        }

    }


    @Nested
    @DisplayName("수입리스트 삭제")
    class deleteIncomeMethod {

        Member member = Member.builder()
            .memberId(10L)
            .build();

        Member member2 = Member.builder()
            .memberId(11L)
            .build();

        Income income1 = Income.builder()
            .incomeId(1L)
            .member(member)
            .incomeDt(LocalDate.now().minusMonths(1))
            .incomeItem("월급")
            .incomeAmount(5000000)
            .incomeMemo("income1-memo")
            .build();

        Income income2 = Income.builder()
            .incomeId(2L)
            .member(member)
            .incomeDt(LocalDate.now())
            .incomeItem("서브프로젝트")
            .incomeAmount(350000)
            .incomeMemo("income2-memo")
            .build();

        Income income3 = Income.builder()
            .incomeId(3L)
            .member(member2)
            .incomeDt(LocalDate.now())
            .incomeItem("서브서브프로젝트")
            .incomeAmount(30000)
            .incomeMemo("income2-memo")
            .build();

        DeleteIncomeInput deleteIncomeInput = DeleteIncomeInput.builder()
            .incomeId(2L)
            .build();

        List<Income> incomeList = new ArrayList<>();
        List<DeleteIncomeInput> deleteIncomeInputList = new ArrayList<>();
        @Test
        @DisplayName("성공")
        void deleteIncome() {
            //given
            incomeList.add(income1);
            incomeList.add(income2);
            deleteIncomeInputList.add(deleteIncomeInput);

            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.of(income1));

            //when
            incomeService.deleteIncome(member.getEmail(), deleteIncomeInputList);
            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

            //then
            verify(incomeRepository, times(1)).deleteAllByIdInBatch(captor.capture());
            assertEquals(captor.getValue().size(), 1);

        }

        @Test
        @DisplayName("실패 - 유저 없음")
        void deleteIncome_fail_no_member() {
            //given
            incomeList.add(income1);
            incomeList.add(income2);
            deleteIncomeInputList.add(deleteIncomeInput);

            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.deleteIncome(any(), deleteIncomeInputList));

            //then
            assertEquals(exception.getErrorCode(), NO_MEMBER);

        }

        @Test
        @DisplayName("실패 - 수입내역 없음")
        void deleteIncome_fail_no_income() {
            //given
            incomeList.add(income1);
            incomeList.add(income2);
            deleteIncomeInputList.add(
                DeleteIncomeInput.builder()
                .incomeId(10L)
                .build()
            );

            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.empty());

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.deleteIncome(any(), deleteIncomeInputList));

            //then
            assertEquals(exception.getErrorCode(), NO_INCOME);
        }

        @Test
        @DisplayName("실패 - 해당 수입내역의 유저가 아님")
        void deleteIncome_fail_not_match_member_and_income() {
            //given
            incomeList.add(income3);
            deleteIncomeInputList.add(
                DeleteIncomeInput.builder()
                    .incomeId(3L)
                    .build()
            );

            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeRepository.findById(any()))
                .willReturn(Optional.of(income3));

            //when
            IncomeException exception = assertThrows(IncomeException.class,
                () -> incomeService.deleteIncome(any(), deleteIncomeInputList));

            //then
            assertEquals(exception.getErrorCode(), NOT_MATCH_MEMBER_AND_INCOME);
        }
    }
}