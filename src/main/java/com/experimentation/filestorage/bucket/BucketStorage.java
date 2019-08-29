package com.experimentation.filestorage.bucket;

import org.springframework.web.multipart.MultipartFile;

public interface BucketStorage {

    FileStorageDTO getFile(String bucketName, String fileName) throws FileStorageServiceException;

    void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws FileStorageServiceException;

    void deleteFile(String bucketName, String fileName) throws FileStorageServiceException;
}
