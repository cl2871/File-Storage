package com.experimentation.filestorage.bucket;

public interface BucketStorageFactory {
    BucketStorage getBucketStorageService(BucketStorageType bucketStorageType);
}
