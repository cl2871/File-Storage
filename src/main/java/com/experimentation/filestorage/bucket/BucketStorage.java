package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.springframework.web.multipart.MultipartFile;

public interface BucketStorage {

    BucketStorageDTO getFile(String bucketName, String fileName) throws BucketStorageServiceException;

    void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws BucketStorageServiceException;

    void deleteFile(String bucketName, String fileName) throws BucketStorageServiceException;
}
