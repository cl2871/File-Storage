package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GCPBucketStorageServiceImpl implements BucketStorageService {

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageServiceImpl.class);

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file)
            throws FileStorageServiceException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        try {
            getStorage().create(blobInfo, file.getBytes());
        } catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Issue reading file");
        }
    }

    @Override
    public void uploadFile(String bucketName, String fileName, File file, String contentType)
            throws FileStorageServiceException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

        try {
            getStorage().create(blobInfo, Files.readAllBytes(file.toPath()));
        } catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Issue reading file");
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws FileStorageServiceException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = getStorage().delete(blobId);
        if (!deleted) {
            throw new FileStorageServiceException("Unable to delete file: " + fileName);
        }
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) {
        Blob blob = getStorage().get(BlobId.of(bucketName, fileName));
        return new FileStorageDTO(fileName, blob.getContentType(), blob.getContent());
    }

    private Storage getStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
