package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link S3FileStorageServiceImpl}.
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

    @BeforeEach
    void setUp() throws Exception {
        // Initialize a mock multipart file
        file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "content".getBytes()
        );
        // Inject bucketName via reflection
        Field bucketField = S3FileStorageServiceImpl.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(service, bucketName);
    }

    @Test
    void uploadFile_success() {
        // Mock successful S3 response
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

    @Test
    void uploadFile_httpFailure_throwsFileStorageException() {
        // Mock failed HTTP status
        PutObjectResponse response = mock(PutObjectResponse.class);
        SdkHttpResponse httpResponse = mock(SdkHttpResponse.class);
        when(httpResponse.isSuccessful()).thenReturn(false);
        when(response.sdkHttpResponse()).thenReturn(httpResponse);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(response);

        assertThrows(FileStorageException.class, () -> service.uploadFile(file));
    }

    @Test
    void uploadFile_ioException_throwsFileStorageException() throws IOException {
        // Simulate IO exception when reading a file
        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.getOriginalFilename()).thenReturn("test.txt");
        when(badFile.getBytes()).thenThrow(new IOException("fail"));

        assertThrows(FileStorageException.class, () -> service.uploadFile(badFile));
    }

    @Test
    void deleteFile_success() {
        // deleteObject returns void; no exception => success
        boolean result = service.deleteFile("some-key");
        assertTrue(result);
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFile_failure_throwsFileStorageException() {
        // Simulate S3 exception on delete
        doThrow(S3Exception.builder().message("fail").build())
                .when(s3Client)
                .deleteObject(any(DeleteObjectRequest.class));

        assertThrows(FileStorageException.class, () -> service.deleteFile("some-key"));
    }
}
