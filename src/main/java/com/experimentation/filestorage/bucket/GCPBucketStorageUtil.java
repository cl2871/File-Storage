package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import org.springframework.stereotype.Component;

@Component
public class GCPBucketStorageUtil {

    public BlobId createBlobId(String bucketName, String fileName) {
        return BlobId.of(bucketName, fileName);
    }

    public BlobInfo createBlobInfo(BlobId blobId, String contentType){
        return BlobInfo.newBuilder(blobId).setContentType(contentType).build();
    }
}
