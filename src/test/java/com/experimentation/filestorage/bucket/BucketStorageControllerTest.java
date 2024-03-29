package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BucketStorageController.class)
public class BucketStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BucketStorageService bucketStorageService;

    @MockBean
    private MultipartFile multipartFile;

    // Final classes
    private String baseUrl;
    private String bucketName;
    private String fileName;
    private MediaType mediaType;
    private byte[] content;

    // Other classes
    private BucketStorageDTO bucketStorageDTO;
    private MockPart requestPart;

    @Before
    public void setUp() {
        baseUrl = "/api/fileStorage";

        fileName = "example.txt";
        bucketName = "my-bucket";
        mediaType = MediaType.TEXT_PLAIN;
        content = "Coffee ipsum".getBytes();

        bucketStorageDTO = new BucketStorageDTO(fileName, MediaType.TEXT_PLAIN_VALUE, content);
        requestPart = new MockPart("file", fileName, content);
    }

    @Test
    public void getFile_shouldReturnFile_whenCalledNormally() throws Exception {

        // Arrange
        Mockito.doReturn(bucketStorageDTO)
                .when(bucketStorageService).doGetFile(bucketName, fileName, BucketStorageType.AWS_S3);

        // Act and Assert
        mockMvc
                .perform(
                        get(
                                buildUrlForGetOrDeleteRequest(BucketStorageTypeConstants.AWS_S3, bucketName, fileName)
                        )
                ).andExpect(
                        status().isOk()
                ).andExpect(
                        content().contentType(mediaType));
    }

    @Test
    public void getFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService).doGetFile(bucketName, fileName, BucketStorageType.AWS_S3);

        // Act and Assert
        mockMvc
                .perform(
                    get(
                            buildUrlForGetOrDeleteRequest(BucketStorageTypeConstants.AWS_S3, bucketName, fileName)
                    )
                ).andExpect(
                        status().is5xxServerError());
    }

    @Test
    public void uploadFile_shouldReturnOkStatus_whenCalledNormally() throws Exception {

        // Arrange
        Mockito.doNothing()
                .when(bucketStorageService)
                .doUploadMultipartFile(
                        eq(bucketName), eq(fileName), any(MultipartFile.class), eq(BucketStorageType.AWS_S3));

        // Act and Assert
        mockMvc
                .perform(
                        multipart(
                                buildUrlForPostRequest(BucketStorageTypeConstants.AWS_S3, bucketName)
                        ).part(requestPart)
                ).andExpect(
                        status().isOk());
    }

    @Test
    public void uploadFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService)
                .doUploadMultipartFile(
                        eq(bucketName), eq(fileName), any(MultipartFile.class), eq(BucketStorageType.AWS_S3));

        // Act and Assert
        mockMvc
                .perform(
                        multipart(
                                buildUrlForPostRequest(BucketStorageTypeConstants.AWS_S3, bucketName)
                        ).part(requestPart)
                ).andExpect(
                        status().is5xxServerError());
    }

    @Test
    public void deleteFile_shouldReturnOkStatus_whenCalledNormally() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(bucketStorageService)
                .doDeleteFile(bucketName, fileName, BucketStorageType.AWS_S3);

        // Act and Assert
        mockMvc
                .perform(
                        delete(
                                buildUrlForGetOrDeleteRequest(BucketStorageTypeConstants.AWS_S3, bucketName, fileName)
                        )
                ).andExpect(
                        status().isOk());
    }

    @Test
    public void deleteFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService)
                .doDeleteFile(bucketName, fileName, BucketStorageType.AWS_S3);

        // Act and Assert
        mockMvc
                .perform(
                        delete(
                                buildUrlForGetOrDeleteRequest(BucketStorageTypeConstants.AWS_S3, bucketName, fileName)
                        )
                ).andExpect(
                        status().is5xxServerError());
    }

    private String buildUrlForGetOrDeleteRequest(String storageProvider, String storageLocation, String fileName) {
        return baseUrl + "/storageProvider/" + storageProvider + "/storageLocation/" + storageLocation + "/fileName/" + fileName;
    }

    private String buildUrlForPostRequest(String storageProvider, String storageLocation) {
        return baseUrl + "/storageProvider/" + storageProvider + "/storageLocation/" + storageLocation;
    }
}