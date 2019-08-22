package com.experimentation.filestorage.bucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/fileStorage")
public class GCPBucketStorageController {

    private static final Logger logger = LoggerFactory.getLogger(GCPBucketStorageController.class);

    private GCPBucketStorageServiceImpl gcpBucketStorageService;

    @Autowired
    public GCPBucketStorageController(GCPBucketStorageServiceImpl gcpBucketStorageService) {
        this.gcpBucketStorageService = gcpBucketStorageService;
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) throws IOException {

        ResponseEntity responseEntity = null;

        try {
            FileStorageDTO fileStorageDTO = gcpBucketStorageService.getFile("", fileName);

            // Setup response to have the object/file as an attachment
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileStorageDTO.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileStorageDTO.getFileName() + "\"")
                    .body(fileStorageDTO.getData());
        } catch (FileStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        } finally {
            return responseEntity;
        }
    }

    @PostMapping("")
    public ResponseEntity<?> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {

        ResponseEntity responseEntity = null;

        try {
            gcpBucketStorageService.uploadMultipartFile("", multipartFile.getOriginalFilename(), multipartFile);
            responseEntity = ResponseEntity.ok().build();
        } catch (FileStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        } finally {
            return responseEntity;
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) throws Exception {

        ResponseEntity responseEntity = null;

        try {
            gcpBucketStorageService.deleteFile("", fileName);
            responseEntity = ResponseEntity.ok().build();
        } catch (FileStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        } finally {
            return responseEntity;
        }
    }
}
