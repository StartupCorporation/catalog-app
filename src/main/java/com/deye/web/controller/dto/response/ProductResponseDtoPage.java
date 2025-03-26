package com.deye.web.controller.dto.response;

import com.deye.web.controller.dto.RangeDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ProductResponseDtoPage extends PageImpl<ProductResponseDto> {
    private RangeDto priceRange;

    @JsonIgnore
    private Pageable pageable;

    public ProductResponseDtoPage(List<ProductResponseDto> content) {
        super(content);
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
