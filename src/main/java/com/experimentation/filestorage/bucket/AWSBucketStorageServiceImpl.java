package com.experimentation.filestorage.bucket;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Service
public class AWSBucketStorageServiceImpl implements BucketStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AWSBucketStorageServiceImpl.class);

    private final AmazonS3 amazonS3;
    private final AWSBucketStorageUtil awsBucketStorageUtil;

    @Autowired
    public AWSBucketStorageServiceImpl(AmazonS3 amazonS3,
                                       AWSBucketStorageUtil awsBucketStorageUtil) {
        this.amazonS3 = amazonS3;
        this.awsBucketStorageUtil = awsBucketStorageUtil;
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) throws FileStorageServiceException {

        logger.info("Retrieving file " + fileName);

        // Try-with-resources for the S3Object
        try (S3Object s3Object = amazonS3.getObject(awsBucketStorageUtil.newGetObjectRequest(bucketName, fileName))){

            String contentType = s3Object.getObjectMetadata().getContentType();
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

            byte[] bytes = awsBucketStorageUtil.convertS3ObjectInputStreamToByteArray(objectInputStream);

            // Note: encoder converts a space to a plus, so we replace the pluses with %20 for content disposition
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

            return new FileStorageDTO(fileName, contentType, bytes);
        }

        // Upload call was transmitted successfully, but Amazon S3 couldn't process it
        catch (AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to get file " + fileName);
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch (SdkClientException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to get file " + fileName);
        }

        // Could not convert S3ObjectInputStream to byte array
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to convert input stream to bytes for file " + fileName);
        }
    }

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file) throws FileStorageServiceException {

        File tempFile = null;

        try {
            TransferManager transferManager = awsBucketStorageUtil.buildTransferManager(amazonS3);
            tempFile = awsBucketStorageUtil.convertMultipartFileToTemporaryFile(file);

            // TransferManager processes all transfers asynchronously, so the upload call returns immediately
            Upload upload = transferManager.upload(bucketName, fileName, tempFile);
            logger.info("Started uploading file " + fileName);

            // We wait for the upload to finish before continuing.
            upload.waitForCompletion();
            logger.info("Upload completed for file " + fileName);
        }

        // Upload call was transmitted successfully, but Amazon S3 couldn't process it
        catch(AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to upload file " + fileName);
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch(SdkClientException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to upload file " + fileName);
        }

        // While upload waitForCompletion, the thread is interrupted
        catch(InterruptedException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Upload thread was interrupted " + fileName);
        }

        // Unable to create a temporary file for upload
        catch(IOException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to create a temp file or read content from file " + fileName);
        }

        // Clean up by removing temp file
        finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws FileStorageServiceException {

        try {
            amazonS3.deleteObject(awsBucketStorageUtil.newDeleteObjectRequest(bucketName, fileName));
            logger.info("Deleted file " + fileName);
        }

        // Delete call was transmitted successfully, but Amazon S3 couldn't process it
        catch(AmazonServiceException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to delete file " + fileName);
        }

        // Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response
        catch(SdkClientException e) {
            logger.error(e.getMessage());
            throw new FileStorageServiceException("Unable to delete file " + fileName);
        }
    }
}