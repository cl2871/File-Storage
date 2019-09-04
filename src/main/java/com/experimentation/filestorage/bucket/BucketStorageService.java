package com.experimentation.filestorage.bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BucketStorageService {

    Logger logger = LoggerFactory.getLogger(BucketStorageService.class);

    @Autowired
    private BucketStorageFactory bucketStorageFactory;

    public BucketStorageDTO doGetFile(String bucketName, String fileName, BucketStorageType bucketStorageType)
            throws BucketStorageServiceException {
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);
        logger.info("BucketStorageService.doGetFile " + bucketStorage);
        return bucketStorage.getFile(bucketName, fileName);
    }

    public void doUploadMultipartFile(String bucketName, String fileName, MultipartFile multipartFile,
                                                BucketStorageType bucketStorageType)
            throws BucketStorageServiceException {
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);
        logger.info("BucketStorageService.doUploadMultipartFile " + bucketStorage);
        bucketStorage.uploadMultipartFile(bucketName, fileName, multipartFile);
    }

    public void doDeleteFile(String bucketName, String fileName, BucketStorageType bucketStorageType)
            throws BucketStorageServiceException {
        BucketStorage bucketStorage = bucketStorageFactory.getBucketStorageService(bucketStorageType);
        logger.info("BucketStorageService.doDeleteFile " + bucketStorage);
        bucketStorage.deleteFile(bucketName, fileName);
    }
}
