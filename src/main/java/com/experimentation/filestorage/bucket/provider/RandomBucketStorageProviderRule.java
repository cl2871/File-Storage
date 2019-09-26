package com.experimentation.filestorage.bucket.provider;

import com.experimentation.filestorage.bucket.BucketStorageType;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBucketStorageProviderRule extends BaseBucketStorageProviderRule {

    /**
     * RandomBucketStorageProviderRule will return a random provider from the list of BucketStorageTypes.
     * @return BucketStorageType instance
     */
    @Override
    public BucketStorageType chooseProvider() {
        BucketStorageType[] bucketStorageTypes = BucketStorageType.class.getEnumConstants();
        int randomNum = getRandomNum(bucketStorageTypes.length);
        return bucketStorageTypes[randomNum];
    }

    protected int getRandomNum(int upperBound) {
        return ThreadLocalRandom.current().nextInt(0, upperBound);
    }
}
