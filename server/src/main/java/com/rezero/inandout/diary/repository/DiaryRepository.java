package com.rezero.inandout.diary.repository;

import com.rezero.inandout.diary.entity.Diary;
import com.rezero.inandout.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByMemberAndDiaryDtBetween(Member member, LocalDate startDt, LocalDate endDt);

    Optional<Diary> findByMemberAndDiaryDt(Member member, LocalDate diaryDt);
}
