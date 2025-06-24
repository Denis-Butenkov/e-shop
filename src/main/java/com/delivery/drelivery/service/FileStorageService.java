package com.delivery.drelivery.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file);

    boolean deleteFile(String key);
}
