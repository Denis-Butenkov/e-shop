package com.lumastyle.delivery.service.impl;

import com.lumastyle.delivery.dto.FoodRequest;
import com.lumastyle.delivery.dto.FoodResponse;
import com.lumastyle.delivery.entity.FoodEntity;
import com.lumastyle.delivery.exception.FileStorageException;
import com.lumastyle.delivery.exception.ResourceNotFoundException;
import com.lumastyle.delivery.mapper.FoodMapper;
import com.lumastyle.delivery.repository.FoodRepository;
import com.lumastyle.delivery.service.FileStorageService;
import com.lumastyle.delivery.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FileStorageService fileStorage;
    private final FoodMapper foodMapper;
    private final FoodRepository foodRepository;


    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        log.info("Adding food: {}", request);
        String imageUrl;
        try {
            imageUrl = fileStorage.uploadFile(file);
        } catch (RuntimeException e) {
            throw new FileStorageException("File upload failed", e);
        }
        FoodEntity entity = foodMapper.toEntity(request);
        entity.setImageUrl(imageUrl);
        FoodEntity saved = foodRepository.save(entity);
        return foodMapper.toResponse(saved);
    }

    @Override
    public List<FoodResponse> readFoods() {
        log.info("Reading all foods");
        return foodRepository.findAll().stream().map(foodMapper::toResponse).toList();
    }

    @Override
    public FoodResponse readFood(String id) {
        log.info("Reading food with id: {}", id);
        FoodEntity existingFoodEntity = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found with the id: " + id));
        log.info("Food found successfully");
        return foodMapper.toResponse(existingFoodEntity);
    }

    @Override
    public void deleteFood(String id) {
        FoodEntity entity = findFoodById(id);

        String imageUrl = entity.getImageUrl();
        String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        boolean isFileDeleted = fileStorage.deleteFile(key);
        validate(isFileDeleted, key);

        foodRepository.deleteById(id);
        log.info("Food with id: {} deleted successfully (file key: {})", id, key);
    }


    // === Helper method ===

    private static void validate(boolean isFileDeleted, String key) {
        if (!isFileDeleted) {
            log.error("Failed to delete file with key {}", key);
            throw new FileStorageException("Could not delete file with key " + key);
        }
    }

    private FoodEntity findFoodById(String id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id " + id));
    }

}