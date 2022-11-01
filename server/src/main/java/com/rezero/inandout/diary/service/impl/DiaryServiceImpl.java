package com.rezero.inandout.diary.service.impl;

import com.rezero.inandout.awss3.AwsS3Service;
import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.repository.DiaryRepository;
import com.rezero.inandout.diary.service.DiaryService;
import com.rezero.inandout.exception.DiaryException;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Service awsS3Service;
    private static final String dir = "diary";

    @Override
    public List<DiaryDto> getDiaryList(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        List<Diary> diaries = diaryRepository.findByMemberAndDiaryDtBetween(member, startDt, endDt);

        List<DiaryDto> diaryDtos = new ArrayList<>();

        for (Diary diary : diaries) {
            String s3ImageUrl = "";
            if (!diary.getDiaryS3ImageKey().isEmpty() || diary.getDiaryS3ImageKey() != "") {
                s3ImageUrl = awsS3Service.getImageUrl(diary.getDiaryS3ImageKey());
            }

            diaryDtos.add(DiaryDto.builder()
                    .diaryId(diary.getDiaryId())
                    .nickName(diary.getMember().getNickName())
                    .diaryDt(diary.getDiaryDt())
                    .text(diary.getText())
                    .s3ImageUrl(s3ImageUrl)
                    .build());
        }

        return diaryDtos;
    }

    @Override
    public void addDiary(String email, LocalDate diaryDt, String text, MultipartFile file) {
        Member member = findMemberByEmail(email);

        if(diaryRepository.findByMemberAndDiaryDt(member, diaryDt).isPresent()) {
            throw new DiaryException(DiaryErrorCode.THIS_DATE_EXIST_DIARY);
        }

        String s3ImageKey = "";

        if (file != null) {
            s3ImageKey = awsS3Service.addImageAndGetKey(dir, file);
        }

        Diary diary = Diary.builder()
                .member(member)
                .diaryDt(diaryDt)
                .text(text)
                .diaryS3ImageKey(s3ImageKey)
                .build();

        diaryRepository.save(diary);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
    }
}
