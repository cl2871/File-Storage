package com.experimentation.filestorage.bucket.util;

public class BucketStorageServiceException extends RuntimeException {

    public BucketStorageServiceException() {
        super();
    }

    public BucketStorageServiceException(String message) {
        super(message);
    }

    public BucketStorageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
