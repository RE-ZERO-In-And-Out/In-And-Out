package com.rezero.inandout.diary.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.repository.DiaryRepository;
import com.rezero.inandout.exception.DiaryException;
import com.rezero.inandout.exception.MemberException;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private AmazonS3Client amazonS3Client;

    @InjectMocks
    private DiaryServiceImpl diaryServiceImpl;

    @Test
    void addDiary() {
    }

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

        @Test
        @DisplayName("일기 목록 조회 - 성공")
        void getDiaryList_success() throws MalformedURLException {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(diaryRepository.findByMemberAndDiaryDtBetween(any(), any(), any()))
                    .willReturn(diaries);

            URL url = new URL("https://inandoutimagebucket.s3.ap-northeast-2" +
                    ".amazonaws.com/2022-10-31T17%3A36%3A50.822%20diary%20%E1%84" +
                    "%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%A1%E1%84%8C%E1%85%B5.jpg");

            given(amazonS3Client.getUrl(any(), any()))
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

        PutObjectResult result = new PutObjectResult();

        MockMultipartFile file = new MockMultipartFile("file",
                "test.png",
                "image/png",
                "«‹png data>>".getBytes());

        @Test
        @DisplayName("일기 등록 - 성공")
        void addDiary_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(amazonS3Client.putObject(any()))
                    .willReturn(result);
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
        @DisplayName("일기 등록 - 실패 : 확인되지 않은 익셉션")
        void addDiary_fail_no() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(amazonS3Client.putObject(any()))
                    .willThrow(new DiaryException("??"));
            //when
            DiaryException exception = assertThrows(DiaryException.class,
                    () -> diaryServiceImpl.addDiary("hgd@gmail.com",
                            LocalDate.of(2022,10,1),
                            "굿", file)
            );

            //then
            assertEquals("??", exception.getMessage());
        }
    }
}