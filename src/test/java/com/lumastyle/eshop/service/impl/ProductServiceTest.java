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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProductServiceImpl}.
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
     * Test file upload failure throws FileStorageException.
     */
    @Test
    void addProduct_fileUploadFails_throws() {
        when(fileStorage.uploadFile(file)).thenThrow(new RuntimeException("fail"));

        assertThrows(FileStorageException.class, () -> service.addProduct(request, file));
        verify(fileStorage).uploadFile(file);
        verifyNoMoreInteractions(repository, mapper);
    }

    /**
     * Test reading all products returns a mapped list.
     */
    @Test
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
     * Test reading existing product by id.
     */
    @Test
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
    void readProduct_notFound_throws() {
        when(repository.findById("nope")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.readProduct("nope"));
    }

    /**
     * Test successful deletion of a product and its file.
     */
    @Test
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
    void deleteProduct_notFound_throws() {
        when(repository.findById("bad")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct("bad"));
        verify(repository).findById("bad");
        verifyNoInteractions(fileStorage);
        verify(repository, never()).deleteById(any());
    }
}

