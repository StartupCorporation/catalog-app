package com.deye.web.controller.dto.response;

import com.deye.web.controller.dto.RangeDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ProductResponseDtoPage {
    private List<ProductResponseDto> content;
    private RangeDto priceRange;
    private Integer totalElements;

    public ProductResponseDtoPage(List<ProductResponseDto> allProducts, Pageable pageable) {
        int start = (int) Math.min(pageable.getOffset(), allProducts.size());
        int end = Math.min((start + pageable.getPageSize()), allProducts.size());
        this.content = allProducts.subList(start, end);
        this.totalElements = allProducts.size();
        Optional<Float> minPrice = allProducts.stream()
                .map(ProductResponseDto::getPrice)
                .min(Float::compareTo);
        Optional<Float> maxPrice = allProducts.stream()
                .map(ProductResponseDto::getPrice)
                .max(Float::compareTo);
        if (minPrice.isPresent()) {
            priceRange = new RangeDto(minPrice.get(), maxPrice.get());
        }
    }
}
