package com.deye.web.repository;

import com.deye.web.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    @Query("select c from CategoryEntity c " +
            "left join fetch c.image " +
            "left join fetch c.categoryAttributes ca " +
            "left join fetch ca.attribute " +
            "where c.id = ?1")
    Optional<CategoryEntity> findByIdWithFetchedAttributesAndImage(UUID id);

    @Query("select c from CategoryEntity c " +
            "left join fetch c.image " +
            "left join fetch c.categoryAttributes ca " +
            "left join fetch ca.attribute ")
    List<CategoryEntity> findAllWithFetchedAttributesAndImage();
}
