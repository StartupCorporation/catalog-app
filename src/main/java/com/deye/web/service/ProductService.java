package com.deye.web.service;

import com.deye.web.async.listener.transactions.events.DeletedProductEvent;
import com.deye.web.async.listener.transactions.events.ReservationResultEvent;
import com.deye.web.async.listener.transactions.events.SavedProductEvent;
import com.deye.web.async.util.RabbitMqEvent;
import com.deye.web.controller.dto.*;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDtoPage;
import com.deye.web.entity.*;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.dlq.ActionNotAllowedSkipDLQException;
import com.deye.web.repository.ProductRepository;
import com.deye.web.repository.spricification.ProductFilterSpecification;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.deye.web.util.mapper.ImageMapper;
import com.deye.web.util.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryService categoryService;
    private final CategoryAttributeService categoryAttributeService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductFilterSpecification productFilterSpecification;
    private final ProductAttributeService productAttributeService;
    private final FileService fileService;
    private final ImageMapper imageMapper;

    @Transactional
    public void save(CreateProductDto createProductDto) {
        String productName = createProductDto.getName();
        log.info("Starting to save product: {}", productName);

        CategoryEntity category = categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(createProductDto.getCategoryId());
        log.info("Category found: {}", category.getName());

        log.info("Starting to set properties for product: {}", productName);
        ProductEntity product = createProduct(createProductDto, category);
        log.info("Product: {} properties are set", product.getName());
        productRepository.saveAndFlush(product);
        List<CreateImageDto> createImages = new ArrayList<>();
        for (FileDto fileDto : createProductDto.getImages()) {
            MultipartFile image = fileDto.getFile();
            FileEntity file = fileService.getFileEntityByFile(product.getImages(), image).get();
            createImages.add(new CreateImageDto(image, file));
        }
        eventPublisher.publishEvent(new SavedProductEvent(product, createImages, null));
    }

    private ProductEntity createProduct(CreateProductDto createProductDto, CategoryEntity category) {
        ProductEntity product = new ProductEntity();
        product.setName(createProductDto.getName());
        product.setDescription(createProductDto.getDescription());
        product.setPrice(createProductDto.getPrice());
        product.setStockQuantity(createProductDto.getStockQuantity());
        product.setCategory(category);
        setImages(product, createProductDto.getImages());

        Map<UUID, Object> attributesValuesToSave = createProductDto.getAttributesValuesToSave();
        addAttributesValues(product, attributesValuesToSave);
        return product;
    }

    public void setImages(ProductEntity product, List<FileDto> images) {
        images.sort(Comparator.comparing(FileDto::getOrder));
        for (FileDto image : images) {
            FileEntity file = fileService.createFileEntity(image, product);
            product.getImages().add(file);
        }
    }

    private void addAttributesValues(ProductEntity product, Map<UUID, Object> attributesValuesToSave) {
        String productName = product.getName();
        log.info("Validating product: {} attributes", productName);
        categoryAttributeService.validateCategoryAttributesValues(product, attributesValuesToSave);
        log.info("Product: {} attributes are validated successfully", productName);
        CategoryEntity category = product.getCategory();
        if (attributesValuesToSave != null) {
            for (UUID attributeId : attributesValuesToSave.keySet()) {
                Object value = attributesValuesToSave.get(attributeId);
                AttributeEntity attribute = category.getCategoryAttributes().stream()
                        .map(CategoryAttributeEntity::getAttribute)
                        .filter(attr -> attr.getId().equals(attributeId))
                        .findAny()
                        .get();
                productAttributeService.addAttributeValue(product, attribute, value);
            }
        }
    }

    @Transactional
    public ProductResponseDtoPage getAll(ProductFilterDto productFilterDto, Pageable pageable) {
        log.info("Fetching all products");
        List<ProductEntity> products = productRepository.findAll(productFilterSpecification.filterBy(productFilterDto));
        log.info("Found {} products", products.size());
        List<ProductResponseDto> productsResponseDto = products.stream()
                .map(productMapper::toProductResponseDto)
                .toList();
        return new ProductResponseDtoPage(productsResponseDto, pageable);
    }

    @Transactional
    public ProductResponseDto getById(UUID id) {
        log.info("Fetching product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {} ({})", product.getName(), product.getId());
        return productMapper.toProductResponseDto(product);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        log.info("Product found: {}", product.getId());
        productRepository.delete(product);
        List<DeleteImageDto> deleteImages = imageMapper.toDeleteImageDtoList(product.getImages());
        eventPublisher.publishEvent(new DeletedProductEvent(id, deleteImages));
    }

    @Transactional
    public void update(UUID id, UpdateProductDto updateProductDto) {
        log.info("Updating product by ID={}", id);
        ProductEntity product = getProductEntityById(id);
        List<FileDto> imagesToAdd = updateProductDto.getImagesToAdd();
        List<UUID> imagesIdsToRemove = updateProductDto.getImagesIdsToRemove();
        Map<UUID, Object> attributesValuesToSave = updateProductDto.getAttributesValuesToSave();
        Set<UUID> attributesIdsToRemove = updateProductDto.getAttributesIdsToRemove();
        if (updateProductDto.getName() != null && !updateProductDto.getName().equals(product.getName())) {
            product.setName(updateProductDto.getName());
        }
        if (updateProductDto.getDescription() != null && !updateProductDto.getDescription().equals(product.getDescription())) {
            product.setDescription(updateProductDto.getDescription());
        }
        if (updateProductDto.getPrice() != null && !updateProductDto.getPrice().equals(product.getPrice())) {
            product.setPrice(updateProductDto.getPrice());
        }
        if (updateProductDto.getStockQuantity() != null && !updateProductDto.getStockQuantity().equals(product.getStockQuantity())) {
            product.setStockQuantity(updateProductDto.getStockQuantity());
        }
        Set<FileEntity> removedImages = Set.of();
        if (imagesIdsToRemove != null) {
            removedImages = fileService.removeFileEntitiesByIds(product.getImages(), imagesIdsToRemove);
        }
        MultipartFile[] filesToAdd = null;
        if (imagesToAdd != null) {
            setImages(product, imagesToAdd);
            filesToAdd = imagesToAdd.stream()
                    .map(FileDto::getFile)
                    .toArray(MultipartFile[]::new);
        }
        if (attributesValuesToSave != null) {
            addAttributesValues(product, attributesValuesToSave);
        }
        if (attributesIdsToRemove != null) {
            product.getAttributesValuesForProduct().stream()
                    .map(AttributeProductValuesEntity::getAttribute)
                    .filter(attribute -> attributesIdsToRemove.contains(attribute.getId()))
                    .collect(Collectors.toSet())
                    .forEach(attributeEntity -> productAttributeService.removeAttributeValue(product, attributeEntity));
        }
        productRepository.saveAndFlush(product);
        eventPublisher.publishEvent(new SavedProductEvent(product, imageMapper.toCreateImageDtoList(product.getImages(), filesToAdd), imageMapper.toDeleteImageDtoList(removedImages)));
    }

    private ProductEntity getProductEntityById(UUID id) {
        log.info("Searching for product in database with ID={}", id);
        return productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(id)
                .orElseThrow(() -> {
                    log.error("Product with ID={} not found!", id);
                    return new EntityNotFoundException(ErrorCodeUtils.PRODUCT_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE);
                });
    }

    @Transactional
    public void reserveProducts(ReservationDto reservationDto) {
        Map<UUID, Integer> productsIdsAndQuantity = reservationDto.getProductsIdsAndQuantity();
        UUID orderId = reservationDto.getOrderId();
        List<ProductEntity> products = getAllProductsByIdsForReservation(reservationDto);
        for (ProductEntity product : products) {
            Integer quantityToReserve = productsIdsAndQuantity.get(product.getId());
            Integer alreadyReservedQuantity = product.getReservedQuantity();
            Integer quantityOnStock = product.getStockQuantity();
            Integer totalReservationQuantity = quantityToReserve + alreadyReservedQuantity;
            if (quantityOnStock <= totalReservationQuantity) {
                log.error("Quantity on stock is less then total reservation quantity for product: {}", product.getId());
                eventPublisher.publishEvent(new ReservationResultEvent(orderId, RabbitMqEvent.FAILED_TO_RESERVE_PRODUCTS));
                throw new ActionNotAllowedSkipDLQException("Quantity on stock is less then total reservation quantity");
            }
            product.setReservedQuantity(totalReservationQuantity);
        }
        productRepository.saveAll(products);
        eventPublisher.publishEvent(new ReservationResultEvent(orderId, RabbitMqEvent.PRODUCTS_RESERVED_FOR_ORDER));
    }

    @Transactional
    public void finishReservation(ReservationDto reservationDto) {
        Map<UUID, Integer> productsIdsAndQuantity = reservationDto.getProductsIdsAndQuantity();
        List<ProductEntity> products = getAllProductsByIdsForReservation(reservationDto);
        for (ProductEntity product : products) {
            Integer orderedQuantity = productsIdsAndQuantity.get(product.getId());
            Integer reservedQuantity = product.getReservedQuantity();
            Integer quantityOnStock = product.getStockQuantity();
            product.setReservedQuantity(reservedQuantity - orderedQuantity);
            product.setStockQuantity(quantityOnStock - orderedQuantity);
        }
        productRepository.saveAll(products);
    }

    private List<ProductEntity> getAllProductsByIdsForReservation(ReservationDto reservationDto) {
        Map<UUID, Integer> productsIdsAndQuantity = reservationDto.getProductsIdsAndQuantity();
        Set<UUID> productsIds = productsIdsAndQuantity.keySet();
        List<ProductEntity> products = productRepository.findAllById(productsIds);
        if (products.size() != productsIds.size()) {
            throw new EntityNotFoundException(ErrorCodeUtils.PRODUCT_NOT_FOUND_ERROR_CODE, ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE);
        }
        return products;
    }
}
