package com.rezero.inandout.income.repository;

import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.IncomeCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailIncomeCategoryRepository extends JpaRepository<DetailIncomeCategory, Long> {

    Optional<DetailIncomeCategory> findByDetailIncomeCategoryId(Long detailIncomeCategoryId);
    List<DetailIncomeCategory> findAllByIncomeCategory(IncomeCategory incomeCategory);
}
