package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.BucketStorageTypeConstants;
import com.experimentation.filestorage.config.JpaConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaConfig.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
public class BucketStorageMetadataRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BucketStorageMetadataRepository bucketStorageMetadataRepository;

    private String storageProvider;
    private String bucketName;
    private String keyName;

    @Before
    public void setUp() {
        storageProvider = BucketStorageTypeConstants.AWS_S3;
        bucketName = "example-bucket";
        keyName = "example-file";
    }

    @Test
    public void findById() {

        // Arrange
        // Create a BucketStorageMetadata object and save it (UUID and auditing fields will be generated when saved)
        BucketStorageMetadata bucketStorageMetadata = new BucketStorageMetadata();
        bucketStorageMetadata.setBucketName(bucketName);
        bucketStorageMetadata.setKeyName(keyName);
        bucketStorageMetadata.setStorageProvider(storageProvider);
        testEntityManager.persistAndFlush(bucketStorageMetadata);

        // Act
        UUID uuid = bucketStorageMetadata.getId();
        Optional<BucketStorageMetadata> entity = bucketStorageMetadataRepository.findById(uuid);

        // Assert
        assertThat(bucketName).isEqualTo(entity.get().getBucketName());
        assertThat(keyName).isEqualTo(entity.get().getKeyName());
        assertThat(storageProvider).isEqualTo(entity.get().getStorageProvider());
    }
}