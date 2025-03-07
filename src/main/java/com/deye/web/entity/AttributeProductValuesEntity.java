package com.deye.web.entity;

import com.deye.web.configuration.adapter.sql.StringAndObjectConverter;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@Entity
@Table(name = "ATTRIBUTE_PRODUCT_VALUE")
@Getter
@Setter
@EqualsAndHashCode(of = {"product", "attribute"})
public class AttributeProductValuesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private AttributeEntity attribute;

    @Column(columnDefinition = "json")
    @Convert(converter = StringAndObjectConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private Object value;
}
