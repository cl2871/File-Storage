package com.experimentation.filestorage.bucket;

import org.slf4j.Logger;

public class BucketStorageLoggerUtil {

    protected static void infoStartGettingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Getting file " + fileName + " at location " + bucketName);
    }

    protected static void infoStartUploadingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Uploading file " + fileName + " to location " + bucketName);
    }

    protected static void infoStartDeletingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Deleting file " + fileName + " at location " + bucketName);
    }

    protected static void infoFinishGettingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Got file " + fileName + " at location " + bucketName);
    }

    protected static void infoFinishUploadingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Uploaded file " + fileName + " to location " + bucketName);
    }

    protected static void infoFinishDeletingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Deleted file " + fileName + " at location " + bucketName);
    }
}
