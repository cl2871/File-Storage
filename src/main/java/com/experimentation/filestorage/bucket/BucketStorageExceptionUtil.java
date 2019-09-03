package com.experimentation.filestorage.bucket;

public class BucketStorageExceptionUtil {

    protected static String setMessageUnableToGetFile(String bucketName, String fileName) {
        return "Unable to get file " + fileName + " at location " + bucketName;
    }

    protected static String setMessageUnableToUploadMultipartFile(String bucketName, String fileName) {
        return "Unable to upload file " + fileName + " to location " + bucketName;
    }

    protected static String setMessageUnableToDeleteFile(String bucketName, String fileName) {
        return "Unable to delete file " + fileName + " at location " + bucketName;
    }

    protected static String setMessageUnableToReadFileForUpload(String fileName) {
        return "Unable to read file " + fileName + " for upload";
    }

    protected static String setMessageUnableToConvertInputStream(String fileName) {
        return "Unable to convert input stream to bytes for file " + fileName;
    }

    protected static String setMessageUploadThreadInterrupted(String fileName) {
        return "Thread was interrupted while trying to upload file " + fileName;
    }

    protected static String setMessageUnableToCreateTempFileOrReadContent(String fileName) {
        return "Unable to create a temp file or read content from file " + fileName;
    }
}
