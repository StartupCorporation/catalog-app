package com.deye.web.unit.service;

import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.FileEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.service.FileService;
import com.deye.web.util.error.ErrorMessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private MultipartFile multipartFile;

    private static final String BUCKET_NAME = "test-bucket";

    private static final String FILE_NAME = "test-file.txt";

    @Mock
    private MultipartFile file;


    // Test when the category is null
    @Test
    void testGetAllFilesByCategory_NullCategory() {
        FileService fileService = new FileService();

        // Expect an ActionNotAllowedException to be thrown
        assertThrows(ActionNotAllowedException.class, () -> fileService.getAllFilesByCategory(null));
    }

    // Test when the category has null products
    @Test
    void testGetAllFilesByCategory_CategoryWithNullProducts() {
        FileService fileService = new FileService();
        CategoryEntity category = new CategoryEntity();
        FileEntity categoryImage = new FileEntity();
        category.setImage(categoryImage);

        // Call the method and assert the result
        Set<FileEntity> result = fileService.getAllFilesByCategory(category);
        assertEquals(1, result.size());
        assertTrue(result.contains(categoryImage));
    }

    // Test when the category has products with images
    @Test
    void testGetAllFilesByCategory_CategoryWithProductsHavingImages() {
        FileService fileService = new FileService();
        CategoryEntity category = new CategoryEntity();
        FileEntity categoryImage = new FileEntity();
        category.setImage(categoryImage);

        ProductEntity product1 = new ProductEntity();
        FileEntity productImage1 = new FileEntity();
        product1.getImages().add(productImage1);

        ProductEntity product2 = new ProductEntity();
        FileEntity productImage2 = new FileEntity();
        product2.getImages().add(productImage2);

        category.setProducts(Arrays.asList(product1, product2));

        // Call the method and assert the result
        Set<FileEntity> result = fileService.getAllFilesByCategory(category);
        assertEquals(3, result.size());
        assertTrue(result.contains(categoryImage));
        assertTrue(result.contains(productImage1));
        assertTrue(result.contains(productImage2));
    }

    // Test when the category has products but no images
    @Test
    void testGetAllFilesByCategory_CategoryWithProductsButNoImages() {
        FileService fileService = new FileService();
        CategoryEntity category = new CategoryEntity();
        FileEntity categoryImage = new FileEntity();
        category.setImage(categoryImage);

        ProductEntity product1 = new ProductEntity();
        ProductEntity product2 = new ProductEntity();

        category.setProducts(Arrays.asList(product1, product2));

        // Call the method and assert the result
        Set<FileEntity> result = fileService.getAllFilesByCategory(category);
        assertEquals(1, result.size());
        assertTrue(result.contains(categoryImage));
    }

    @Test
    void createFileEntity_shouldReturnFileEntity_whenValidFileProvided() {
        // Arrange
        ReflectionTestUtils.setField(fileService, "bucketName", BUCKET_NAME);
        when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME);

        // Act
        FileEntity result = fileService.createFileEntity(multipartFile);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(FILE_NAME, result.getName(), "The file name should match the provided file name");
        assertEquals(BUCKET_NAME, result.getDirectory(), "The directory should match the bucket name");
    }

    @Test
    void createFileEntity_shouldThrowException_whenFileNameIsEmpty() {
        // Arrange
        when(multipartFile.getOriginalFilename()).thenReturn("");

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.createFileEntity(multipartFile);
        }, "An ActionNotAllowedException should be thrown when the file name is empty");

        assertEquals("File name is empty", exception.getMessage(), "Exception message should match");
    }

    @Test
    void createFileEntity_shouldThrowException_whenFileNameIsNull() {
        // Arrange
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.createFileEntity(multipartFile);
        }, "An ActionNotAllowedException should be thrown when the file name is null");

        assertEquals("File name is empty", exception.getMessage(), "Exception message should match");
    }

    // Test case for when the file name matches the FileEntity name
    @Test
    void testIsFileCorrespondsToFileEntity_FileNameMatches() {
        // Arrange
        FileService fileService = new FileService();
        String expectedFileName = "testFile.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(expectedFileName);

        when(file.getOriginalFilename()).thenReturn(expectedFileName);

        // Act
        boolean result = fileService.isFileCorrespondsToFileEntity(file, fileEntity);

        // Assert
        assertTrue(result, "The file name should match the FileEntity name.");
    }

    // Test case for when the file name does not match the FileEntity name
    @Test
    void testIsFileCorrespondsToFileEntity_FileNameDoesNotMatch() {
        // Arrange
        FileService fileService = new FileService();
        String fileName = "testFile.txt";
        String differentFileName = "differentFile.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(differentFileName);

        when(file.getOriginalFilename()).thenReturn(fileName);

        // Act
        boolean result = fileService.isFileCorrespondsToFileEntity(file, fileEntity);

        // Assert
        assertFalse(result, "The file name should not match the FileEntity name.");
    }

    // Test case for when the file name is empty, expecting an exception
    @Test
    void testIsFileCorrespondsToFileEntity_FileNameIsEmpty() {
        // Arrange
        FileService fileService = new FileService();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName("testFile.txt");

        when(file.getOriginalFilename()).thenReturn("");

        // Act & Assert
        Exception exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.isFileCorrespondsToFileEntity(file, fileEntity);
        });

        assertEquals("File name is empty", exception.getMessage(), "Expected ActionNotAllowedException for empty file name.");
    }

    // Test case for when the file name is null, expecting an exception
    @Test
    void testIsFileCorrespondsToFileEntity_FileNameIsNull() {
        // Arrange
        FileService fileService = new FileService();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName("testFile.txt");

        when(file.getOriginalFilename()).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.isFileCorrespondsToFileEntity(file, fileEntity);
        });

        assertEquals("File name is empty", exception.getMessage(), "Expected ActionNotAllowedException for null file name.");
    }

    // Test when the file has a valid name
    @Test
    void testExtractFileNameFromFile_ValidFileName() {
        // Arrange
        FileService fileService = new FileService();
        String expectedFileName = "testFile.txt";
        when(file.getOriginalFilename()).thenReturn(expectedFileName);

        // Act
        String actualFileName = fileService.extractFileNameFromFile(file);

        // Assert
        assertEquals(expectedFileName, actualFileName, "The extracted file name should match the expected file name.");
    }

    // Test when the file name is null
    @Test
    void testExtractFileNameFromFile_NullFileName() {
        // Arrange
        FileService fileService = new FileService();
        when(file.getOriginalFilename()).thenReturn(null);

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.extractFileNameFromFile(file);
        });

        assertEquals("File name is empty", exception.getMessage(), "Exception message should indicate that the file name is empty.");
    }

    // Test when the file name is an empty string
    @Test
    void testExtractFileNameFromFile_EmptyFileName() {
        // Arrange
        FileService fileService = new FileService();
        when(file.getOriginalFilename()).thenReturn("");

        // Act & Assert
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.extractFileNameFromFile(file);
        });

        assertEquals("File name is empty", exception.getMessage(), "Exception message should indicate that the file name is empty.");
    }

    @Test
    void testRemoveFileEntitiesByIds_SuccessfulRemoval() {
        // Setup: Create a FileService instance and set the bucketName
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");

        // Setup: Create file entities and ids
        FileEntity file1 = new FileEntity("file1", "dir1");
        FileEntity file2 = new FileEntity("file2", "dir2");
        file1.setId(UUID.randomUUID());
        file2.setId(UUID.randomUUID());

        Set<FileEntity> filesEntities = new HashSet<>(Arrays.asList(file1, file2));
        Set<UUID> ids = new HashSet<>(Collections.singletonList(file1.getId()));

        // Act: Call the method under test
        Set<FileEntity> removedFiles = fileService.removeFileEntitiesByIds(filesEntities, ids);

        // Assert: Verify the results
        assertEquals(1, removedFiles.size(), "One file should be removed");
        assertTrue(removedFiles.contains(file1), "file1 should be in the removed set");
        assertFalse(filesEntities.contains(file1), "file1 should be removed from the original set");
    }

    @Test
    void testRemoveFileEntitiesByIds_NoRemoval() {
        // Setup: Create a FileService instance and set the bucketName
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");

        // Setup: Create file entities and ids
        FileEntity file1 = new FileEntity("file1", "dir1");
        FileEntity file2 = new FileEntity("file2", "dir2");
        file1.setId(UUID.randomUUID());
        file2.setId(UUID.randomUUID());

        Set<FileEntity> filesEntities = new HashSet<>(Arrays.asList(file1, file2));
        Set<UUID> ids = new HashSet<>(Collections.singletonList(UUID.randomUUID())); // No matching ID

        // Act: Call the method under test
        Set<FileEntity> removedFiles = fileService.removeFileEntitiesByIds(filesEntities, ids);

        // Assert: Verify the results
        assertTrue(removedFiles.isEmpty(), "No files should be removed");
        assertEquals(2, filesEntities.size(), "Original set should remain unchanged");
    }

    @Test
    void testRemoveFileEntitiesByIds_ThrowsException() {
        // Setup: Create a FileService instance and set the bucketName
        FileService fileService = new FileService();
        ReflectionTestUtils.setField(fileService, "bucketName", "test-bucket");

        // Setup: Create file entities and ids
        FileEntity file1 = new FileEntity("file1", "dir1");
        file1.setId(UUID.randomUUID());

        Set<FileEntity> filesEntities = new HashSet<>(Collections.singletonList(file1));
        Set<UUID> ids = new HashSet<>(Arrays.asList(file1.getId(), UUID.randomUUID())); // More IDs than files

        // Act & Assert: Call the method under test and expect an exception
        ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class, () -> {
            fileService.removeFileEntitiesByIds(filesEntities, ids);
        });

        // Assert: Verify the exception details
        assertEquals(ErrorMessageUtils.PRODUCT_IMAGES_DELETION_NOT_ALLOWED_ERROR_MESSAGE, exception.getMessage());
    }

    // Test case for when the file entity is found in the collection
    @Test
    public void testGetFileEntityByFile_FileEntityFound() {
        // Arrange
        FileService fileService = new FileService();
        MockMultipartFile mockFile = new MockMultipartFile("file", "testFile.txt", "text/plain", "Test content".getBytes());
        FileEntity fileEntity = new FileEntity("testFile.txt", "/test/directory");
        Collection<FileEntity> fileEntities = Collections.singletonList(fileEntity);

        // Act
        Optional<FileEntity> result = fileService.getFileEntityByFile(fileEntities, mockFile);

        // Assert
        assertTrue(result.isPresent(), "FileEntity should be found");
        assertEquals(fileEntity, result.get(), "The found FileEntity should match the expected one");
    }

    // Test case for when the file entity is not found in the collection
    @Test
    public void testGetFileEntityByFile_FileEntityNotFound() {
        // Arrange
        FileService fileService = new FileService();
        MockMultipartFile mockFile = new MockMultipartFile("file", "nonExistentFile.txt", "text/plain", "Test content".getBytes());
        FileEntity fileEntity = new FileEntity("testFile.txt", "/test/directory");
        Collection<FileEntity> fileEntities = Collections.singletonList(fileEntity);

        // Act
        Optional<FileEntity> result = fileService.getFileEntityByFile(fileEntities, mockFile);

        // Assert
        assertFalse(result.isPresent(), "FileEntity should not be found");
    }

    // Test case for when the file has an empty name
    @Test
    public void testGetFileEntityByFile_FileNameEmpty() {
        // Arrange
        FileService fileService = new FileService();
        MockMultipartFile mockFile = new MockMultipartFile("file", "", "text/plain", "Test content".getBytes());
        FileEntity fileEntity = new FileEntity("testFile.txt", "/test/directory");
        Collection<FileEntity> fileEntities = Collections.singletonList(fileEntity);

        // Act & Assert
        assertThrows(ActionNotAllowedException.class, () -> fileService.getFileEntityByFile(fileEntities, mockFile), "Should throw ActionNotAllowedException for empty file name");
    }

    // Test case for when the file has a null name
    @Test
    public void testGetFileEntityByFile_FileNameNull() {
        // Arrange
        FileService fileService = new FileService();
        MockMultipartFile mockFile = new MockMultipartFile("file", null, "text/plain", "Test content".getBytes());
        FileEntity fileEntity = new FileEntity("testFile.txt", "/test/directory");
        Collection<FileEntity> fileEntities = Collections.singletonList(fileEntity);

        // Act & Assert
        assertThrows(ActionNotAllowedException.class, () -> fileService.getFileEntityByFile(fileEntities, mockFile), "Should throw ActionNotAllowedException for null file name");
    }

    // Test case: Verify that the method correctly groups files by directory
    @Test
    void testGetDirectoriesWithFilesNames_GroupByDirectory() {
        // Setup
        FileService fileService = new FileService();
        List<FileEntity> fileEntities = Arrays.asList(
                new FileEntity("file1", "dir1"),
                new FileEntity("file2", "dir1"),
                new FileEntity("file3", "dir2")
        );

        // Execution
        Map<String, List<String>> result = fileService.getDirectoriesWithFilesNames(fileEntities);

        // Assertion
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("dir1", Arrays.asList("file1", "file2"));
        expected.put("dir2", Collections.singletonList("file3"));
        assertEquals(expected, result, "Files should be grouped by their directories correctly.");
    }

    // Test case: Verify that an empty collection returns an empty map
    @Test
    void testGetDirectoriesWithFilesNames_EmptyCollection() {
        // Setup
        FileService fileService = new FileService();
        Collection<FileEntity> fileEntities = Collections.emptyList();

        // Execution
        Map<String, List<String>> result = fileService.getDirectoriesWithFilesNames(fileEntities);

        // Assertion
        assertEquals(Collections.emptyMap(), result, "An empty collection should return an empty map.");
    }

    // Test case: Verify that files with the same name in different directories are handled correctly
    @Test
    void testGetDirectoriesWithFilesNames_SameFileNameDifferentDirectories() {
        // Setup
        FileService fileService = new FileService();
        List<FileEntity> fileEntities = Arrays.asList(
                new FileEntity("file1", "dir1"),
                new FileEntity("file2", "dir1")
        );

        // Execution
        Map<String, List<String>> result = fileService.getDirectoriesWithFilesNames(fileEntities);

        // Assertion
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("dir1", List.of("file1", "file2"));
        assertEquals(expected, result, "Files with the same name in different directories should be handled correctly.");
    }
}