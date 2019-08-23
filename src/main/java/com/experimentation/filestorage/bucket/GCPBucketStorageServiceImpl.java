package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GCPBucketStorageServiceImpl implements BucketStorageService {

    private Storage storage;

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageServiceImpl.class);

    @Autowired
    public GCPBucketStorageServiceImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file)
            throws FileStorageServiceException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        try {
            storage.create(blobInfo, file.getBytes());
        } catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Issue reading file to upload");
        }
    }

    @Override
    public void uploadFile(String bucketName, String fileName, File file, String contentType)
            throws FileStorageServiceException {
        logger.info("Bucket Name: " + bucketName + " File Name:" + fileName);
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

        try {
            storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        } catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Issue reading file to upload");
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws FileStorageServiceException {
        logger.info("Bucket Name: " + bucketName + " File Name:" + fileName);
        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = storage.delete(blobId);
        if (!deleted) {
            throw new FileStorageServiceException("Unable to delete file: " + fileName);
        }
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) {
        logger.info("Bucket Name: " + bucketName + " File Name:" + fileName);
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return new FileStorageDTO(fileName, blob.getContentType(), blob.getContent());
    }
}
