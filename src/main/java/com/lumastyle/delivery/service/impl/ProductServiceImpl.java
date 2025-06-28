package com.lumastyle.delivery.service.impl;

import com.lumastyle.delivery.dto.product.ProductRequest;
import com.lumastyle.delivery.dto.product.ProductResponse;
import com.lumastyle.delivery.entity.ProductEntity;
import com.lumastyle.delivery.exception.FileStorageException;
import com.lumastyle.delivery.exception.ResourceNotFoundException;
import com.lumastyle.delivery.mapper.ProductMapper;
import com.lumastyle.delivery.repository.ProductRepository;
import com.lumastyle.delivery.service.FileStorageService;
import com.lumastyle.delivery.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final FileStorageService fileStorage;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;


    @Override
    public ProductResponse addProduct(ProductRequest request, MultipartFile file) {
        log.info("Adding product: {}", request);
        String imageUrl;
        try {
            imageUrl = fileStorage.uploadFile(file);
        } catch (RuntimeException e) {
            throw new FileStorageException("File upload failed", e);
        }
        ProductEntity entity = productMapper.toEntity(request);
        entity.setImageUrl(imageUrl);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Override
    public List<ProductResponse> readProducts() {
        log.info("Reading all foods");
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

    @Override
    public ProductResponse readProduct(String id) {
        log.info("Reading product with id: {}", id);
        ProductEntity existingProductEntity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with the id: " + id));
        log.info("Food found successfully");
        return productMapper.toResponse(existingProductEntity);
    }

    @Override
    public void deleteProduct(String id) {
        ProductEntity entity = findFoodById(id);

        String imageUrl = entity.getImageUrl();
        String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        boolean isFileDeleted = fileStorage.deleteFile(key);
        validate(isFileDeleted, key);

        productRepository.deleteById(id);
        log.info("Food with id: {} deleted successfully (file key: {})", id, key);
    }


    // === Helper method ===

    private static void validate(boolean isFileDeleted, String key) {
        if (!isFileDeleted) {
            log.error("Failed to delete file with key {}", key);
            throw new FileStorageException("Could not delete file with key " + key);
        }
    }

    private ProductEntity findFoodById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id " + id));
    }

}