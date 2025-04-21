package com.deye.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "FILE")
@NoArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;
    private String directory;

    @Column(name = "sort_order")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;

    public FileEntity(String name, String directory) {
        this.name = name;
        this.directory = directory;
    }

    public FileEntity(String name, String directory, Integer order) {
        this.name = name;
        this.directory = directory;
        this.order = order;
    }

    public FileEntity(String name, String directory, Integer order, ProductEntity product) {
        this.name = name;
        this.directory = directory;
        this.order = order;
        this.product = product;
    }
}
