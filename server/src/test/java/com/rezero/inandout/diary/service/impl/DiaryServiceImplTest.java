package com.rezero.inandout.diary.service.impl;

import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.repository.DiaryRepository;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiaryServiceImpl 테스트")
class DiaryServiceImplTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DiaryServiceImpl diaryServiceImpl;

    @Nested
    @DisplayName("일기 목록 조회")
    class getDiaryMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        List<Diary> diaries = Arrays.asList(
                Diary.builder()
                        .diaryId(1L)
                        .member(member)
                        .diaryDt(LocalDate.of(2020, 10, 1))
                        .text("굿")
                        .diaryPhotoUrl("/diary/photo/~~")
                        .build(),
                Diary.builder()
                        .diaryId(2L)
                        .member(member)
                        .diaryDt(LocalDate.of(2020, 10, 2))
                        .text("굿")
                        .diaryPhotoUrl("/diary/photo/~~")
                        .build(),
                Diary.builder()
                        .diaryId(3L)
                        .member(member)
                        .diaryDt(LocalDate.of(2020, 10, 3))
                        .text("굿")
                        .diaryPhotoUrl("/diary/photo/~~")
                        .build()
        );

        @Test
        @DisplayName("일기 목록 조회 - 성공")
        void getDiaryList_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByMemberAndDiaryDtBetween(any(), any(), any()))
                    .willReturn(diaries);
            //when
            List<DiaryDto> diaryDtos = diaryServiceImpl.getDiaryList("hgd@gmail.com",
                    LocalDate.of(2022, 10, 1),
                    LocalDate.of(2022, 10, 31));

            //then
            assertEquals(1L, diaryDtos.get(0).getDiaryId());
            assertEquals(2L, diaryDtos.get(1).getDiaryId());
            assertEquals(3L, diaryDtos.get(2).getDiaryId());
        }

        @Test
        @DisplayName("일기 목록 조회 - 실패")
        void getDiaryList_fail_notExistMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.empty());
            //when
            MemberException exception = assertThrows(MemberException.class,
                    () -> diaryServiceImpl.getDiaryList("hgd@gmail.com",
                            LocalDate.of(2022, 10, 1),
                            LocalDate.of(2022, 10, 31)));

            //then
            assertEquals(MemberErrorCode.MEMBER_NOT_EXIST, exception.getErrorCode());
        }
    }
}