package com.experimentation.filestorage.bucket.util;

import org.slf4j.Logger;

public class BucketStorageLoggerUtil {

    public static void infoStartGettingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Getting file " + fileName + " at location " + bucketName);
    }

    public static void infoStartUploadingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Uploading file " + fileName + " to location " + bucketName);
    }

    public static void infoStartDeletingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Deleting file " + fileName + " at location " + bucketName);
    }

    public static void infoFinishGettingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Got file " + fileName + " at location " + bucketName);
    }

    public static void infoFinishUploadingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Uploaded file " + fileName + " to location " + bucketName);
    }

    public static void infoFinishDeletingFile(Logger logger, String bucketName, String fileName) {
        logger.info("Deleted file " + fileName + " at location " + bucketName);
    }
}
