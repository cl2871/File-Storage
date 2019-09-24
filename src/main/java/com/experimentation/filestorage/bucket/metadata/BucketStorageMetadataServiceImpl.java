package com.experimentation.filestorage.bucket.metadata;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BucketStorageMetadataServiceImpl implements BucketStorageMetadataService {

    private final BucketStorageMetadataRepository bucketStorageMetadataRepository;

    @Autowired
    BucketStorageMetadataServiceImpl(BucketStorageMetadataRepository bucketStorageMetadataRepository) {
        this.bucketStorageMetadataRepository = bucketStorageMetadataRepository;
    }

    /**
     * Returns all BucketStorageMetadata records
     * @return One or more BucketStorageMetadata
     */
    @Override
    public Iterable<BucketStorageMetadata> getAllBucketStorageMetadata() {
        return bucketStorageMetadataRepository.findAll();
    }

    /**
     * Gets a BucketStorageMetadata based on its uuid
     * @param uuid
     * @return One BucketStorageMetadata
     */
    @Override
    public BucketStorageMetadata getBucketStorageMetadata(UUID uuid) throws BucketStorageServiceException {
        return bucketStorageMetadataRepository
                .findById(uuid)
                .orElseThrow(() ->
                        new BucketStorageServiceException("Unable to find BucketStorageMetadata by UUID")
                );
    }

    /**
     * Saves a BucketStorageMetadata to the backend and returns the saved BucketStorageMetadata
     * @param bucketStorageMetadata
     * @return Saved BucketStorageMetadata
     */
    @Override
    public BucketStorageMetadata saveBucketStorageMetadata(BucketStorageMetadata bucketStorageMetadata) {
        return bucketStorageMetadataRepository.save(bucketStorageMetadata);
    }

    /**
     * Gets a BucketStorageMetadata by its id and updates it using the information in the request body
     * @param uuid
     * @param bucketStorageMetadata
     * @return Updated BucketStorageMetadata
     */
    @Override
    public BucketStorageMetadata updateBucketStorageMetadata(UUID uuid, BucketStorageMetadata bucketStorageMetadata) {
        return bucketStorageMetadataRepository.findById(uuid).map(oldBucketStorageMetadata -> {
            oldBucketStorageMetadata.setStorageProvider(bucketStorageMetadata.getStorageProvider());
            oldBucketStorageMetadata.setStorageLocation(bucketStorageMetadata.getStorageLocation());
            return bucketStorageMetadataRepository.save(oldBucketStorageMetadata);
        }).orElseThrow(() -> new BucketStorageServiceException("BucketStorageMetadata Id " + uuid + " not found"));
    }

    /**
     * Get a BucketStorageMetadata by its id and delete it.
     * @param uuid
     */
    @Override
    public void deleteBucketStorageMetadata(UUID uuid) {
        bucketStorageMetadataRepository.findById(uuid).ifPresent(bucketStorageMetadata -> {
            bucketStorageMetadataRepository.delete(bucketStorageMetadata);
        });
    }
}
