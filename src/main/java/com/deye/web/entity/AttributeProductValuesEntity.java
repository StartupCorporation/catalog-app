package com.deye.web.entity;

import com.deye.web.configuration.adapter.SQLJsonConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ATTRIBUTE_PRODUCT_VALUE")
@Getter
@Setter
public class AttributeProductValuesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private AttributeEntity attribute;

    @Column(columnDefinition = "json")
    @Convert(converter = SQLJsonConverter.class)
    private Map<String, Object> value;
}
