package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "CATEGORY_ATTRIBUTE")
@Getter
@Setter
public class CategoryAttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private AttributeEntity attribute;
    private boolean isRequired;
    private boolean isFilterable;
}
