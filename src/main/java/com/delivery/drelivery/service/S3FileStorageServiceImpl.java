package com.delivery.drelivery.service;

import com.delivery.drelivery.exception.BadRequestException;
import com.delivery.drelivery.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.3s.bucket.name}")
    private String bucketName;

    private static void validation(PutObjectResponse response, String key) {
        if (!response.sdkHttpResponse().isSuccessful()) {
            log.error("S3 upload failed: {}",
                    response.sdkHttpResponse().statusText().orElse("<no message>"));
            throw new FileStorageException(
                    "File upload to S3 was not successful for key: " + key);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        validatePath(file);
        String key;
        try {
            log.info("Uploading file: {}", file.getOriginalFilename());
            key = getKey(file);

            PutObjectRequest request = buildS3PutObjectRequest(file, key);

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            validation(response, key);
        } catch (IOException e) {
            throw new FileStorageException("Upload failed", e);
        }
        log.info("File uploaded successfully to S3 with key: {}", key);
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    @Override
    public boolean deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        log.info("Deleting file with key: {}", key);
        try {
            s3Client.deleteObject(request);
            return true;
        } catch (SdkException e) {
            throw new FileStorageException("Deletion failed", e);
        }
    }


    // === Helper methods ===

    private static String getKey(MultipartFile file) {
        String original = Objects.requireNonNull(file.getOriginalFilename());
        String extension = original.contains(".")
                ? original.substring(original.lastIndexOf('.'))
                : "";
        return UUID.randomUUID() + extension;
    }

    private void validatePath(MultipartFile file) {
        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (original.contains("..")) {
            throw new BadRequestException(
                    "Filename contains invalid path sequence: " + original);
        }
    }

    private PutObjectRequest buildS3PutObjectRequest(MultipartFile file, String key) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(file.getContentType())
                .build();
    }
}
