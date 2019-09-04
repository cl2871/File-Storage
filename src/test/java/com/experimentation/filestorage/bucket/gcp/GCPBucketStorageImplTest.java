package com.experimentation.filestorage.bucket.gcp;

import com.experimentation.filestorage.bucket.BucketStorageDTO;
import com.experimentation.filestorage.bucket.util.BucketStorageHelper;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GCPBucketStorageImpl.class})
public class GCPBucketStorageImplTest {

    @Autowired
    private GCPBucketStorageImpl gcpBucketStorageService;

    @MockBean
    private Storage storage;

    @MockBean
    private GCPBucketStorageHelper gcpBucketStorageHelper;

    @MockBean
    private BucketStorageHelper bucketStorageHelper;

    @MockBean
    private MultipartFile multipartFile;

    // Final classes
    private String bucketName;
    private String fileName;
    private BlobId blobId;
    private String contentType;
    private byte[] content;
    private boolean deletedTrue;
    private boolean deletedFalse;

    // Other fields to initialize
    private BucketStorageDTO bucketStorageDTO;
    private BlobInfo blobInfo;
    private Blob blob;

    @Before
    public void setUp() throws Exception {
        bucketName = "example";
        fileName = "test.txt";
        blobId = BlobId.of(bucketName, fileName);
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        content = "Example text".getBytes();

        bucketStorageDTO = new BucketStorageDTO(fileName, contentType, content);
        blobInfo = Mockito.mock(BlobInfo.class);
        blob = Mockito.mock(Blob.class);
        deletedTrue = true;
        deletedFalse = false;
    }

    @Test
    public void getFile_shouldReturnABucketStorageDTO_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(blob)
                .when(storage).get(blobId);
        Mockito.doReturn(contentType)
                .when(blob).getContentType();
        Mockito.doReturn(content)
                .when(blob).getContent();
        Mockito.doReturn(this.bucketStorageDTO)
                .when(bucketStorageHelper).createBucketStorageDTO(fileName, contentType, content);

        // Act
        BucketStorageDTO bucketStorageDTO = gcpBucketStorageService.getFile(bucketName, fileName);

        // Assert
        assertThat(bucketStorageDTO.getFileName()).isEqualTo(fileName);
        assertThat(bucketStorageDTO.getContentType()).isEqualTo(contentType);
        assertThat(bucketStorageDTO.getData()).isEqualTo(content);
        verifyStorageGetIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldThrowBucketStorageServiceException_whenNoFileIsFound() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);

        // Storage returning a null blob will cause a NullPointerException to be thrown when called on
        Mockito.doReturn(null)
                .when(storage).get(blobId);

        // Act
        gcpBucketStorageService.getFile(bucketName, fileName);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void getFile_shouldThrowBucketStorageServiceException_whenBaseServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doThrow(BaseServiceException.class)
                .when(storage).get(blobId);

        // Act
        gcpBucketStorageService.getFile(bucketName, fileName);
    }

    @Test
    public void uploadMultipartFile_shouldCompleteUpload_whenCalledWithBucketNameAndFileNameAndMultipartFile()
            throws IOException {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(contentType)
                .when(multipartFile).getContentType();
        Mockito.doReturn(blobInfo)
                .when(gcpBucketStorageHelper).createBlobInfo(blobId, contentType);
        Mockito.doReturn(content)
                .when(multipartFile).getBytes();
        Mockito.doReturn(blob)
                .when(storage).create(blobInfo, content);

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        verifyStorageCreateIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadFile_shouldThrowBucketStorageServiceException_whenIOExceptionIsThrown()
            throws IOException {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(contentType)
                .when(multipartFile).getContentType();
        Mockito.doReturn(blobInfo)
                .when(gcpBucketStorageHelper).createBlobInfo(blobId, contentType);
        Mockito.doThrow(IOException.class)
                .when(multipartFile).getBytes();

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void uploadFile_shouldThrowBucketStorageServiceException_whenBaseServiceExceptionIsThrown()
            throws IOException {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(contentType)
                .when(multipartFile).getContentType();
        Mockito.doReturn(blobInfo)
                .when(gcpBucketStorageHelper).createBlobInfo(blobId, contentType);
        Mockito.doReturn(content)
                .when(multipartFile).getBytes();
        Mockito.doThrow(BaseServiceException.class)
                .when(storage).create(blobInfo, content);

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);
    }

    @Test
    public void deleteFile_shouldDelete_whenCalledWithFileNameAndFileIsInBucket() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(deletedTrue)
                .when(storage).delete(blobId);

        // Act
        gcpBucketStorageService.deleteFile(bucketName, fileName);

        // Assert
        verifyStorageDeleteIsCalledOnce();
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldThrowBucketStorageServiceException_whenCalledWithFileNameAndFileIsNotInBucket() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(deletedFalse)
                .when(storage).delete(blobId);

        // Act
        gcpBucketStorageService.deleteFile(bucketName, fileName);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void deleteFile_shouldThrowBucketStorageServiceException_whenBaseServiceExceptionIsThrown() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doThrow(BaseServiceException.class)
                .when(storage).delete(blobId);

        // Act
        gcpBucketStorageService.deleteFile(bucketName, fileName);
    }

    private void verifyStorageGetIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .get(any(BlobId.class));
    }

    private void verifyStorageCreateIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .create(any(BlobInfo.class), any(byte[].class));
    }

    private void verifyStorageDeleteIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .delete(any(BlobId.class));
    }
}