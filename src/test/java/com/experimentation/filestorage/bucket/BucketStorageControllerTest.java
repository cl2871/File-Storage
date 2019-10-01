package com.experimentation.filestorage.bucket;

import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

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

    // Classes to have initialized values
    // Final classes
    private static String baseUrl;
    private static String bucketName;
    private static String fileName;
    private static MediaType mediaType;
    private static byte[] contentBytes;
    private static InputStream inputStream;
    private static UUID uuid;

    // Other classes
    private static BucketStorageDTO bucketStorageDTO;
    private static MockPart requestPart;

    @BeforeClass
    public static void setUp() {
        baseUrl = "/api/fileStorage";

        fileName = "example.txt";
        bucketName = "my-bucket";
        mediaType = MediaType.TEXT_PLAIN;
        uuid = UUID.randomUUID();

        contentBytes = "Coffee ipsum".getBytes();
        requestPart = new MockPart("file", fileName, contentBytes);
        inputStream = new ByteArrayInputStream(contentBytes);
        bucketStorageDTO = new BucketStorageDTO(fileName, MediaType.TEXT_PLAIN_VALUE, inputStream);
    }

    @After
    public void tearDown() {
        Mockito.reset(bucketStorageService);
    }

    @Test
    public void getFile_shouldReturnFile_whenCalledNormally() throws Exception {

        // Arrange
        Mockito.doReturn(bucketStorageDTO)
                .when(bucketStorageService).doGetFile(uuid);

        // Act and Assert
        mockMvc
                .perform(
                        get(
                                buildUrlForGetOrDeleteRequest(uuid)
                        )
                ).andExpect(
                        status().isOk()
                ).andExpect(
                        content().contentType(mediaType));

        verifyDoGetFileIsCalledOnce();
    }

    @Test
    public void getFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService).doGetFile(uuid);

        // Act and Assert
        mockMvc
                .perform(
                    get(
                            buildUrlForGetOrDeleteRequest(uuid)
                    )
                ).andExpect(
                        status().is5xxServerError());

        verifyDoGetFileIsCalledOnce();
    }

    @Test
    public void uploadFile_shouldReturnOkStatus_whenCalledNormally() throws Exception {

        // Arrange
        Mockito.doReturn(uuid)
                .when(bucketStorageService)
                .doUploadMultipartFile(eq(bucketName), eq(fileName), any(MultipartFile.class));

        // Act and Assert
        mockMvc
                .perform(
                        multipart(
                                buildUrlForPostRequest(bucketName)
                        ).part(requestPart)
                ).andExpect(
                        status().isOk()
                ).andExpect(
                        content().string(uuid.toString()));

        verifyDoUploadMultipartFileIsCalledOnce();
    }

    @Test
    public void uploadFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService)
                .doUploadMultipartFile(eq(bucketName), eq(fileName), any(MultipartFile.class));

        // Act and Assert
        mockMvc
                .perform(
                        multipart(
                                buildUrlForPostRequest(bucketName)
                        ).part(requestPart)
                ).andExpect(
                        status().is5xxServerError());

        verifyDoUploadMultipartFileIsCalledOnce();
    }

    @Test
    public void deleteFile_shouldReturnOkStatus_whenCalledNormally() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(bucketStorageService)
                .doDeleteFile(uuid);

        // Act and Assert
        mockMvc
                .perform(
                        delete(
                                buildUrlForGetOrDeleteRequest(uuid)
                        )
                ).andExpect(
                        status().isOk());

        verifyDoDeleteFileIsCalledOnce();
    }

    @Test
    public void deleteFile_shouldReturn500Status_whenBucketStorageServiceExceptionIsThrown() throws Exception {

        // Arrange
        Mockito.doThrow(BucketStorageServiceException.class)
                .when(bucketStorageService)
                .doDeleteFile(uuid);

        // Act and Assert
        mockMvc
                .perform(
                        delete(
                                buildUrlForGetOrDeleteRequest(uuid)
                        )
                ).andExpect(
                        status().is5xxServerError());

        verifyDoDeleteFileIsCalledOnce();
    }

    private void verifyDoGetFileIsCalledOnce() {
        Mockito.verify(bucketStorageService, VerificationModeFactory.times(1))
                .doGetFile(uuid);
    }

    private void verifyDoUploadMultipartFileIsCalledOnce() {
        Mockito.verify(bucketStorageService, VerificationModeFactory.times(1))
                .doUploadMultipartFile(eq(bucketName), eq(fileName), any(MultipartFile.class));
    }

    private void verifyDoDeleteFileIsCalledOnce() {
        Mockito.verify(bucketStorageService, VerificationModeFactory.times(1))
                .doDeleteFile(uuid);
    }

    private String buildUrlForGetOrDeleteRequest(UUID uuid) {
        return baseUrl + "/" + uuid;
    }

    private String buildUrlForPostRequest(String bucketName) {
        return baseUrl + "/" + bucketName;
    }
}