package com.deye.web.entity;

import com.deye.web.configuration.adapter.SQLAttributeDefinitionJsonConverter;
import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.enumerated.AttributeTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.List;
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

    @Column(columnDefinition = "json")
    @Convert(converter = SQLAttributeDefinitionJsonConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private AttributeDefinition metadata;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CategoryAttributeEntity> attributeCategories;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AttributeProductValuesEntity> attributeValuesForProducts;
}
