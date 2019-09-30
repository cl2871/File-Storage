package com.experimentation.filestorage.bucket;

import java.io.InputStream;

public class BucketStorageDTO {

    private String fileName;

    private String contentType;

    private InputStream data;

    public BucketStorageDTO(String fileName, String contentType, InputStream data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getData() {
        return data;
    }
}
