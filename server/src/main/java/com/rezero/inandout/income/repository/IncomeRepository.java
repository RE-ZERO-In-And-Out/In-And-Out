package com.rezero.inandout.income.repository;

import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findAllByMemberAndIncomeDtBetweenOrderByIncomeDt(Member member, LocalDate startDt, LocalDate endDt);

    Optional<Income> findByMemberAndIncomeId(Member member, Long incomeId);

}
