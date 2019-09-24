package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

    @Before
    public void setUp() {
        // Optional of mocked BucketStorageMetadata
        bucketStorageMetadataOptional = Optional.of(bucketStorageMetadata);
        bucketStorageMetadataOptionalNull = Optional.empty();
        uuid = UUID.randomUUID();
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
    public void saveBucketStorageMetadata() {
    }

    @Test
    public void updateBucketStorageMetadata() {
    }

    @Test
    public void deleteBucketStorageMetadata() {
    }
}