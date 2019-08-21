package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class GCPBucketStorageServiceImpl implements BucketStorageService {

    @Override
    public void uploadMultipartFile(String bucketName, String fileName, MultipartFile file)
            throws IOException{
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        Blob blob = getStorage().create(blobInfo, file.getBytes());
    }

    @Override
    public void uploadFile(String bucketName, String fileName, File file, String contentType) throws Exception {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        Blob blob = getStorage().create(blobInfo, Files.readAllBytes(file.toPath()));
    }

    @Override
    public void deleteFile(String bucketName, String fileName) throws Exception {
        BlobId blobId = BlobId.of(bucketName, fileName);
        boolean deleted = getStorage().delete(blobId);
        if (!deleted) {
            throw new Exception("Unable to delete file: " + fileName);
        }
    }

    @Override
    public FileStorageDTO getFile(String bucketName, String fileName) {
        Blob blob = getStorage().get(BlobId.of(bucketName, fileName));
        return new FileStorageDTO(fileName, blob.getContentType(), blob.getContent());
    }

    private Storage getStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
