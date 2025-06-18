package com.delivery.drelivery.service;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import com.delivery.drelivery.entity.FoodEntity;
import com.delivery.drelivery.mapper.FoodMapper;
import com.delivery.drelivery.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final S3Client s3Client;
    private final FoodMapper foodMapper;
    private final FoodRepository foodRepository;

    @Value("${aws.3s.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        log.info("Uploading file: {}", file.getOriginalFilename());
        String filenameExtension = getFilenameExtension(file);
        String key = getKey(filenameExtension);
        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return validation(response, key);
        } catch (IOException ex){
            log.error("An error occurred while uploading the file: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the file");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        log.info("Adding food: {}", request);
        FoodEntity newFoodEntity = foodMapper.toEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        newFoodEntity = foodRepository.save(newFoodEntity);
        log.info("Food added successfully");
        return foodMapper.toResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> readFoods() {
        log.info("Reading all foods");
        return foodRepository.findAll().stream().map(foodMapper::toResponse).toList();
    }

    @Override
    public FoodResponse readFood(String id) {
        log.info("Reading food with id: {}", id);
        FoodEntity existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found with the id: " + id));
        log.info("Food found successfully");
        return foodMapper.toResponse(existingFood);
    }

    @Override
    public boolean deleteFile(String filename) {
        log.info("Deleting file with name: {}", filename);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        log.info("File deleted successfully");
        return true;
    }

    @Override
    public void deleteFood(String id) {
        FoodResponse response = readFood(id);
        String imageUrl = response.getImageUrl();
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        boolean isFileDeleted = deleteFile(filename);
        if (isFileDeleted){
            foodRepository.deleteById(response.getId());
            log.info("Food deleted successfully");
        } else {
            log.error("The file was not deleted successfully");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the file");
        }
    }

    // === Helper methods

    private static String getFilenameExtension(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    private static String getKey(String filenameExtension) {
        return UUID.randomUUID() +"."+ filenameExtension;
    }

    private String validation(PutObjectResponse response, String key) {
        if (response.sdkHttpResponse().isSuccessful()){
            log.info("File uploaded successfully");
            return getUrl(key);
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
        }
    }

    private String getUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}
