package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.BucketStorageType;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;

import java.util.UUID;

public interface BucketStorageMetadataService {

    public Iterable<BucketStorageMetadata> getAllBucketStorageMetadata();

    public BucketStorageMetadata getBucketStorageMetadata(UUID uuid) throws BucketStorageServiceException;

    public BucketStorageMetadata saveBucketStorageMetadata(BucketStorageMetadata bucketStorageMetadata);

    public BucketStorageMetadata updateBucketStorageMetadata(UUID uuid, BucketStorageMetadata bucketStorageMetadata);

    public void deleteBucketStorageMetadata(UUID uuid);

    public BucketStorageMetadata createBucketStorageMetadata(BucketStorageType bucketStorageType,
                                                             String bucketName,
                                                             String fileName);
}
