package com.deye.web.entity;

import com.deye.web.security.dto.IdentityDetailsDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;

    public FileEntity(MultipartFile file) {
        this.name = extractNameFromFile(file);
        this.directory = generateFileDirectoryName();
    }

    public boolean isFileCorrespondsToEntity(MultipartFile file) {
        String fileName = extractNameFromFile(file);
        return this.name.equals(fileName);
    }

    private String extractNameFromFile(MultipartFile file) {
        return file.getOriginalFilename();
    }

    private String generateFileDirectoryName() {
        IdentityDetailsDto identityDetailsDto = (IdentityDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "identity-" + identityDetailsDto.getUsername() + "-directory";
    }
}
