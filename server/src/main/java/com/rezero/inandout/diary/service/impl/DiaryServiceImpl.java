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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Service awsS3Service;
    private static final String dir = "diary";
    private static final String deleteFile = "delete";
    private static final String nullFile = "null";

    @Override
    public List<DiaryDto> getDiaryList(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        List<Diary> diaries = diaryRepository.findByMemberAndDiaryDtBetween(member, startDt, endDt);

        List<DiaryDto> diaryDtos = new ArrayList<>();

        for (Diary diary : diaries) {
            String s3ImageUrl = "";
            if (!diary.getDiaryS3ImageKey().isEmpty()) {
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

        String fileContent;

        try {
            fileContent = new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!fileContent.equals(nullFile)) {
            s3ImageKey = awsS3Service.addImageAndGetKey(dir, file);
            log.info("[S3 Image save] dir: " + dir + "/ member: " + email);
        }

        Diary diary = Diary.builder()
                .member(member)
                .diaryDt(diaryDt)
                .text(text)
                .diaryS3ImageKey(s3ImageKey)
                .build();

        diaryRepository.save(diary);
    }

    @Override
    public void updateDiary(String email, Long diaryId, LocalDate diaryDt, String text, MultipartFile file) {
        Member member = findMemberByEmail(email);

        Diary updateDiary = findDiaryByDiaryIdAndMember(diaryId, member);

        Optional<Diary> optionalDateExistDiary = diaryRepository.findByMemberAndDiaryDt(member, diaryDt);

        if (optionalDateExistDiary.isPresent()) {
            Diary dateExistDiary = optionalDateExistDiary.get();

            if (dateExistDiary.getDiaryDt() != updateDiary.getDiaryDt()) {
                throw new DiaryException(DiaryErrorCode.THIS_DATE_EXIST_DIARY);
            }
        }

        if (!updateDiary.getDiaryS3ImageKey().isEmpty()) {
            awsS3Service.deleteImage(member.getMemberS3ImageKey());
            log.info("[S3 Image delete] dir: " + dir + "/ member: " + email);
        }

        String s3ImageKey = updateDiary.getDiaryS3ImageKey();

        String fileContent;

        try {
            fileContent = new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (fileContent.equals(deleteFile)) {
            awsS3Service.deleteImage(s3ImageKey);
            log.info("[S3 Image delete] dir: " + dir + "/ member: " + email);
            s3ImageKey = "";
        } else if (!fileContent.equals(nullFile)) {
            s3ImageKey = awsS3Service.addImageAndGetKey(dir, file);
            log.info("[S3 Image save] dir: " + dir + "/ member: " + email);
        }

        updateDiary.setDiaryDt(diaryDt);
        updateDiary.setText(text);
        updateDiary.setDiaryS3ImageKey(s3ImageKey);

        diaryRepository.save(updateDiary);
    }

    @Override
    public void deleteDiary(String email, Long diaryId) {
        Member member = findMemberByEmail(email);

        Diary deleteDiary = findDiaryByDiaryIdAndMember(diaryId, member);

        String deleteDiaryKey = deleteDiary.getDiaryS3ImageKey();

        if (!deleteDiaryKey.isEmpty()) {
            awsS3Service.deleteImage(deleteDiary.getDiaryS3ImageKey());
            log.info("[S3 Image delete] dir: " + dir + "/ member: " + email);
        }

        diaryRepository.delete(deleteDiary);
    }

    private Diary findDiaryByDiaryIdAndMember(Long diaryId, Member member) {
        return diaryRepository.findByDiaryIdAndMember(diaryId, member)
                .orElseThrow(() -> new DiaryException(DiaryErrorCode.NOT_EXIST_DIARY));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
    }
}
