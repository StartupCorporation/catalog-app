package com.deye.web.entity;

import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private CategoryEntity category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private Set<FileEntity> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AttributeProductValuesEntity> attributesValuesForProduct = new HashSet<>();
    private int reservedQuantity;

    public void setImages(MultipartFile[] images) {
        for (MultipartFile image : images) {
            FileEntity file = new FileEntity(image);
            file.setProduct(this);
            this.images.add(file);
        }
    }

    public Optional<FileEntity> getImageByFile(MultipartFile file) {
        for (FileEntity fileEntity : this.images) {
            if (fileEntity.isFileCorrespondsToEntity(file)) {
                return Optional.of(fileEntity);
            }
        }
        return Optional.empty();
    }

    public Map<String, List<String>> getDirectoriesWithFilesNames() {
        return images.stream()
                .collect(Collectors.groupingBy(FileEntity::getDirectory, Collectors.mapping(FileEntity::getName, Collectors.toList())));
    }

    public Set<FileEntity> removeImagesByIds(Collection<UUID> ids) {
        if (this.images.size() > ids.size()) {
            Set<FileEntity> imagesToRemove = images.stream()
                    .filter(image -> ids.contains(image.getId()))
                    .collect(Collectors.toSet());
            boolean isRemoved = images.removeAll(imagesToRemove);
            return isRemoved ? imagesToRemove : Set.of();
        }
        throw new ActionNotAllowedException(ErrorCodeUtils.ACTION_NOT_ALLOWED_ERROR_CODE, ErrorMessageUtils.PRODUCT_IMAGES_DELETION_NOT_ALLOWED_ERROR_MESSAGE);
    }

    public void addAttributeValue(AttributeEntity attribute, Object value) {
        AttributeProductValuesEntity attributeProductValue = new AttributeProductValuesEntity();
        attributeProductValue.setProduct(this);
        attributeProductValue.setAttribute(attribute);
        attributeProductValue.setValue(value);
        AttributeProductValuesEntity existedAttributeValue = attributesValuesForProduct.stream()
                .filter(attributeValueForProduct -> attributeValueForProduct.equals(attributeProductValue))
                .findAny()
                .orElse(null);
        if (existedAttributeValue != null && !existedAttributeValue.getValue().equals(value)) {
            existedAttributeValue.setValue(attributeProductValue.getValue());
        } else {
            attributesValuesForProduct.add(attributeProductValue);
        }
    }

    public void removeAttributeValue(AttributeEntity attribute) {
        Optional<CategoryAttributeEntity> categoryAttributeOpt = this.category.getCategoryAttributes().stream()
                .filter(categoryAttr -> categoryAttr.getAttribute().equals(attribute))
                .findAny();
        if (categoryAttributeOpt.isEmpty()) {
            throw new EntityNotFoundException(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_TO_DELETE_WAS_NOT_FOUND_ERROR_MESSAGE);
        }
        CategoryAttributeEntity categoryAttribute = categoryAttributeOpt.get();
        if (categoryAttribute.isRequired()) {
            throw new ActionNotAllowedException(ErrorCodeUtils.ACTION_NOT_ALLOWED_ERROR_CODE, ErrorMessageUtils.ATTRIBUTE_DELETE_ACTION_NOT_ALLOWED_ERROR_MESSAGE);
        }
        AttributeProductValuesEntity attributeProductValue = attributesValuesForProduct.stream()
                .filter(attributeProduct -> attributeProduct.getAttribute().equals(attribute))
                .findAny()
                .orElse(null);
        if (attributeProductValue != null) {
            attributesValuesForProduct.remove(attributeProductValue);
        }
    }
}
