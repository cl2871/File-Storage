package com.experimentation.filestorage.bucket.metadata;

import java.util.UUID;

public interface BucketStorageMetadataService {

    public Iterable<BucketStorageMetadata> getAllBucketStorageMetadata();

    public BucketStorageMetadata getBucketStorageMetadata(UUID uuid);

    public BucketStorageMetadata saveBucketStorageMetadata(BucketStorageMetadata bucketStorageMetadata);

    public BucketStorageMetadata updateBucketStorageMetadata(UUID uuid, BucketStorageMetadata bucketStorageMetadata);

    public void deleteBucketStorageMetadata(UUID uuid);
}