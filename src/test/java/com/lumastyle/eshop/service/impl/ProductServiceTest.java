package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.entity.ProductEntity;
import com.lumastyle.eshop.exception.FileStorageException;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.mapper.ProductMapper;
import com.lumastyle.eshop.repository.ProductRepository;
import com.lumastyle.eshop.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProductServiceImpl}, covering add, read, and delete operations.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private FileStorageService fileStorage;

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    private ProductRequest request;
    private MultipartFile file;
    private ProductEntity entity;
    private ProductEntity savedEntity;
    private ProductResponse response;

    /**
     * Initializes common test data before each test.
     */
    @BeforeEach
    void setUp() {
        request = new ProductRequest("Name", "Desc", new BigDecimal("199.99"), "Tie");
        file = mock(MultipartFile.class);
        entity = new ProductEntity();
        savedEntity = new ProductEntity();
        savedEntity.setId("id123");
        savedEntity.setName(request.getName());
        savedEntity.setDescription(request.getDescription());
        savedEntity.setPrice(request.getPrice());
        savedEntity.setCategory(request.getCategory());
        savedEntity.setImageUrl("http://img/url.jpg");
        response = new ProductResponse();
    }

    /**
     * Test the successful addition of a product.
     */
    @Test
    @DisplayName("addProduct returns response on success")
    @Tag("Unit")
    void addProduct_success() {
        when(fileStorage.uploadFile(file)).thenReturn("http://img/url.jpg");
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toResponse(savedEntity)).thenReturn(response);

        ProductResponse result = service.addProduct(request, file);

        assertSame(response, result);
        verify(fileStorage).uploadFile(file);
        verify(repository).save(entity);
        verify(mapper).toResponse(savedEntity);
    }

    /**
     * Test that a file upload failure throws FileStorageException.
     */
    @Test
    @DisplayName("addProduct throws FileStorageException on upload failure")
    @Tag("Unit")
    void addProduct_fileUploadFails_throws() {
        when(fileStorage.uploadFile(file)).thenThrow(new RuntimeException("fail"));

        assertThrows(FileStorageException.class, () -> service.addProduct(request, file));
        verify(fileStorage).uploadFile(file);
        verifyNoInteractions(repository, mapper);
    }

    /**
     * Test reading all products returns a mapped list.
     */
    @Test
    @DisplayName("readProducts returns list of ProductResponse")
    @Tag("Unit")
    void readProducts_returnsList() {
        List<ProductEntity> entities = List.of(entity);
        ProductResponse resp = new ProductResponse();
        when(repository.findAll()).thenReturn(entities);
        when(mapper.toResponse(entity)).thenReturn(resp);

        List<ProductResponse> list = service.readProducts();

        assertEquals(1, list.size());
        assertSame(resp, list.getFirst());
    }

    /**
     * Test reading an existing product by id.
     */
    @Test
    @DisplayName("readProduct returns ProductResponse when entity exists")
    @Tag("Unit")
    void readProduct_exists() {
        when(repository.findById("id123")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ProductResponse resp = service.readProduct("id123");
        assertSame(response, resp);
    }

    /**
     * Test reading a non-existent product throws ResourceNotFoundException.
     */
    @Test
    @DisplayName("readProduct throws ResourceNotFoundException when not found")
    @Tag("Unit")
    void readProduct_notFound_throws() {
        when(repository.findById("nope")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.readProduct("nope"));
    }

    /**
     * Test successful deletion of a product and its file.
     */
    @Test
    @DisplayName("deleteProduct deletes file and entity on success")
    @Tag("Unit")
    void deleteProduct_success() {
        entity.setImageUrl("https://host/path/file.png");
        when(repository.findById("id123")).thenReturn(Optional.of(entity));
        when(fileStorage.deleteFile("file.png")).thenReturn(true);

        service.deleteProduct("id123");

        verify(fileStorage).deleteFile("file.png");
        verify(repository).deleteById("id123");
    }

    /**
     * Test deletion failure when file deletion fails.
     */
    @Test
    @DisplayName("deleteProduct throws FileStorageException on file delete failure")
    @Tag("Unit")
    void deleteProduct_fileDeletionFails_throws() {
        entity.setImageUrl("https://host/path/img.jpg");
        when(repository.findById("id123")).thenReturn(Optional.of(entity));
        when(fileStorage.deleteFile("img.jpg")).thenReturn(false);

        assertThrows(FileStorageException.class, () -> service.deleteProduct("id123"));
        verify(fileStorage).deleteFile("img.jpg");
        verify(repository, never()).deleteById(any());
    }

    /**
     * Test deletion of a non-existent product throws ResourceNotFoundException.
     */
    @Test
    @DisplayName("deleteProduct throws ResourceNotFoundException when not found")
    @Tag("Unit")
    void deleteProduct_notFound_throws() {
        when(repository.findById("bad")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct("bad"));
        verify(repository).findById("bad");
        verifyNoInteractions(fileStorage);
        verify(repository, never()).deleteById(any());
    }
}
