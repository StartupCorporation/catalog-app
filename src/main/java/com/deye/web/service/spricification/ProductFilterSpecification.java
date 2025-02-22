package com.deye.web.service.spricification;

import com.deye.web.controller.dto.PriceRangeFilterDto;
import com.deye.web.controller.dto.ProductFilterDto;
import com.deye.web.entity.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductFilterSpecification {

    public Specification<ProductEntity> filterBy(ProductFilterDto productFilterDto) {
        return ((root, query, criteriaBuilder) -> {
            if (productFilterDto == null) {
                return criteriaBuilder.and();
            }
            List<Predicate> predicates = new ArrayList<>();
            String productName = productFilterDto.getName();
            List<UUID> productCategoriesIds = productFilterDto.getCategoriesIds();
            PriceRangeFilterDto productPriceRangeFilter = productFilterDto.getPriceRange();

            if (StringUtils.isNotBlank(productName)) {
                Predicate productNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + productName.toLowerCase() + "%");
                predicates.add(productNamePredicate);
            }
            if (productCategoriesIds != null && !productCategoriesIds.isEmpty()) {
                Predicate productCategoriesPredicate = root.get("category").get("id").in(productCategoriesIds);
                predicates.add(productCategoriesPredicate);
            }
            if (productPriceRangeFilter != null) {
                Predicate productPricePredicate = criteriaBuilder.between(root.get("price"), productPriceRangeFilter.getMin(), productPriceRangeFilter.getMax());
                predicates.add(productPricePredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
