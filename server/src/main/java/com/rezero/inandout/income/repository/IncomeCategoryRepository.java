package com.rezero.inandout.income.repository;

import com.rezero.inandout.income.entity.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

}
