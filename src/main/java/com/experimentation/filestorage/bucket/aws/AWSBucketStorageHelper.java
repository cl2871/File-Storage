package com.experimentation.filestorage.bucket.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AWSBucketStorageHelper {

    protected GetObjectRequest newGetObjectRequest(String bucketName, String fileName) {
        return new GetObjectRequest(bucketName, fileName);
    }

    protected DeleteObjectRequest newDeleteObjectRequest(String bucketName, String fileName) {
        return new DeleteObjectRequest(bucketName, fileName);
    }

    protected TransferManager buildTransferManager(AmazonS3 amazonS3) {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
    }

    /**
     * Returns an (AWS S3) ObjectMetadata object with contentType and contentLength
     * @param multipartFile
     * @return objectMetadata
     */
    protected ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }
}
