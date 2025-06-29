package com.lumastyle.eshop.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for storing files (e.g., images) in cloud or local storage.
 */
public interface FileStorageService {

    /**
     * Upload the given multipart file and return its public URL or key.
     *
     * @param file the file to upload
     * @return the generated storage key or URL
     */
    String uploadFile(MultipartFile file);

    /**
     * Delete the file identified by the given key.
     *
     * @param key the storage key or URL path to delete
     * @return true if deletion succeeded, false otherwise
     */
    boolean deleteFile(String key);
}
