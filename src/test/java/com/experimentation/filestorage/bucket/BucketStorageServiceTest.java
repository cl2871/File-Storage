package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.metadata.BucketStorageMetadata;
import com.experimentation.filestorage.bucket.metadata.BucketStorageMetadataService;
import com.experimentation.filestorage.bucket.provider.BucketStorageProviderRule;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit test for the BucketStorageService class
 */
public class BucketStorageServiceTest {

    // Class under test
    private static BucketStorageService bucketStorageService;

    // Mocks
    private static BucketStorageFactory bucketStorageFactory;
    private static BucketStorageProviderRule bucketStorageProviderRule;
    private static BucketStorageMetadataService bucketStorageMetadataService;
    private static BucketStorageMetadata bucketStorageMetadata;
    private static BucketStorageDTO bucketStorageDTO;
    private static BucketStorage bucketStorage;
    private static MultipartFile multipartFile;

    // Variables to have initialized values (e.g. final classes)
    private static String bucketName;
    private static String fileName;
    private static BucketStorageType bucketStorageType;
    private static UUID uuid;
    private static String storageProvider;

    @BeforeClass
    public static void setUp() {

        // Create a BucketStorageFactory mock to be passed into the bucketStorageService
        bucketStorageFactory = Mockito.mock(BucketStorageFactory.class);
        bucketStorageProviderRule = Mockito.mock(BucketStorageProviderRule.class);
        bucketStorageMetadataService = Mockito.mock(BucketStorageMetadataService.class);
        bucketStorageService = new
                BucketStorageService(bucketStorageFactory, bucketStorageProviderRule, bucketStorageMetadataService);

        // Mocks
        bucketStorageMetadata = Mockito.mock(BucketStorageMetadata.class);
        bucketStorageDTO = Mockito.mock(BucketStorageDTO.class);
        bucketStorage = Mockito.mock(BucketStorage.class);
        multipartFile = Mockito.mock(MultipartFile.class);

        // Initialize values for testing
        bucketName = "example-bucket";
        fileName = "test.jpg";
        bucketStorageType = BucketStorageType.GCP;
        uuid = UUID.randomUUID();
        storageProvider = BucketStorageTypeConstants.GCP;
    }

    @After
    public void tearDown() {
        Mockito.reset(bucketStorage);
        Mockito.reset(bucketStorageMetadataService);
    }

    @Test
    public void getFile_shouldReturnABucketStorageDTO_whenCalledNormally() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService).getBucketStorageMetadata(uuid);

        // Method logic will get corresponding bucketStorageType using the storageProvider string
        Mockito.doReturn(storageProvider)
                .when(bucketStorageMetadata).getStorageProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);

        // The bucketName and fileName from the bucketStorageMetadata are arguments for getFile()
        Mockito.doReturn(bucketName)
                .when(bucketStorageMetadata).getBucketName();
        Mockito.doReturn(fileName)
                .when(bucketStorageMetadata).getKeyName();
        Mockito.doReturn(bucketStorageDTO)
                .when(bucketStorage).getFile(bucketName, fileName);

        // Act
        BucketStorageDTO resultBucketStorageDTO = bucketStorageService.doGetFile(uuid);

        // Assert
        assertThat(resultBucketStorageDTO).isEqualTo(bucketStorageDTO);
        verifyGetBucketStorageMetadataIsCalledOnce();
        verifyGetFileIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService).getBucketStorageMetadata(uuid);

        // Method logic will get corresponding bucketStorageType using the storageProvider string
        Mockito.doReturn(storageProvider)
                .when(bucketStorageMetadata).getStorageProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);

        // The bucketName and fileName from the bucketStorageMetadata are arguments for getFile()
        Mockito.doReturn(bucketName)
                .when(bucketStorageMetadata).getBucketName();
        Mockito.doReturn(fileName)
                .when(bucketStorageMetadata).getKeyName();
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).getFile(bucketName, fileName);

        // Act
        bucketStorageService.doGetFile(uuid);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test
    public void uploadMultipartFile_shouldUploadFile_whenCalledNormally() {

        // Arrange
        Mockito.doReturn(bucketStorageType)
                .when(bucketStorageProviderRule).chooseProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doNothing()
                .when(bucketStorage).uploadMultipartFile(bucketName, fileName, multipartFile);
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService)
                .createBucketStorageMetadata(bucketStorageType, bucketName, fileName);
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService).saveBucketStorageMetadata(bucketStorageMetadata);
        Mockito.doReturn(uuid)
                .when(bucketStorageMetadata).getId();

        // Act
        UUID storageId = bucketStorageService.doUploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        assertThat(storageId).isEqualTo(uuid);
        verifySaveBucketStorageMetadataIsCalledOnce();
        verifyUploadMultipartFileIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadMultipartFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {
        // Arrange
        Mockito.doReturn(bucketStorageType)
                .when(bucketStorageProviderRule).chooseProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).uploadMultipartFile(bucketName, fileName, multipartFile);

        // Act
        bucketStorageService.doUploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    @Test
    public void deleteFile_shouldUploadFile_whenCalledNormally() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService).getBucketStorageMetadata(uuid);

        // Method logic will get corresponding bucketStorageType using the storageProvider string
        Mockito.doReturn(storageProvider)
                .when(bucketStorageMetadata).getStorageProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);

        // The bucketName and fileName from the bucketStorageMetadata are arguments for deleteFile()
        Mockito.doReturn(bucketName)
                .when(bucketStorageMetadata).getBucketName();
        Mockito.doReturn(fileName)
                .when(bucketStorageMetadata).getKeyName();
        Mockito.doNothing()
                .when(bucketStorage).deleteFile(bucketName, fileName);
        Mockito.doNothing()
                .when(bucketStorageMetadataService).deleteBucketStorageMetadata(uuid);

        // Act
        bucketStorageService.doDeleteFile(uuid);

        // Assert
        verifyDeleteBucketStorageMetadataIsCalledOnce();
        verifyDeleteFileIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldRethrowException_whenBucketStorageServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataService).getBucketStorageMetadata(uuid);

        // Method logic will get corresponding bucketStorageType using the storageProvider string
        Mockito.doReturn(storageProvider)
                .when(bucketStorageMetadata).getStorageProvider();
        Mockito.doReturn(bucketStorage)
                .when(bucketStorageFactory).getBucketStorageService(bucketStorageType);

        // The bucketName and fileName from the bucketStorageMetadata are arguments for deleteFile()
        Mockito.doReturn(bucketName)
                .when(bucketStorageMetadata).getBucketName();
        Mockito.doReturn(fileName)
                .when(bucketStorageMetadata).getKeyName();
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorage).deleteFile(bucketName, fileName);

        // Act
        bucketStorageService.doDeleteFile(uuid);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    private void verifyGetFileIsCalledOnce() {
        Mockito.verify(bucketStorage, VerificationModeFactory.times(1))
                .getFile(bucketName, fileName);
    }

    private void verifyUploadMultipartFileIsCalledOnce() {
        Mockito.verify(bucketStorage, VerificationModeFactory.times(1))
                .uploadMultipartFile(bucketName, fileName, multipartFile);
    }

    private void verifyDeleteFileIsCalledOnce() {
        Mockito.verify(bucketStorage, VerificationModeFactory.times(1))
                .deleteFile(bucketName, fileName);
    }

    private void verifyGetBucketStorageMetadataIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataService, VerificationModeFactory.times(1))
                .getBucketStorageMetadata(eq(uuid));
    }

    private void verifySaveBucketStorageMetadataIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataService, VerificationModeFactory.times(1))
                .saveBucketStorageMetadata(bucketStorageMetadata);
    }

    private void verifyDeleteBucketStorageMetadataIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataService, VerificationModeFactory.times(1))
                .deleteBucketStorageMetadata(uuid);
    }
}