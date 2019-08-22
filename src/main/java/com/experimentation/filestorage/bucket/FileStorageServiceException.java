package com.experimentation.filestorage.bucket;

public class FileStorageServiceException extends RuntimeException {

    public FileStorageServiceException() {
        super();
    }

    public FileStorageServiceException(String message) {
        super(message);
    }

    public FileStorageServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
