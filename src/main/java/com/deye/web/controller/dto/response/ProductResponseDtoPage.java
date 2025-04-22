package com.deye.web.controller.dto.response;

import com.deye.web.controller.dto.RangeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ProductResponseDtoPage {
    private List<ProductResponseDto> content;
    private RangeDto priceRange;
    private Long totalElements;

    public ProductResponseDtoPage(List<ProductResponseDto> content) {
        this.content = content;
        Optional<Float> minPrice = content.stream()
                .map(ProductResponseDto::getPrice)
                .min(Float::compareTo);
        Optional<Float> maxPrice = content.stream()
                .map(ProductResponseDto::getPrice)
                .max(Float::compareTo);
        if (minPrice.isPresent()) {
            priceRange = new RangeDto(minPrice.get(), maxPrice.get());
        }
    }
}
