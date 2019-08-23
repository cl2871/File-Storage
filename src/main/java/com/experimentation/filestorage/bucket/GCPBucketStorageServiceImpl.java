package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GCPBucketStorageServiceImpl implements BucketStorageService {

    private Storage storage;
    private GCPBucketStorageUtil gcpBucketStorageUtil;
    private FileStorageUtil fileStorageUtil;

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageServiceImpl.class);

    @Autowired
    public GCPBucketStorageServiceImpl(Storage storage,
                                       GCPBucketStorageUtil gcpBucketStorageUtil,
                                       FileStorageUtil fileStorageUtil) {
        this.storage = storage;
        this.gcpBucketStorageUtil = gcpBucketStorageUtil;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file)
            throws FileStorageServiceException {
        BlobId blobId = gcpBucketStorageUtil.createBlobId(bucketName, fileName);
        BlobInfo blobInfo = gcpBucketStorageUtil.createBlobInfo(blobId, file.getContentType());

        try {
            storage.create(blobInfo, file.getBytes());
        } catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Issue reading file to upload");
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws FileStorageServiceException {
        logger.info("Bucket Name: " + bucketName + " File Name:" + fileName);
        BlobId blobId = gcpBucketStorageUtil.createBlobId(bucketName, fileName);
        boolean deleted = storage.delete(blobId);
        if (!deleted) {
            throw new FileStorageServiceException("Unable to delete file: " + fileName);
        }
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) {
        logger.info("Bucket Name: " + bucketName + " File Name:" + fileName);
        Blob blob = storage.get(gcpBucketStorageUtil.createBlobId(bucketName, fileName));
        return fileStorageUtil.createFileStorageDTO(fileName, blob.getContentType(), blob.getContent());
    }
}
