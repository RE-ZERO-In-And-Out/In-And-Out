package com.rezero.inandout.diary.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.repository.DiaryRepository;
import com.rezero.inandout.diary.service.DiaryService;
import com.rezero.inandout.exception.DiaryException;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3Client amazonS3Client;

    @Value(value = "${cloud.aws.bucket.name}")
    private String S3Bucket;

    @Override
    public List<DiaryDto> getDiaryList(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        List<Diary> diaries = diaryRepository.findByMemberAndDiaryDtBetween(member, startDt, endDt);

        List<DiaryDto> diaryDtos = new ArrayList<>();

        for (Diary diary : diaries) {
            diaryDtos.add(DiaryDto.builder()
                    .diaryId(diary.getDiaryId())
                    .nickName(diary.getMember().getNickName())
                    .diaryDt(diary.getDiaryDt())
                    .text(diary.getText())
                    .s3ImageUrl(getS3ImageUrl(diary.getDiaryS3ImageKey()))
                    .build());
        }

        return diaryDtos;
    }

    private String getS3ImageUrl(String s3ImageKey) {
        return amazonS3Client.getUrl(S3Bucket, s3ImageKey).toString();
    }


    @Override
    public void addDiary(String email, LocalDate diaryDt, String text, MultipartFile file) {
        Member member = findMemberByEmail(email);

        String s3ImageKey = addFileToS3(file);

        Diary diary = Diary.builder()
                .member(member)
                .diaryDt(diaryDt)
                .text(text)
                .diaryS3ImageKey(s3ImageKey)
                .build();

        diaryRepository.save(diary);
    }

    private String addFileToS3(MultipartFile file) {
        String key = LocalDateTime.now() + " diary " + file.getOriginalFilename();

        long size = file.getSize();

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        objectMetaData.setContentLength(size);

        // S3에 업로드
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(S3Bucket, key, file.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw new DiaryException(e.getMessage());
        }

        return key;
    }

    private Member findMemberByEmail(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
    }
}
