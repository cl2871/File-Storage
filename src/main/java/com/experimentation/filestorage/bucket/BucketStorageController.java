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

@RestController
@RequestMapping("/api/fileStorage")
public class BucketStorageController {

    private final String awsBucketName;
    private final String gcpBucketName;
    private final BucketStorageService bucketStorageService;

    private static final Logger logger = LoggerFactory.getLogger(BucketStorageController.class);

    @Autowired
    public BucketStorageController(@Value("${app.aws.bucket}") String awsBucketName,
                                   @Value("${app.gcp.bucket}") String gcpBucketName,
                                   BucketStorageService bucketStorageService) {
        this.awsBucketName = awsBucketName;
        this.gcpBucketName = gcpBucketName;
        this.bucketStorageService = bucketStorageService;
    }

    @GetMapping("storageProvider/{storageProvider}/storageLocation/{storageLocation}/fileName/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable("storageProvider") String storageProvider,
                                     @PathVariable("storageLocation") String bucketName,
                                     @PathVariable String fileName) {
        ResponseEntity responseEntity;

        try {
            BucketStorageType bucketStorageType = BucketStorageType.valueOf(storageProvider);
            FileStorageDTO fileStorageDTO = bucketStorageService.doGetFile(bucketName, fileName, bucketStorageType);

            // Setup response to have the object/file as an attachment
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileStorageDTO.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileStorageDTO.getFileName() + "\"")
                    .body(fileStorageDTO.getData());
        }

        // Unable to retrieve file
        catch (FileStorageServiceException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body(e.getMessage());
        }

        // BucketStorageType could not be identified from the storageProvider value
        catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            responseEntity = ResponseEntity.status(500).body("Invalid storage provider identifier provided.");
        }
        return responseEntity;
    }

//    @PostMapping("")
//    public ResponseEntity<?> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) {
//
//        ResponseEntity responseEntity;
//        try {
//            bucketStorageService.uploadMultipartFile(bucketName, multipartFile.getOriginalFilename(), multipartFile);
//            responseEntity = ResponseEntity.ok().build();
//        } catch (FileStorageServiceException e) {
//            logger.error(e.getMessage());
//            responseEntity = ResponseEntity.status(500).body(e.getMessage());
//        }
//        return responseEntity;
//    }
//
//    @DeleteMapping("/{fileName}")
//    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
//
//        ResponseEntity responseEntity;
//        try {
//            awsBucketStorageService.deleteFile(bucketName, fileName);
//            responseEntity = ResponseEntity.ok().build();
//        } catch (FileStorageServiceException e) {
//            logger.error(e.getMessage());
//            responseEntity = ResponseEntity.status(500).body(e.getMessage());
//        }
//        return responseEntity;
//    }
}
