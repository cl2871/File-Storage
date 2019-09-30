package com.experimentation.filestorage.bucket.gcp;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.common.io.ByteStreams;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

@Component
public class GCPBucketStorageHelper {

    protected BlobId createBlobId(String bucketName, String fileName) {
        return BlobId.of(bucketName, fileName);
    }

    protected BlobInfo createBlobInfo(BlobId blobId, String contentType){
        return BlobInfo.newBuilder(blobId).setContentType(contentType).build();
    }

    protected InputStream createInputStreamFromBlob(Blob blob) {
        ReadChannel reader = blob.reader();
        return Channels.newInputStream(reader);
    }

    protected void copyInputStreamToOutputStreamFromWriteChannel(MultipartFile multipartFile, WriteChannel writer)
            throws IOException {
        ByteStreams.copy(multipartFile.getInputStream(), Channels.newOutputStream(writer));
    }
}
