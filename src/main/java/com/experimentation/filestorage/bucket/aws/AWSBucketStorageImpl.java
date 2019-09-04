package com.experimentation.filestorage.bucket.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.experimentation.filestorage.bucket.*;
import com.experimentation.filestorage.bucket.util.BucketStorageExceptionUtil;
import com.experimentation.filestorage.bucket.util.BucketStorageHelper;
import com.experimentation.filestorage.bucket.util.BucketStorageLoggerUtil;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Component(BucketStorageTypeConstants.AWS_S3)
public class AWSBucketStorageImpl implements BucketStorage {

    private static final Logger logger = LoggerFactory.getLogger(AWSBucketStorageImpl.class);

    private final AmazonS3 amazonS3;
    private final AWSBucketStorageHelper awsBucketStorageHelper;
    private final BucketStorageHelper bucketStorageHelper;

    @Autowired
    public AWSBucketStorageImpl(AmazonS3 amazonS3,
                                AWSBucketStorageHelper awsBucketStorageHelper,
                                BucketStorageHelper bucketStorageHelper) {
        this.amazonS3 = amazonS3;
        this.awsBucketStorageHelper = awsBucketStorageHelper;
        this.bucketStorageHelper = bucketStorageHelper;
    }

    @Override
    public BucketStorageDTO getFile(String bucketName, String fileName) throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartGettingFile(logger, bucketName, fileName);

        // Try-with-resources for the S3Object
        try (S3Object s3Object = amazonS3.getObject(awsBucketStorageHelper.newGetObjectRequest(bucketName, fileName))){

            String contentType = s3Object.getObjectMetadata().getContentType();
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

            byte[] bytes = awsBucketStorageHelper.convertS3ObjectInputStreamToByteArray(objectInputStream);

            // Note: encoder converts a space to a plus, so we replace the pluses with %20 for content disposition
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            BucketStorageLoggerUtil.infoFinishGettingFile(logger, bucketName, fileName);
            return bucketStorageHelper.createBucketStorageDTO(fileName, contentType, bytes);
        }

        // Upload call was transmitted successfully, but Amazon S3 couldn't process it
        catch (AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch (SdkClientException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToGetFile(bucketName, fileName)
            );
        }

        // Could not convert S3ObjectInputStream to byte array
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToConvertInputStream(fileName)
            );
        }
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartUploadingFile(logger, bucketName, fileName);

        File tempFile = null;

        try {
            TransferManager transferManager = awsBucketStorageHelper.buildTransferManager(amazonS3);
            tempFile = awsBucketStorageHelper.convertMultipartFileToTemporaryFile(file);

            // TransferManager processes all transfers asynchronously, so the upload call returns immediately
            Upload upload = transferManager.upload(bucketName, fileName, tempFile);

            // We wait for the upload to finish before continuing.
            upload.waitForCompletion();
            BucketStorageLoggerUtil.infoFinishUploadingFile(logger, bucketName, fileName);
        }

        // Upload call was transmitted successfully, but Amazon S3 couldn't process it
        catch(AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToUploadMultipartFile(bucketName, fileName)
            );
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch(SdkClientException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToUploadMultipartFile(bucketName, fileName)
            );
        }

        // While upload waitForCompletion, the thread is interrupted
        catch(InterruptedException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUploadThreadInterrupted(fileName)
            );
        }

        // Unable to create a temporary file for upload
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToCreateTempFileOrReadContent(fileName)
            );
        }

        // Clean up by removing temp file
        finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws BucketStorageServiceException {

        BucketStorageLoggerUtil.infoStartDeletingFile(logger, bucketName, fileName);

        try {
            amazonS3.deleteObject(awsBucketStorageHelper.newDeleteObjectRequest(bucketName, fileName));
            BucketStorageLoggerUtil.infoFinishDeletingFile(logger, bucketName, fileName);
        }

        // Delete call was transmitted successfully, but Amazon S3 couldn't process it
        catch(AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
            );
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch(SdkClientException e) {
            logger.error(e.getMessage());
            throw new BucketStorageServiceException(
                    BucketStorageExceptionUtil.setMessageUnableToDeleteFile(bucketName, fileName)
            );
        }
    }
}
