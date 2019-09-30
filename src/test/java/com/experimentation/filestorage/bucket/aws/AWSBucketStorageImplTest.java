package com.experimentation.filestorage.bucket.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.experimentation.filestorage.bucket.BucketStorageDTO;
import com.experimentation.filestorage.bucket.util.BucketStorageHelper;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit test for AWSBucketStorageImpl class
 */
public class AWSBucketStorageImplTest {

    // Class under test
    private static AWSBucketStorageImpl awsBucketStorageService;

    // Mocks
    private static AmazonS3 amazonS3;
    private static AWSBucketStorageHelper awsBucketStorageHelper;
    private static BucketStorageHelper bucketStorageHelper;
    private static S3Object s3Object;
    private static GetObjectRequest getObjectRequest;
    private static DeleteObjectRequest deleteObjectRequest;
    private static ObjectMetadata objectMetadata;
    private static S3ObjectInputStream s3ObjectInputStream;
    private static BucketStorageDTO bucketStorageDTO;
    private static TransferManager transferManager;
    private static MultipartFile multipartFile;
    private static InputStream inputStream;
    private static File file;
    private static Upload upload;

    // Final classes to be initialized with values
    private static String bucketName;
    private static String fileName;
    private static String contentType;

    @BeforeClass
    public static void setUp() {

        // Inject mocks into awsBucketStorageService
        amazonS3 = Mockito.mock(AmazonS3.class);
        awsBucketStorageHelper = Mockito.mock(AWSBucketStorageHelper.class);
        bucketStorageHelper = Mockito.mock(BucketStorageHelper.class);
        awsBucketStorageService = new AWSBucketStorageImpl(amazonS3, awsBucketStorageHelper, bucketStorageHelper);

        // Mocks
        s3Object = Mockito.mock(S3Object.class);
        getObjectRequest = Mockito.mock(GetObjectRequest.class);
        deleteObjectRequest = Mockito.mock(DeleteObjectRequest.class);
        objectMetadata = Mockito.mock(ObjectMetadata.class);
        s3ObjectInputStream = Mockito.mock(S3ObjectInputStream.class);
        bucketStorageDTO = Mockito.mock(BucketStorageDTO.class);
        transferManager = Mockito.mock(TransferManager.class);
        multipartFile = Mockito.mock(MultipartFile.class);
        inputStream = Mockito.mock(InputStream.class);
        file = Mockito.mock(File.class);
        upload = Mockito.mock(Upload.class);

        // Initialize values for testing
        bucketName = "example";
        fileName = "test.txt";
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
    }

    @After
    public void tearDown() {
        // Reset following static mocks to ensure verify methods are correct in each test
        Mockito.reset(amazonS3);
        Mockito.reset(transferManager);
        Mockito.reset(file);
        Mockito.reset(upload);
        Mockito.reset(inputStream);
        Mockito.reset(objectMetadata);
        Mockito.reset(s3Object);
    }

    @Test
    public void getFile_shouldReturnABucketStorageDTO_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageHelper).newGetObjectRequest(bucketName, fileName);
        Mockito.doReturn(s3Object)
                .when(amazonS3).getObject(getObjectRequest);
        Mockito.doReturn(objectMetadata)
                .when(s3Object).getObjectMetadata();
        Mockito.doReturn(contentType)
                .when(objectMetadata).getContentType();
        Mockito.doReturn(s3ObjectInputStream)
                .when(s3Object).getObjectContent();
        Mockito.doReturn(bucketStorageDTO)
                .when(bucketStorageHelper).createBucketStorageDTO(fileName, contentType, s3ObjectInputStream);

        // Act
        BucketStorageDTO resultBucketStorageDTO = awsBucketStorageService.getFile(bucketName, fileName);

        // Assert
        assertThat(resultBucketStorageDTO).isEqualTo(bucketStorageDTO);
        verifyGetObjectIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldThrowBucketStorageServiceException_whenAmazonServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageHelper).newGetObjectRequest(bucketName, fileName);
        Mockito.doThrow(AmazonServiceException.class)
                .when(amazonS3).getObject(getObjectRequest);

        // Act
        awsBucketStorageService.getFile(bucketName, fileName);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldThrowBucketStorageServiceException_whenSdkClientExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageHelper).newGetObjectRequest(bucketName, fileName);
        Mockito.doThrow(SdkClientException.class)
                .when(amazonS3).getObject(getObjectRequest);

        // Act
        awsBucketStorageService.getFile(bucketName, fileName);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test
    public void uploadMultipartFile_shouldCompleteUpload_whenBucketNameAndFileNameAndMultipartFileAreGiven()
            throws IOException, InterruptedException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageHelper).buildTransferManager(amazonS3);
        Mockito.doReturn(objectMetadata)
                .when(awsBucketStorageHelper).createObjectMetadata(multipartFile);
        Mockito.doReturn(inputStream)
                .when(multipartFile).getInputStream();
        Mockito.doReturn(upload)
                .when(transferManager).upload(bucketName, fileName, inputStream, objectMetadata);
        Mockito.doNothing()
                .when(upload).waitForCompletion();

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        verifyTransferManagerUploadIsCalledOnce();
        verifyUploadWaitForCompletionIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldThrowBucketStorageServiceException_whenAmazonServiceExceptionIsThrown()
            throws IOException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageHelper).buildTransferManager(amazonS3);
        Mockito.doReturn(objectMetadata)
                .when(awsBucketStorageHelper).createObjectMetadata(multipartFile);
        Mockito.doReturn(inputStream)
                .when(multipartFile).getInputStream();
        Mockito.doThrow(AmazonServiceException.class)
                .when(transferManager).upload(bucketName, fileName, inputStream, objectMetadata);

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldThrowBucketStorageServiceException_whenSdkClientExceptionIsThrown()
            throws IOException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageHelper).buildTransferManager(amazonS3);
        Mockito.doReturn(objectMetadata)
                .when(awsBucketStorageHelper).createObjectMetadata(multipartFile);
        Mockito.doReturn(inputStream)
                .when(multipartFile).getInputStream();
        Mockito.doThrow(SdkClientException.class)
                .when(transferManager).upload(bucketName, fileName, inputStream, objectMetadata);

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldThrowBucketStorageServiceException_whenIOExceptionIsThrown()
            throws IOException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageHelper).buildTransferManager(amazonS3);
        Mockito.doReturn(objectMetadata)
                .when(awsBucketStorageHelper).createObjectMetadata(multipartFile);
        Mockito.doThrow(IOException.class)
                .when(multipartFile).getInputStream();

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldThrowBucketStorageServiceException_whenInterruptedExceptionIsThrown()
            throws IOException, InterruptedException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageHelper).buildTransferManager(amazonS3);
        Mockito.doReturn(objectMetadata)
                .when(awsBucketStorageHelper).createObjectMetadata(multipartFile);
        Mockito.doReturn(inputStream)
                .when(multipartFile).getInputStream();
        Mockito.doReturn(upload)
                .when(transferManager).upload(bucketName, fileName, inputStream, objectMetadata);
        Mockito.doThrow(InterruptedException.class)
                .when(upload).waitForCompletion();

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test
    public void deleteFile_shouldDeleteObjectUsingAmazonS3_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageHelper).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doNothing()
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);

        // Assert
        verifyAmazonS3DeleteObjectIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldThrowBucketStorageServiceException_whenAmazonS3ExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageHelper).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doThrow(AmazonS3Exception.class)
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldThrowBucketStorageServiceException_whenSdkClientExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageHelper).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doThrow(SdkClientException.class)
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    private void verifyGetObjectIsCalledOnce() {
        Mockito.verify(amazonS3, VerificationModeFactory.times(1))
                .getObject(any(GetObjectRequest.class));
    }

    private void verifyTransferManagerUploadIsCalledOnce() {
        Mockito.verify(transferManager, VerificationModeFactory.times(1))
                .upload(eq(bucketName), eq(fileName), eq(inputStream), eq(objectMetadata));
    }

    private void verifyUploadWaitForCompletionIsCalledOnce() throws InterruptedException {
        Mockito.verify(upload, VerificationModeFactory.times(1))
                .waitForCompletion();
    }

    private void verifyAmazonS3DeleteObjectIsCalledOnce() {
        Mockito.verify(amazonS3, VerificationModeFactory.times(1))
                .deleteObject(any(DeleteObjectRequest.class));
    }
}