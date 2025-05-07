package com.deye.web.controller;

import com.deye.web.controller.dto.CreateProductDto;
import com.deye.web.controller.dto.ProductFilterDto;
import com.deye.web.controller.dto.UpdateProductDto;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDtoPage;
import com.deye.web.controller.dto.response.ProductShortResponseDto;
import com.deye.web.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductResponseDtoPage> getAll(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @ModelAttribute @Valid ProductFilterDto productFilterDto) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAll(productFilterDto, pageable));
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<ProductShortResponseDto>> getProductsByIds(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        productService.deleteById(id);
    }

    @PostMapping
    public void save(@ModelAttribute @Valid CreateProductDto createProductDto) {
        productService.save(createProductDto);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable UUID id, @ModelAttribute @Valid UpdateProductDto updateProductDto) {
        productService.update(id, updateProductDto);
    }
}
