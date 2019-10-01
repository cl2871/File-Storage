package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.BucketStorageType;
import com.experimentation.filestorage.bucket.BucketStorageTypeConstants;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit test for BucketStorageMetadataServiceImpl class
 */
public class BucketStorageMetadataServiceImplTest {

    // Class under test
    private static BucketStorageMetadataServiceImpl bucketStorageMetadataService;

    // Mocks
    private static BucketStorageMetadataRepository bucketStorageMetadataRepository;
    private static List<BucketStorageMetadata> bucketStorageMetadataList;
    private static BucketStorageMetadata bucketStorageMetadata;

    // Final classes to be initialized
    private static Optional<BucketStorageMetadata> bucketStorageMetadataOptional;
    private static Optional<BucketStorageMetadata> bucketStorageMetadataOptionalNull;
    private static UUID uuid;
    private static String storageProvider;
    private static String bucketName;
    private static String keyName;

    // Other classes to be initialized
    private static BucketStorageType bucketStorageType;

    @BeforeClass
    public static void setUp() {

        // Inject mock into class under test
        bucketStorageMetadataRepository = Mockito.mock(BucketStorageMetadataRepository.class);
        bucketStorageMetadataService = new BucketStorageMetadataServiceImpl(bucketStorageMetadataRepository);

        // Mocks
        bucketStorageMetadataList = Mockito.mock(List.class);
        bucketStorageMetadata = Mockito.mock(BucketStorageMetadata.class);

        // Optional of mocked BucketStorageMetadata
        bucketStorageMetadataOptional = Optional.of(bucketStorageMetadata);
        bucketStorageMetadataOptionalNull = Optional.empty();
        uuid = UUID.randomUUID();
        storageProvider = BucketStorageTypeConstants.AWS_S3;
        bucketName = "example-bucket";
        keyName = "example-file";

        bucketStorageType = BucketStorageType.AWS_S3;
    }

    @After
    public void tearDown() {
        // Reset following static mocks to ensure verify methods are correct in each test
        Mockito.reset(bucketStorageMetadataRepository);
    }

    @Test
    public void getAllBucketStorageMetadata_shouldReturnAllBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataList)
                .when(bucketStorageMetadataRepository).findAll();

        // Act
        Iterable<BucketStorageMetadata> metadataIterable = bucketStorageMetadataService.getAllBucketStorageMetadata();

        // Assert
        assertThat(metadataIterable).isEqualTo(bucketStorageMetadataList);
    }

    @Test
    public void getBucketStorageMetadata_shouldReturnBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataOptional)
                .when(bucketStorageMetadataRepository).findById(uuid);
        // bucketStorageMetadataOptional will return the bucketStorageMetadata mock on orElseThrow()

        // Act
        BucketStorageMetadata returnedBucketStorageMetadata = bucketStorageMetadataService.getBucketStorageMetadata(uuid);

        // Assert
        assertThat(returnedBucketStorageMetadata).isEqualTo(bucketStorageMetadata);
        verifyFindByIdIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getBucketStorageMetadata_shouldThrowBucketStorageServiceException_whenBucketStorageMetadataNotFound() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataOptionalNull)
                .when(bucketStorageMetadataRepository).findById(uuid);
        // bucketStorageMetadataOptional will throw an error on orElseThrow()

        // Act
        bucketStorageMetadataService.getBucketStorageMetadata(uuid);

        // Assert
        // Test annotation expects a BucketStorageServiceException
    }

    @Test
    public void saveBucketStorageMetadata_shouldSaveBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadata)
                .when(bucketStorageMetadataRepository).save(bucketStorageMetadata);

        // Act
        BucketStorageMetadata savedBucketStorageMetadata = bucketStorageMetadataService
                .saveBucketStorageMetadata(bucketStorageMetadata);

        // Assert
        assertThat(savedBucketStorageMetadata).isEqualTo(bucketStorageMetadata);
        verifySaveIsCalledOnce();
    }

    @Test
    public void updateBucketStorageMetadata_shouldUpdateBucketStorageMetadataWithNewValues_whenCalledRegularly() {

        // Arrange
        // bucketStorageMetadataOld acts as object in db to be updated
        BucketStorageMetadata bucketStorageMetadataOld = new BucketStorageMetadata();

        // bucketStorageMetadataUpdated acts as incoming request with updated values
        BucketStorageMetadata bucketStorageMetadataUpdated = new BucketStorageMetadata();
        bucketStorageMetadataUpdated.setStorageProvider(storageProvider);
        bucketStorageMetadataUpdated.setBucketName(bucketName);
        bucketStorageMetadataUpdated.setKeyName(keyName);

        // bucketStorageMetadata will be retrieved from db, updated, then saved back to the db
        Mockito.doReturn(Optional.of(bucketStorageMetadataOld))
                .when(bucketStorageMetadataRepository).findById(uuid);
        Mockito.doReturn(bucketStorageMetadataOld)
                .when(bucketStorageMetadataRepository).save(bucketStorageMetadataOld);

        // Act
        BucketStorageMetadata savedBucketStorageMetadata = bucketStorageMetadataService
                .updateBucketStorageMetadata(uuid, bucketStorageMetadataUpdated);

        // Assert
        // savedBucketStorageMetadata should have the new values
        assertThat(savedBucketStorageMetadata.getStorageProvider()).isEqualTo(storageProvider);
        assertThat(savedBucketStorageMetadata.getBucketName()).isEqualTo(bucketName);
        assertThat(savedBucketStorageMetadata.getKeyName()).isEqualTo(keyName);
        verifyFindByIdIsCalledOnce();
        verifySaveIsCalledOnce();
    }

    @Test
    public void deleteBucketStorageMetadata_shouldDeleteBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataOptional)
                .when(bucketStorageMetadataRepository).findById(uuid);
        Mockito.doNothing()
                .when(bucketStorageMetadataRepository).delete(bucketStorageMetadata);

        // Act
        bucketStorageMetadataService.deleteBucketStorageMetadata(uuid);

        // Assert
        verifyFindByIdIsCalledOnce();
        verifyDeleteIsCalledOnce();
    }

    @Test
    public void createBucketStorageMetadata() {

        // Act
        BucketStorageMetadata bucketStorageMetadata = bucketStorageMetadataService
                .createBucketStorageMetadata(bucketStorageType, bucketName, keyName);

        // Assert
        assertThat(bucketStorageMetadata.getStorageProvider()).isEqualTo(storageProvider);
        assertThat(bucketStorageMetadata.getBucketName()).isEqualTo(bucketName);
        assertThat(bucketStorageMetadata.getKeyName()).isEqualTo(keyName);
    }

    private void verifyFindByIdIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataRepository, VerificationModeFactory.times(1))
                .findById(uuid);
    }

    private void verifySaveIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataRepository, VerificationModeFactory.times(1))
                .save(any(BucketStorageMetadata.class));
    }

    private void verifyDeleteIsCalledOnce() {
        Mockito.verify(bucketStorageMetadataRepository, VerificationModeFactory.times(1))
                .delete(any(BucketStorageMetadata.class));
    }
}