package com.experimentation.filestorage.bucket;

public class FileStorageDTO {

    private String fileName;

    private String contentType;

    private byte[] data;

    public FileStorageDTO(String fileName, String contentType, byte[] data) {
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

    public byte[] getData() {
        return data;
    }
}
