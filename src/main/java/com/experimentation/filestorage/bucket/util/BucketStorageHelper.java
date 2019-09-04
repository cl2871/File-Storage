package com.experimentation.filestorage.bucket.util;

import com.experimentation.filestorage.bucket.BucketStorageDTO;
import org.springframework.stereotype.Component;

@Component
public class BucketStorageHelper {

    public BucketStorageDTO createBucketStorageDTO(String bucketName, String contentType, byte[] content) {
        return new BucketStorageDTO(bucketName, contentType, content);
    }
}
