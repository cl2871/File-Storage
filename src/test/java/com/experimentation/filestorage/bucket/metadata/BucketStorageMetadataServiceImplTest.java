package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.BucketStorageTypeConstants;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BucketStorageMetadataServiceImpl.class})
public class BucketStorageMetadataServiceImplTest {

    @Autowired
    private BucketStorageMetadataServiceImpl bucketStorageMetadataService;

    @MockBean
    private BucketStorageMetadataRepository bucketStorageMetadataRepository;

    @MockBean
    private List<BucketStorageMetadata> bucketStorageMetadataList;

    @MockBean
    private BucketStorageMetadata bucketStorageMetadata;

    private Optional<BucketStorageMetadata> bucketStorageMetadataOptional;
    private Optional<BucketStorageMetadata> bucketStorageMetadataOptionalNull;
    private UUID uuid;
    private String storageProvider;
    private String storageLocation;

    @Before
    public void setUp() {
        // Optional of mocked BucketStorageMetadata
        bucketStorageMetadataOptional = Optional.of(bucketStorageMetadata);
        bucketStorageMetadataOptionalNull = Optional.empty();
        uuid = UUID.randomUUID();
        storageProvider = BucketStorageTypeConstants.AWS_S3;
        storageLocation = "example-bucket";
    }

    @Test
    public void getAllBucketStorageMetadata_shouldReturnAllBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataList)
                .when(bucketStorageMetadataRepository).findAll();

        // Act
        Iterable<BucketStorageMetadata> metadataIterable = bucketStorageMetadataService.getAllBucketStorageMetadata();

        // Assert
        assertThat(bucketStorageMetadataList).isEqualTo(metadataIterable);
    }

    @Test
    public void getBucketStorageMetadata_shouldReturnBucketStorageMetadata_whenCalledRegularly() {

        // Arrange
        Mockito.doReturn(bucketStorageMetadataOptional)
                .when(bucketStorageMetadataRepository).findById(uuid);
        // bucketStorageMetadataOptional will return the bucketStorageMetadata mock on orElseThrow()

        // Act
        BucketStorageMetadata bucketStorageMetadata = bucketStorageMetadataService.getBucketStorageMetadata(uuid);

        // Assert
        assertThat(bucketStorageMetadata).isEqualTo(bucketStorageMetadata);
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
        assertThat(bucketStorageMetadata).isEqualTo(savedBucketStorageMetadata);
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
        bucketStorageMetadataUpdated.setStorageLocation(storageLocation);

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
        assertThat(storageProvider).isEqualTo(savedBucketStorageMetadata.getStorageProvider());
        assertThat(storageLocation).isEqualTo(savedBucketStorageMetadata.getStorageLocation());
        verifyFindByIdIsCalledOnce();
        verifySaveIsCalledOnce();
    }

    @Test
    public void deleteBucketStorageMetadata() {
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