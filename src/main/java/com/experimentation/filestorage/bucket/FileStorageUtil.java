package com.experimentation.filestorage.bucket;

public class FileStorageUtil {

    public FileStorageDTO createFileStorageDTO(String bucketName, String contentType, byte[] content) {
        return new FileStorageDTO(bucketName, contentType, content);
    }
}
