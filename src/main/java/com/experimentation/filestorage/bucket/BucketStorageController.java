package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/fileStorage")
public class BucketStorageController {

    private final BucketStorageService bucketStorageService;

    private static final Logger logger = LoggerFactory.getLogger(BucketStorageController.class);

    @Autowired
    public BucketStorageController(BucketStorageService bucketStorageService) {
        this.bucketStorageService = bucketStorageService;
    }

    @GetMapping("{fileId}")
    public ResponseEntity<?> getFile(@PathVariable("fileId") UUID uuid) {
        ResponseEntity responseEntity;

        try {
            BucketStorageDTO bucketStorageDTO = bucketStorageService.doGetFile(uuid);
            InputStream inputStream = bucketStorageDTO.getData();
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            // Setup response to have the object/file as an attachment
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(bucketStorageDTO.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + bucketStorageDTO.getFileName() + "\"")
                    .body(inputStreamResource);
        }

        // Unable to retrieve file
        catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }

        return responseEntity;
    }

    @PostMapping("{bucketName}")
    public ResponseEntity<?> uploadFile(@PathVariable("bucketName") String bucketName,
                                        @RequestPart(value = "file") MultipartFile multipartFile) {

        ResponseEntity responseEntity;
        try {
            String fileName = multipartFile.getOriginalFilename();
            bucketStorageService.doUploadMultipartFile(bucketName, fileName, multipartFile);
            responseEntity = ResponseEntity.ok().build();
        }

        // Unable to upload file
        catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }

        return responseEntity;
    }

    @DeleteMapping("{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileId") UUID uuid) {

        ResponseEntity responseEntity;

        try {
            bucketStorageService.doDeleteFile(uuid);
            responseEntity = ResponseEntity.ok().build();
        }

        // Unable to delete file
        catch (BucketStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }

        return responseEntity;
    }
}
