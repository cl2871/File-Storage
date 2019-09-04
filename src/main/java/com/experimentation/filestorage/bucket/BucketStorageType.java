package com.experimentation.filestorage.bucket;

public enum BucketStorageType {

    AWS_S3(BucketStorageTypeConstants.AWS_S3),
    GCP(BucketStorageTypeConstants.GCP);

    private final String storageName;

    BucketStorageType(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public String toString() {
        return this.storageName;
    }
}
