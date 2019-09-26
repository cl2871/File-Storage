package com.experimentation.filestorage.bucket.provider;

import com.experimentation.filestorage.bucket.BucketStorageType;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseBucketStorageProviderRule implements BucketStorageProviderRule {

    private static final Logger logger = LoggerFactory.getLogger(BaseBucketStorageProviderRule.class);

    /**
     * The chooseProvider(provider) method will attempt to return a BucketStorageType based on the provided string.
     * If a corresponding BucketStorageType can not be chosen, a BucketStorageServiceException will be thrown.
     * @param provider
     * @return BucketStorageType instance
     */
    @Override
    public BucketStorageType chooseProvider(String provider) throws BucketStorageServiceException {
        try {
            return BucketStorageType.valueOf(provider);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException("BucketStorageType cannot be determined based on provider string");
        }
    }
}
