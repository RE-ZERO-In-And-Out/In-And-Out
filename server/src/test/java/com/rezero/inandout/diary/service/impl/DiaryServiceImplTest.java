package com.rezero.inandout.diary.service.impl;

import com.rezero.inandout.awss3.AwsS3Service;
import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.repository.DiaryRepository;
import com.rezero.inandout.exception.DiaryException;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DiaryServiceImpl 테스트")
class DiaryServiceImplTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AwsS3Service awsS3Service;

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
                        .diaryS3ImageKey("2022-10-31T17:36:50.822 diary 강아지.jpg")
                        .build(),
                Diary.builder()
                        .diaryId(2L)
                        .member(member)
                        .diaryDt(LocalDate.of(2020, 10, 2))
                        .text("굿")
                        .diaryS3ImageKey("2022-10-31T17:36:50.822 diary 강아지.jpg")
                        .build(),
                Diary.builder()
                        .diaryId(3L)
                        .member(member)
                        .diaryDt(LocalDate.of(2020, 10, 3))
                        .text("굿")
                        .diaryS3ImageKey("2022-10-31T17:36:50.822 diary 강아지.jpg")
                        .build()
        );

        String url = "anyUrl";

        @Test
        @DisplayName("일기 목록 조회 - 성공")
        void getDiaryList_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByMemberAndDiaryDtBetween(any(), any(), any()))
                    .willReturn(diaries);

            given(awsS3Service.getImageUrl(any()))
                    .willReturn(url);

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

    @Nested
    @DisplayName("일기 등록")
    class addDiary {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();


        MockMultipartFile file = new MockMultipartFile("file",
                "test.png",
                "image/png",
                "«‹png data>>".getBytes());

        String s3ImageKey = "anyKey";

        Diary diary = Diary.builder().build();

        @Test
        @DisplayName("일기 등록 - 성공")
        void addDiary_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(awsS3Service.addImageAndGetKey(any(), any()))
                    .willReturn(s3ImageKey);
            //when
            diaryServiceImpl.addDiary("hgd@gmail.com",
                    LocalDate.of(2022,10,1),
                    "굿", file);

            //then
            verify(diaryRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("일기 등록 - 실패 : 멤버 없음")
        void addDiary_fail_memberNotExist() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.empty());

            //when
            MemberException exception = assertThrows(MemberException.class,
                    () -> diaryServiceImpl.addDiary("hgd@gmail.com",
                            LocalDate.of(2022,10,1),
                            "굿", file)
                    );

            //then
            assertEquals(MemberErrorCode.MEMBER_NOT_EXIST, exception.getErrorCode());
        }

        @Test
        @DisplayName("일기 등록 - 실패 : 이 날짜에는 일기가 존재합니다")
        void addDiary_fail_thisDateExistDiary() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByMemberAndDiaryDt(any(), any()))
                    .willReturn(Optional.of(diary));
            //when
            DiaryException exception = assertThrows(DiaryException.class,
                    () -> diaryServiceImpl.addDiary("hgd@gmail.com",
                            LocalDate.of(2022,10,1),
                            "굿", file)
            );

            //then
            assertEquals(DiaryErrorCode.THIS_DATE_EXIST_DIARY, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("일기 수정")
    class updateDiaryMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();


        MockMultipartFile file = new MockMultipartFile("file",
                "test.png",
                "image/png",
                "«‹png data>>".getBytes());

        String s3ImageKey = "anyKey";

        Diary diary = Diary.builder()
                .diaryId(1L)
                .text("anyText")
                .diaryDt(LocalDate.of(2022,10,1))
                .diaryS3ImageKey("anyKey")
                .build();

        Diary dateExistDiary = Diary.builder()
                .diaryId(2L)
                .diaryDt(LocalDate.of(2022,10,2))
                .build();

        @Test
        @DisplayName("일기 수정 - 성공")
        void updateDiary_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByDiaryIdAndMember(any(), any()))
                    .willReturn(Optional.of(diary));

            given(diaryRepository.findByMemberAndDiaryDt(any(), eq(LocalDate.of(2022, 10, 1))))
                    .willReturn(Optional.of(diary));

            awsS3Service.deleteImage(any());

            given(awsS3Service.addImageAndGetKey(any(), any()))
                    .willReturn(s3ImageKey);

            //when
            diaryServiceImpl.updateDiary(
                    "hgd@gmail.com",
                    1L,
                    LocalDate.of(2022, 10, 1),
                    "anyText",
                    file
            );
            //then
            verify(diaryRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("일기 수정 - 실패 : 멤버 없음")
        void updateDiary_fail_memberNotExist() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.empty());

            //when
            MemberException exception = assertThrows(MemberException.class,
                    () -> diaryServiceImpl.updateDiary(
                            "hgd@gmail.com",
                            1L,
                            LocalDate.of(2022, 10, 1),
                            "anyText",
                            file
                    )
            );
            //then
            assertEquals(MemberErrorCode.MEMBER_NOT_EXIST, exception.getErrorCode());
        }

        @Test
        @DisplayName("일기 수정 - 실패 : 일기 없음")
        void updateDiary_fail_notExistDiary() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByDiaryIdAndMember(any(), any()))
                    .willReturn(Optional.empty());

            //when
            DiaryException exception = assertThrows(DiaryException.class,
                    () -> diaryServiceImpl.updateDiary(
                            "hgd@gmail.com",
                            1L,
                            LocalDate.of(2022, 10, 1),
                            "anyText",
                            file
                    )
            );
            //then
            assertEquals(DiaryErrorCode.NOT_EXIST_DIARY, exception.getErrorCode());
        }

        @Test
        @DisplayName("일기 수정 - 수정하려는 날짜에 존재하는 일기")
        void updateDiary_fail_thisDateExistDiary() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByDiaryIdAndMember(any(), any()))
                    .willReturn(Optional.of(diary));

            given(diaryRepository.findByMemberAndDiaryDt(any(), eq(LocalDate.of(2022, 10, 2))))
                    .willReturn(Optional.of(dateExistDiary));

            //when
            DiaryException exception = assertThrows(DiaryException.class,
                    () -> diaryServiceImpl.updateDiary(
                            "hgd@gmail.com",
                            1L,
                            LocalDate.of(2022, 10, 2),
                            "anyText",
                            file
                    )
            );
            //then
            assertEquals(DiaryErrorCode.THIS_DATE_EXIST_DIARY, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("일기 삭제")
    class deleteDiaryMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        Diary diary = Diary.builder()
                .diaryId(1L)
                .text("anyText")
                .diaryDt(LocalDate.of(2022,10,1))
                .diaryS3ImageKey("anyKey")
                .build();

        String s3ImageKey = "anyKey";

        @Test
        @DisplayName("일기 삭제 - 성공")
        void deleteDiary_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByDiaryIdAndMember(any(), any()))
                    .willReturn(Optional.of(diary));

            awsS3Service.deleteImage(s3ImageKey);

            //when
            diaryServiceImpl.deleteDiary("hgd@gmail.com", 1L);

            //then
            verify(diaryRepository, times(1)).delete(any());

        }

        @Test
        @DisplayName("일기 삭제 - 실패 : 멤버 없음")
        void deleteDiary_fail_memberNotExist() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.empty());

            //when
            MemberException exception = assertThrows(MemberException.class,
                    () -> diaryServiceImpl.deleteDiary("hgd@gmail.com", 1L));

            //then
            assertEquals(MemberErrorCode.MEMBER_NOT_EXIST, exception.getErrorCode());

        }

        @Test
        @DisplayName("일기 삭제 - 실패 : 일기 없음")
        void deleteDiary_fail_diaryNotExist() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByDiaryIdAndMember(any(), any()))
                    .willReturn(Optional.empty());

            //when
            DiaryException exception = assertThrows(DiaryException.class,
                    () -> diaryServiceImpl.deleteDiary("hgd@gmail.com", 1L));

            //then
            assertEquals(DiaryErrorCode.NOT_EXIST_DIARY, exception.getErrorCode());

        }
    }
}