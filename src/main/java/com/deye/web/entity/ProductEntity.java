package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private Float price;
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true, mappedBy = "product")
    private Set<FileEntity> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AttributeProductValuesEntity> attributesValuesForProduct;

    public void setImages(Set<String> fileNames) {
        for (String fileName : fileNames) {
            FileEntity productImage = new FileEntity();
            productImage.setName(fileName);
            productImage.setProduct(this);
            this.images.add(productImage);
        }
    }

    public void removeImages(List<String> fileNames) {
        Set<FileEntity> toRemove = images.stream()
                .filter(image -> fileNames.contains(image.getName()))
                .collect(Collectors.toSet());
        images.removeAll(toRemove);
    }

    public Set<String> getImagesNames() {
        return images.stream()
                .map(FileEntity::getName)
                .collect(Collectors.toSet());
    }
}
