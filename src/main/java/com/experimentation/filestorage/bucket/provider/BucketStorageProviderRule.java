package com.experimentation.filestorage.bucket.provider;

import com.experimentation.filestorage.bucket.BucketStorageType;

/**
 * BucketStorageProviderRule is an interface that defines the rule for choosing a provider for file storage.
 * The chooseProvider
 */
public interface BucketStorageProviderRule {

    BucketStorageType chooseProvider();

    BucketStorageType chooseProvider(String provider);
}
