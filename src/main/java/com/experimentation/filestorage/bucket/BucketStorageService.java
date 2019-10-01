package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.metadata.BucketStorageMetadata;
import com.experimentation.filestorage.bucket.metadata.BucketStorageMetadataService;
import com.experimentation.filestorage.bucket.provider.BucketStorageProviderRule;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class BucketStorageService {

    Logger logger = LoggerFactory.getLogger(BucketStorageService.class);

    private final BucketStorageFactory bucketStorageFactory;
    private final BucketStorageProviderRule bucketStorageProviderRule;
    private final BucketStorageMetadataService bucketStorageMetadataService;

    @Autowired
    public BucketStorageService(BucketStorageFactory bucketStorageFactory,
                                BucketStorageProviderRule bucketStorageProviderRule,
                                BucketStorageMetadataService bucketStorageMetadataService) {
        this.bucketStorageFactory = bucketStorageFactory;
        this.bucketStorageProviderRule = bucketStorageProviderRule;
        this.bucketStorageMetadataService = bucketStorageMetadataService;
    }

    public BucketStorageDTO doGetFile(UUID uuid) throws BucketStorageServiceException {

        // Get the storage metadata related to the file and corresponding storage service
        BucketStorageMetadata bucketStorageMetadata = bucketStorageMetadataService.getBucketStorageMetadata(uuid);
        BucketStorageType bucketStorageType = BucketStorageType.valueOf(bucketStorageMetadata.getStorageProvider());
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);

        // Return the file
        logger.info("BucketStorageService.doGetFile " + bucketStorage);
        return bucketStorage.getFile(bucketStorageMetadata.getBucketName(), bucketStorageMetadata.getKeyName());
    }

    public void doUploadMultipartFile(String bucketName, String fileName, MultipartFile multipartFile)
            throws BucketStorageServiceException {

        // Choose a provider for handling upload
        BucketStorageType bucketStorageType = bucketStorageProviderRule.chooseProvider();
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);

        // Upload the file
        logger.info("BucketStorageService.doUploadMultipartFile " + bucketStorage);
        bucketStorage.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Save the storage metadata
        BucketStorageMetadata bucketStorageMetadata = bucketStorageMetadataService
                .createBucketStorageMetadata(bucketStorageType, bucketName, fileName);
        bucketStorageMetadataService.saveBucketStorageMetadata(bucketStorageMetadata);
    }

    public void doDeleteFile(UUID uuid) throws BucketStorageServiceException {

        // Get the storage metadata related to the file and the corresponding service
        BucketStorageMetadata bucketStorageMetadata = bucketStorageMetadataService.getBucketStorageMetadata(uuid);
        BucketStorageType bucketStorageType = BucketStorageType.valueOf(bucketStorageMetadata.getStorageProvider());
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);

        // Delete the file from the service and delete its metadata
        logger.info("BucketStorageService.doDeleteFile " + bucketStorage);
        bucketStorage.deleteFile(bucketStorageMetadata.getBucketName(), bucketStorageMetadata.getKeyName());
        bucketStorageMetadataService.deleteBucketStorageMetadata(uuid);
    }
}
