package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BucketStorageService.class})
public class BucketStorageServiceTest {

    @Autowired
    private BucketStorageService bucketStorageService;

    @MockBean
    private BucketStorageFactory bucketStorageFactory;

    @MockBean
    private BucketStorageDTO bucketStorageDTO;

    @MockBean
    private BucketStorage bucketStorage;

    @MockBean
    private MultipartFile multipartFile;

    // Final classes
    private String bucketName;
    private String fileName;
    private BucketStorageType bucketStorageType;

    @Before
    public void setUp() throws Exception {
        bucketName = "example-bucket";
        fileName = "test.jpg";
        bucketStorageType = BucketStorageType.AWS_S3;
    }

    @Test
    public void getFile_shouldReturnABucketStorageDTO_whenCalledNormally() {

        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doReturn(bucketStorageDTO)
                .when(bucketStorage).getFile(bucketName, fileName);

        // Act
        BucketStorageDTO resultBucketStorageDTO =
                bucketStorageService.doGetFile(bucketName, fileName, bucketStorageType);

        // Assert
        assertThat(resultBucketStorageDTO).isEqualTo(bucketStorageDTO);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).getFile(bucketName, fileName);

        // Act
        bucketStorageService.doGetFile(bucketName, fileName, bucketStorageType);
    }

    @Test
    public void uploadMultipartFile_shouldUploadFile_whenCalledNormally() {
        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doNothing()
                .when(bucketStorage).uploadMultipartFile(bucketName, fileName, multipartFile);

        // Act
        bucketStorageService.doUploadMultipartFile(bucketName, fileName, multipartFile, bucketStorageType);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {
        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).uploadMultipartFile(bucketName, fileName, multipartFile);

        // Act
        bucketStorageService.doUploadMultipartFile(bucketName, fileName, multipartFile, bucketStorageType);
    }

    @Test
    public void deleteFile_shouldUploadFile_whenCalledNormally() {
        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doNothing()
                .when(bucketStorage).deleteFile(bucketName, fileName);

        // Act
        bucketStorageService.doDeleteFile(bucketName, fileName, bucketStorageType);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {
        // Arrange
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).deleteFile(bucketName, fileName);

        // Act
        bucketStorageService.doDeleteFile(bucketName, fileName, bucketStorageType);
    }
}