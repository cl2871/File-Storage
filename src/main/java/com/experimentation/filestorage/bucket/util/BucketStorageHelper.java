package com.experimentation.filestorage.bucket.util;

import com.experimentation.filestorage.bucket.BucketStorageDTO;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class BucketStorageHelper {

    public BucketStorageDTO createBucketStorageDTO(String bucketName, String contentType, InputStream inputStream) {
        return new BucketStorageDTO(bucketName, contentType, inputStream);
    }
}
