package com.experimentation.filestorage.bucket;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AWSBucketStorageServiceImpl.class})
public class AWSBucketStorageServiceImplTest {

    @Autowired
    private AWSBucketStorageServiceImpl awsBucketStorageService;

    @MockBean
    private AmazonS3 amazonS3;

    @MockBean
    private AWSBucketStorageUtil awsBucketStorageUtil;

    @MockBean
    private S3Object s3Object;

    @MockBean
    private GetObjectRequest getObjectRequest;

    @MockBean
    private ObjectMetadata objectMetadata;

    @MockBean
    private S3ObjectInputStream s3ObjectInputStream;

    // Final classes
    private String bucketName;
    private String fileName;
    private String contentType;
    private byte[] bytes;

    @Before
    public void setUp() throws Exception {
        bucketName = "example";
        fileName = "test.txt";
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        bytes = "Some example test".getBytes();
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
    public void uploadMultipartFile() {
    }

    @Test
    public void deleteFile() {
    }

    private void verifyGetObjectIsCalledOnce() {
        Mockito.verify(amazonS3, VerificationModeFactory.times(1))
                .getObject(any(GetObjectRequest.class));
    }
}