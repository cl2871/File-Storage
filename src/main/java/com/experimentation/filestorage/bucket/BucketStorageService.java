package com.experimentation.filestorage.bucket;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface BucketStorageService {

    public FileStorageDTO getFile(String bucketName, String fileName) throws Exception;

    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws Exception;

    public void uploadFile(String bucketName, String fileName, File file, String contentType) throws Exception;

    public void deleteFile(String bucketName, String fileName) throws Exception;
}
