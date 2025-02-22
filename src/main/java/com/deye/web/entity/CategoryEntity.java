package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Category services for products domain separation and sorting.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "CATEGORY")
@Entity
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;
    private String description;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    private FileEntity image;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "category")
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CategoryAttributeEntity> categoryAttributes = new HashSet<>();

    public void setImage(String fileName) {
        FileEntity categoryImage = new FileEntity();
        categoryImage.setName(fileName);
        this.image = categoryImage;
    }

    public void addAttribute(CategoryAttributeEntity attribute) {
        categoryAttributes.add(attribute);
    }
}
