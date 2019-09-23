package com.experimentation.filestorage.bucket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BucketStorageMetadataRepository extends JpaRepository<BucketStorageMetadata, UUID> {

    @Override
    Optional<BucketStorageMetadata> findById(UUID uuid);
}
