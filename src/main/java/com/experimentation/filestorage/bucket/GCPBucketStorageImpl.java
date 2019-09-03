package com.experimentation.filestorage.bucket;

import com.google.cloud.BaseServiceException;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component(BucketStorageTypeConstants.GCP_STORAGE)
public class GCPBucketStorageImpl implements BucketStorage {

    private Storage storage;
    private GCPBucketStorageUtil gcpBucketStorageUtil;
    private FileStorageUtil fileStorageUtil;

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageImpl.class);

    @Autowired
    public GCPBucketStorageImpl(Storage storage,
                                GCPBucketStorageUtil gcpBucketStorageUtil,
                                FileStorageUtil fileStorageUtil) {
        this.storage = storage;
        this.gcpBucketStorageUtil = gcpBucketStorageUtil;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) throws FileStorageServiceException {

        BucketStorageLoggerUtil.infoStartGettingFile(logger, bucketName, fileName);

        try {
            Blob blob = storage.get(gcpBucketStorageUtil.createBlobId(bucketName, fileName));
            BucketStorageLoggerUtil.infoFinishGettingFile(logger, bucketName, fileName);
            return fileStorageUtil.createFileStorageDTO(fileName, blob.getContentType(), blob.getContent());
        }

        // Returned blob is null; no file was found
        catch (NullPointerException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile multipartFile)
            throws FileStorageServiceException {

        BucketStorageLoggerUtil.infoStartUploadingFile(logger, bucketName, fileName);

        BlobId blobId = gcpBucketStorageUtil.createBlobId(bucketName, fileName);
        BlobInfo blobInfo = gcpBucketStorageUtil.createBlobInfo(blobId, multipartFile.getContentType());

        try {
            storage.create(blobInfo, multipartFile.getBytes());
            BucketStorageLoggerUtil.infoFinishUploadingFile(logger, bucketName, fileName);
        }

        // Unable to read multipart file for upload
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToReadFileForUpload(fileName)
            );
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToUploadMultipartFile(bucketName, fileName)
            );
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws FileStorageServiceException {

        BucketStorageLoggerUtil.infoStartDeletingFile(logger, bucketName, fileName);

        try {
            BlobId blobId = gcpBucketStorageUtil.createBlobId(bucketName, fileName);
            boolean deleted = storage.delete(blobId);
            if (!deleted) {
                throw new FileStorageServiceException(
                        BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
                );
            }
            BucketStorageLoggerUtil.infoFinishDeletingFile(logger, bucketName, fileName);
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
            );
        }
    }
}
