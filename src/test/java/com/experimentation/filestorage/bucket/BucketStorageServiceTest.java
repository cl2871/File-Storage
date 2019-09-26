package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the BucketStorageService class
 */
public class BucketStorageServiceTest {

    // Class under test
    private static BucketStorageService bucketStorageService;

    // Mocks
    private static BucketStorageFactory bucketStorageFactory;
    private static BucketStorageDTO bucketStorageDTO;
    private static BucketStorage bucketStorage;
    private static MultipartFile multipartFile;

    // Variables to have initialized values (e.g. final classes)
    private static String bucketName;
    private static String fileName;
    private static BucketStorageType bucketStorageType;

    @BeforeClass
    public static void setUp() {

        // Create a BucketStorageFactory mock to be passed into the bucketStorageService
        bucketStorageFactory = Mockito.mock(BucketStorageFactory.class);
        bucketStorageService = new BucketStorageService(bucketStorageFactory);

        // Mocks
        bucketStorageDTO = Mockito.mock(BucketStorageDTO.class);
        bucketStorage = Mockito.mock(BucketStorage.class);
        multipartFile = Mockito.mock(MultipartFile.class);

        // Initialize values for testing
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }
}