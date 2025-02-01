package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
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

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<FileEntity> images = new HashSet<>();

    public void setImages(Set<String> fileNames) {
        for (String fileName : fileNames) {
            FileEntity productImage = new FileEntity();
            productImage.setName(fileName);
            this.images.add(productImage);
        }
    }

    public Set<String> getImagesNames() {
        return images.stream()
                .map(FileEntity::getName)
                .collect(Collectors.toSet());
    }
}
