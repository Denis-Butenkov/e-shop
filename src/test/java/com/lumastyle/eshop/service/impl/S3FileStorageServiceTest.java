package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link S3FileStorageServiceImpl}, covering file upload and deletion scenarios.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class S3FileStorageServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3FileStorageServiceImpl service;

    private MockMultipartFile file;
    private final String bucketName = "test-bucket";

    /**
     * Sets up a mock multipart file and injects the bucket name via reflection before each test.
     *
     * @throws Exception if reflection fails when setting the bucketName field
     */
    @BeforeEach
    void setUp() throws Exception {
        file = new MockMultipartFile(
                "file",
                "test.png",
                "text/plain",
                "content".getBytes()
        );
        Field bucketField = S3FileStorageServiceImpl.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(service, bucketName);
    }

    /**
     * Verifies that a successful S3 putObject response returns a valid URL.
     */
    @Test
    @DisplayName("uploadFile returns URL when upload succeeds")
    @Tag("Unit")
    void uploadFile_success() {
        PutObjectResponse response = mock(PutObjectResponse.class);
        SdkHttpResponse httpResponse = mock(SdkHttpResponse.class);
        when(httpResponse.isSuccessful()).thenReturn(true);
        when(response.sdkHttpResponse()).thenReturn(httpResponse);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(response);

        String result = service.uploadFile(file);

        assertTrue(result.startsWith("https://" + bucketName + ".s3.amazonaws.com/"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    /**
     * Ensures that uploadFile throws FileStorageException when HTTP status indicates failure.
     */
    @Test
    @DisplayName("uploadFile throws when HTTP response is not successful")
    @Tag("Unit")
    void uploadFile_httpFailure_throwsFileStorageException() {
        PutObjectResponse response = mock(PutObjectResponse.class);
        SdkHttpResponse httpResponse = mock(SdkHttpResponse.class);
        when(httpResponse.isSuccessful()).thenReturn(false);
        when(response.sdkHttpResponse()).thenReturn(httpResponse);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(response);

        assertThrows(FileStorageException.class, () -> service.uploadFile(file));
    }

    /**
     * Ensures that uploadFile throws FileStorageException when reading the file bytes fails.
     */
    @Test
    @DisplayName("uploadFile throws when file IO fails")
    @Tag("Unit")
    void uploadFile_ioException_throwsFileStorageException() throws IOException {
        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.getOriginalFilename()).thenReturn("test.png");
        when(badFile.getBytes()).thenThrow(new IOException("fail"));

        assertThrows(FileStorageException.class, () -> service.uploadFile(badFile));
    }

    /**
     * Verifies that deleteFile returns true when deletion succeeds without exception.
     */
    @Test
    @DisplayName("deleteFile returns true on successful delete")
    @Tag("Unit")
    void deleteFile_success() {
        boolean result = service.deleteFile("some-key");

        assertTrue(result);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    /**
     * Ensures deleteFile throws FileStorageException when S3 client deletion fails.
     */
    @Test
    @DisplayName("deleteFile throws when S3 deletion fails")
    @Tag("Unit")
    void deleteFile_failure_throwsFileStorageException() {
        doThrow(S3Exception.builder().message("fail").build())
                .when(s3Client)
                .deleteObject(any(DeleteObjectRequest.class));

        assertThrows(FileStorageException.class, () -> service.deleteFile("some-key"));
    }
}
