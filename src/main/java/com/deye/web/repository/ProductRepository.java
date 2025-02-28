package com.deye.web.repository;

import com.deye.web.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    @Query("select p from ProductEntity p " +
            "left join fetch p.images " +
            "left join fetch p.category pc " +
            "left join fetch pc.categoryAttributes pcc " +
            "left join fetch pcc.attribute " +
            "left join fetch p.attributesValuesForProduct pa " +
            "left join fetch pa.attribute " +
            "where p.id = ?1")
    Optional<ProductEntity> findByIdWithFetchedImagesAndCategoryAndAttributes(UUID id);
}
