package com.experimentation.filestorage.bucket.gcp;

import com.experimentation.filestorage.bucket.*;
import com.experimentation.filestorage.bucket.util.BucketStorageExceptionUtil;
import com.experimentation.filestorage.bucket.util.BucketStorageHelper;
import com.experimentation.filestorage.bucket.util.BucketStorageLoggerUtil;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

@Component(BucketStorageTypeConstants.GCP)
public class GCPBucketStorageImpl implements BucketStorage {

    private Storage storage;
    private final GCPBucketStorageHelper gcpBucketStorageHelper;
    private final BucketStorageHelper bucketStorageHelper;

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageImpl.class);

    @Autowired
    public GCPBucketStorageImpl(Storage storage,
                                GCPBucketStorageHelper gcpBucketStorageHelper,
                                BucketStorageHelper bucketStorageHelper) {
        this.storage = storage;
        this.gcpBucketStorageHelper = gcpBucketStorageHelper;
        this.bucketStorageHelper = bucketStorageHelper;
    }

    @Override
    public BucketStorageDTO getFile(String bucketName, String fileName) throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartGettingFile(logger, bucketName, fileName);

        try {
            Blob blob = storage.get(gcpBucketStorageHelper.createBlobId(bucketName, fileName));
            InputStream inputStream = gcpBucketStorageHelper.createInputStreamFromBlob(blob);

            BucketStorageLoggerUtil.infoFinishGettingFile(logger, bucketName, fileName);
            return bucketStorageHelper.createBucketStorageDTO(fileName, blob.getContentType(), inputStream);
        }

        // Returned blob is null; no file was found
        catch (NullPointerException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile multipartFile)
            throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartUploadingFile(logger, bucketName, fileName);

        BlobId blobId = gcpBucketStorageHelper.createBlobId(bucketName, fileName);
        BlobInfo blobInfo = gcpBucketStorageHelper.createBlobInfo(blobId, multipartFile.getContentType());

        try (WriteChannel writer = storage.writer(blobInfo)) {
            gcpBucketStorageHelper.copyInputStreamToOutputStreamFromWriteChannel(multipartFile, writer);
            BucketStorageLoggerUtil.infoFinishUploadingFile(logger, bucketName, fileName);
        }

        // Unable to get InputStream or close the WriteChannel
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException("Issue writing data from InputStream or closing writer");
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToUploadMultipartFile(bucketName, fileName)
            );
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartDeletingFile(logger, bucketName, fileName);

        try {
            BlobId blobId = gcpBucketStorageHelper.createBlobId(bucketName, fileName);
            boolean deleted = storage.delete(blobId);
            if (!deleted) {
                throw new BucketStorageServiceException(
                        BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
                );
            }
            BucketStorageLoggerUtil.infoFinishDeletingFile(logger, bucketName, fileName);
        }

        // Google Cloud extension of RuntimeException
        catch (BaseServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
            );
        }
    }
}
