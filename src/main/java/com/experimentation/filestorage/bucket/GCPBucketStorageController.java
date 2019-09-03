package com.experimentation.filestorage.bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/fileStorage/gcp")
public class GCPBucketStorageController {

    private String bucketName;
    private GCPBucketStorageImpl gcpBucketStorageService;

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageController.class);

    @Autowired
    public GCPBucketStorageController(@Value("${app.gcp.bucket}") String bucketName, GCPBucketStorageImpl gcpBucketStorageService) {
        this.bucketName = bucketName;
        this.gcpBucketStorageService = gcpBucketStorageService;
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) throws IOException {

        ResponseEntity responseEntity;
        try {
            BucketStorageDTO bucketStorageDTO = gcpBucketStorageService.getFile(bucketName, fileName);

            // Setup response to have the object/file as an attachment
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(bucketStorageDTO.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + bucketStorageDTO.getFileName() + "\"")
                    .body(bucketStorageDTO.getData());
        } catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }
        return responseEntity;
    }

    @PostMapping("")
    public ResponseEntity<?> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {

        ResponseEntity responseEntity;
        try {
            gcpBucketStorageService.uploadMultipartFile(bucketName, multipartFile.getOriginalFilename(), multipartFile);
            responseEntity = ResponseEntity.ok().build();
        } catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }
        return responseEntity;
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) throws Exception {

        ResponseEntity responseEntity;
        try {
            gcpBucketStorageService.deleteFile(bucketName, fileName);
            responseEntity = ResponseEntity.ok().build();
        } catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }
        return responseEntity;
    }
}
