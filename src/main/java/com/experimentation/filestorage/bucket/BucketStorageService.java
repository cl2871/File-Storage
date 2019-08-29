package com.experimentation.filestorage.bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BucketStorageService {

    Logger logger = LoggerFactory.getLogger(BucketStorageService.class);

    @Autowired
    private BucketStorageFactory bucketStorageFactory;

    public FileStorageDTO doGetFile(String bucketName, String fileName, BucketStorageType bucketStorageType)
            throws FileStorageServiceException {
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);
        logger.info("BucketStorageService.doGetFile " + bucketStorage);
        return bucketStorage.getFile(bucketName, fileName);
    }
}
