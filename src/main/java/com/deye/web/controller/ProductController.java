package com.deye.web.controller;

import com.deye.web.controller.dto.CreateProductDto;
import com.deye.web.controller.dto.UpdateProductDto;
import com.deye.web.controller.view.ProductView;
import com.deye.web.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<ProductView>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductView> getById(@PathVariable UUID id) {
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
