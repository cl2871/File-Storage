package com.experimentation.filestorage.bucket;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
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
public class AWSBucketStorageUtil {

    protected GetObjectRequest newGetObjectRequest(String bucketName, String fileName) {
        return new GetObjectRequest(bucketName, fileName);
    }

    protected DeleteObjectRequest newDeleteObjectRequest(String bucketName, String fileName) {
        return new DeleteObjectRequest(bucketName, fileName);
    }

    protected byte[] convertS3ObjectInputStreamToByteArray(S3ObjectInputStream s3ObjectInputStream) throws IOException {
        return IOUtils.toByteArray(s3ObjectInputStream);
    }

    protected TransferManager buildTransferManager(AmazonS3 amazonS3) {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
    }

    /**
     * Converts a MultipartFile object into a temporary File object.
     *
     * @param multipartFile
     * @return convertedFile
     * @throws IOException
     */
    protected File convertMultipartFileToTemporaryFile(MultipartFile multipartFile) throws IOException {

        // Create a temp file
        File convertedFile = File.createTempFile("temp-", null);

        // Write the bytes of the multipart
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(multipartFile.getBytes());
        fos.close();

        return convertedFile;
    }
}
