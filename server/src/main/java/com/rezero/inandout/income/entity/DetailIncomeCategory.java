package com.rezero.inandout.income.entity;


import com.rezero.inandout.domain.BaseEntity;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailIncomeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailIncomeCategoryId;


    @ManyToOne
    @JoinColumn(name = "income_category_id")
    private IncomeCategory incomeCategory;

    private String detailIncomeCategoryName;

    public static DetailIncomeCategoryDto toDto(DetailIncomeCategory detailIncomeCategory) {
        return DetailIncomeCategoryDto.builder()
            .detailIncomeCategoryId(detailIncomeCategory.getDetailIncomeCategoryId())
            .detailIncomeCategoryName(detailIncomeCategory.getDetailIncomeCategoryName())
            .build();
    }

    public static List<DetailIncomeCategoryDto> toDtoList(List<DetailIncomeCategory> detailIncomeCategoryList) {
        List<DetailIncomeCategoryDto> detailIncomeCategoryDtoList = new ArrayList<>();

        for (DetailIncomeCategory item : detailIncomeCategoryList) {
            detailIncomeCategoryDtoList.add(DetailIncomeCategory.toDto(item));
        }

        return detailIncomeCategoryDtoList;
    }
}
