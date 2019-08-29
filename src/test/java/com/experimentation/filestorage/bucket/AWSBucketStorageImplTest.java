package com.experimentation.filestorage.bucket;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AWSBucketStorageImpl.class})
public class AWSBucketStorageImplTest {

    @Autowired
    private AWSBucketStorageImpl awsBucketStorageService;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private AWSBucketStorageUtil awsBucketStorageUtil;

    @MockBean
    private S3Object s3Object;

    @MockBean
    private GetObjectRequest getObjectRequest;

    @MockBean
    private DeleteObjectRequest deleteObjectRequest;

    @MockBean
    private ObjectMetadata objectMetadata;

    @MockBean
    private S3ObjectInputStream s3ObjectInputStream;

    @MockBean
    private TransferManager transferManager;

    @MockBean
    private MultipartFile multipartFile;

    @MockBean
    private File file;

    @MockBean
    private Upload upload;

    // Final classes
    private String bucketName;
    private String fileName;
    private String contentType;
    private byte[] bytes;
    private boolean tempFileDeletedTrue;
    private boolean tempFileDeletedFalse;

    @Before
    public void setUp() throws Exception {
        bucketName = "example";
        fileName = "test.txt";
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        bytes = "Some example test".getBytes();
        tempFileDeletedTrue = true;
        tempFileDeletedFalse = true;
    }

    @Test
    public void getFile_shouldReturnAFileStorageDTO_whenCalledWithFileName() throws IOException {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageUtil).newGetObjectRequest(bucketName, fileName);
        Mockito.doReturn(s3Object)
                .when(amazonS3).getObject(getObjectRequest);
        Mockito.doReturn(objectMetadata)
                .when(s3Object).getObjectMetadata();
        Mockito.doReturn(contentType)
                .when(objectMetadata).getContentType();
        Mockito.doReturn(s3ObjectInputStream)
                .when(s3Object).getObjectContent();
        Mockito.doReturn(bytes)
                .when(awsBucketStorageUtil).convertS3ObjectInputStreamToByteArray(s3ObjectInputStream);

        // Act
        FileStorageDTO fileStorageDTO = awsBucketStorageService.getFile(bucketName, fileName);

        // Assert
        assertThat(fileStorageDTO.getFileName()).isEqualTo(fileName);
        assertThat(fileStorageDTO.getContentType()).isEqualTo(contentType);
        assertThat(fileStorageDTO.getData()).isEqualTo(bytes);
        verifyGetObjectIsCalledOnce();
    }

    @Test(expected = FileStorageServiceException.class)
    public void getFile_shouldThrowFileStorageException_whenAmazonServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageUtil).newGetObjectRequest(bucketName, fileName);
        Mockito.doThrow(AmazonServiceException.class)
                .when(amazonS3).getObject(getObjectRequest);

        // Act
        awsBucketStorageService.getFile(bucketName, fileName);
    }

    @Test(expected = FileStorageServiceException.class)
    public void getFile_shouldThrowFileStorageException_whenSdkClientExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageUtil).newGetObjectRequest(bucketName, fileName);
        Mockito.doThrow(SdkClientException.class)
                .when(amazonS3).getObject(getObjectRequest);

        // Act
        awsBucketStorageService.getFile(bucketName, fileName);
    }

    @Test(expected = FileStorageServiceException.class)
    public void getFile_shouldThrowFileStorageException_whenIOExceptionIsThrown() throws IOException {

        // Arrange
        Mockito.doReturn(getObjectRequest)
                .when(awsBucketStorageUtil).newGetObjectRequest(bucketName, fileName);
        Mockito.doReturn(s3Object)
                .when(amazonS3).getObject(getObjectRequest);
        Mockito.doReturn(objectMetadata)
                .when(s3Object).getObjectMetadata();
        Mockito.doReturn(contentType)
                .when(objectMetadata).getContentType();
        Mockito.doReturn(s3ObjectInputStream)
                .when(s3Object).getObjectContent();
        Mockito.doThrow(IOException.class)
                .when(awsBucketStorageUtil).convertS3ObjectInputStreamToByteArray(s3ObjectInputStream);

        // Act
        awsBucketStorageService.getFile(bucketName, fileName);
    }

    @Test
    public void uploadMultipartFile_shouldCompleteUpload_whenBucketNameAndFileNameAndMultipartFileAreGiven()
            throws Exception {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageUtil).buildTransferManager(amazonS3);
        Mockito.doReturn(file)
                .when(awsBucketStorageUtil).convertMultipartFileToTemporaryFile(multipartFile);
        Mockito.doReturn(upload)
                .when(transferManager).upload(bucketName, fileName, file);
        Mockito.doNothing()
                .when(upload).waitForCompletion();
        Mockito.doReturn(tempFileDeletedTrue)
                .when(file).delete();

        // Act
        awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        verifyTransferManagerUploadIsCalledOnce();
        verifyUploadWaitForCompletionIsCalledOnce();
        verifyTempFileDeleteIsCalledOnce();
    }

    @Test
    public void uploadMultipartFile_shouldThrowFileStorageException_whenAmazonServiceExceptionIsThrown() throws IOException {

        // Arrange
        Mockito.doReturn(transferManager)
                .when(awsBucketStorageUtil).buildTransferManager(amazonS3);
        Mockito.doReturn(file)
                .when(awsBucketStorageUtil).convertMultipartFileToTemporaryFile(multipartFile);
        Mockito.doThrow(AmazonServiceException.class)
                .when(transferManager).upload(bucketName, fileName, file);
        Mockito.doReturn(tempFileDeletedTrue)
                .when(file).delete();

        // Act
        try {
            awsBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);
        } catch (FileStorageServiceException e) {
            assertThat(e).isInstanceOf(FileStorageServiceException.class);
        }

        // Assert
        verifyTempFileDeleteIsCalledOnce();
    }

    @Test
    public void deleteFile_shouldDeleteObjectUsingAmazonS3_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageUtil).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doNothing()
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);

        // Assert
        verifyAmazonS3DeleteObjectIsCalledOnce();
    }

    @Test(expected = FileStorageServiceException.class)
    public void deleteFile_shouldThrowFileStorageServiceException_whenAmazonS3ExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageUtil).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doThrow(AmazonS3Exception.class)
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);
    }

    @Test(expected = FileStorageServiceException.class)
    public void deleteFile_shouldThrowFileStorageServiceException_whenSdkClientExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(deleteObjectRequest)
                .when(awsBucketStorageUtil).newDeleteObjectRequest(bucketName, fileName);
        Mockito.doThrow(SdkClientException.class)
                .when(amazonS3).deleteObject(deleteObjectRequest);

        // Act
        awsBucketStorageService.deleteFile(bucketName, fileName);
    }

    private void verifyGetObjectIsCalledOnce() {
        Mockito.verify(amazonS3, VerificationModeFactory.times(1))
                .getObject(any(GetObjectRequest.class));
    }

    private void verifyTransferManagerUploadIsCalledOnce() {
        Mockito.verify(transferManager, VerificationModeFactory.times(1))
                .upload(anyString(), anyString(), any(File.class));
    }

    private void verifyUploadWaitForCompletionIsCalledOnce() throws InterruptedException {
        Mockito.verify(upload, VerificationModeFactory.times(1))
                .waitForCompletion();
    }

    private void verifyTempFileDeleteIsCalledOnce() {
        Mockito.verify(file, VerificationModeFactory.times(1))
                .delete();
    }

    private void verifyAmazonS3DeleteObjectIsCalledOnce() {
        Mockito.verify(amazonS3, VerificationModeFactory.times(1))
                .deleteObject(any());
    }
}