package com.deye.web.entity;

import com.deye.web.configuration.adapter.SQLJsonConverter;
import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ATTRIBUTE")
@Getter
@Setter
public class AttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private AttributeTypeEnum type;

    @Column(columnDefinition = "json")
    @Convert(converter = SQLJsonConverter.class)
    private Map<String, Object> metadata;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CategoryAttributeEntity> attributeCategories;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AttributeProductValuesEntity> attributeValuesForProducts;
}
