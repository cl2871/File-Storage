package com.experimentation.filestorage.bucket;

public enum BucketStorageType {

    AWS_S3(BucketStorageTypeConstants.AWS_S3_STORAGE),
    GCP(BucketStorageTypeConstants.GCP_STORAGE);

    private final String parserName;

    BucketStorageType(String parserName) {
        this.parserName = parserName;
    }

    @Override
    public String toString() {
        return this.parserName;
    }
}
