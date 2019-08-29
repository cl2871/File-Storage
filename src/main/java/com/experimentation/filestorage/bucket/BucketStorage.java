package com.experimentation.filestorage.bucket;

import org.springframework.web.multipart.MultipartFile;

public interface BucketStorage {

    FileStorageDTO getFile(String bucketName, String fileName) throws Exception;

    void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws Exception;

    void deleteFile(String bucketName, String fileName) throws Exception;
}
