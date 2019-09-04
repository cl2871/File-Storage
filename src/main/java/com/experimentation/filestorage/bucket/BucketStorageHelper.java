package com.experimentation.filestorage.bucket;

import org.springframework.stereotype.Component;

@Component
public class BucketStorageHelper {

    public BucketStorageDTO createBucketStorageDTO(String bucketName, String contentType, byte[] content) {
        return new BucketStorageDTO(bucketName, contentType, content);
    }
}
