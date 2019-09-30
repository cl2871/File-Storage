package com.experimentation.filestorage.bucket.gcp;

import com.experimentation.filestorage.bucket.BucketStorageDTO;
import com.experimentation.filestorage.bucket.util.BucketStorageHelper;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import com.google.cloud.BaseServiceException;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit test for the GCPBucketStorageImpl class
 */
public class GCPBucketStorageImplTest {

    // Class under test
    private static GCPBucketStorageImpl gcpBucketStorageService;

    // Mocks
    private static Storage storage;
    private static GCPBucketStorageHelper gcpBucketStorageHelper;
    private static BucketStorageHelper bucketStorageHelper;
    private static MultipartFile multipartFile;
    private static WriteChannel writer;

    // Final classes to have values initialized
    private static String bucketName;
    private static String fileName;
    private static BlobId blobId;
    private static String contentType;
    private static InputStream content;
    private static boolean deletedTrue;
    private static boolean deletedFalse;

    // Other fields to initialize
    private static BucketStorageDTO bucketStorageDTO;
    private static BlobInfo blobInfo;
    private static Blob blob;

    @BeforeClass
    public static void setUp() {

        // Inject mocks into the gcpBucketStorageService
        storage = Mockito.mock(Storage.class);
        gcpBucketStorageHelper = Mockito.mock(GCPBucketStorageHelper.class);
        bucketStorageHelper = Mockito.mock(BucketStorageHelper.class);
        gcpBucketStorageService = new GCPBucketStorageImpl(storage, gcpBucketStorageHelper, bucketStorageHelper);

        // Mocks
        multipartFile = Mockito.mock(MultipartFile.class);
        writer = Mockito.mock(WriteChannel.class);

        // Initialize values for testing
        bucketName = "example";
        fileName = "test.txt";
        blobId = BlobId.of(bucketName, fileName);
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        content = new ByteArrayInputStream("Example text".getBytes(StandardCharsets.UTF_8));
        deletedTrue = true;
        deletedFalse = false;

        bucketStorageDTO = new BucketStorageDTO(fileName, contentType, content);
        blobInfo = Mockito.mock(BlobInfo.class);
        blob = Mockito.mock(Blob.class);
    }

    @After
    public void tearDown() {
        // Reset following static mocks to ensure verify methods are correct in each test
        Mockito.reset(storage);
        Mockito.reset(gcpBucketStorageHelper);
    }

    @Test
    public void getFile_shouldReturnABucketStorageDTO_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageHelper).createBlobId(bucketName, fileName);
        Mockito.doReturn(blob)
                .when(storage).get(blobId);
        Mockito.doReturn(content)
                .when(gcpBucketStorageHelper).createInputStreamFromBlob(blob);
        Mockito.doReturn(contentType)
                .when(blob).getContentType();
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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
        Mockito.doReturn(writer)
                .when(storage).writer(blobInfo);
        Mockito.doNothing()
                .when(gcpBucketStorageHelper).copyInputStreamToOutputStreamFromWriteChannel(multipartFile, writer);

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        verifyStorageWriterIsCalledOnce();
        verifyGCPBucketStorageHelperCopyInputStreamToOutputStreamFromWriteChannelIsCalledOnce();
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
        Mockito.doReturn(writer)
                .when(storage).writer(blobInfo);
        Mockito.doThrow(IOException.class)
                .when(gcpBucketStorageHelper).copyInputStreamToOutputStreamFromWriteChannel(multipartFile, writer);

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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
        Mockito.doReturn(writer)
                .when(storage).writer(blobInfo);
        Mockito.doThrow(BaseServiceException.class)
                .when(gcpBucketStorageHelper).copyInputStreamToOutputStreamFromWriteChannel(multipartFile, writer);

        // Act
        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
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

        // Assert
        // Test annotation expects a BucketStorageServiceException to be thrown
    }

    private void verifyStorageGetIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .get(any(BlobId.class));
    }

    private void verifyStorageWriterIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .writer(any(BlobInfo.class));
    }

    private void verifyStorageDeleteIsCalledOnce() {
        Mockito.verify(storage, VerificationModeFactory.times(1))
                .delete(any(BlobId.class));
    }

    private void verifyGCPBucketStorageHelperCopyInputStreamToOutputStreamFromWriteChannelIsCalledOnce()
            throws IOException {
        Mockito.verify(gcpBucketStorageHelper, VerificationModeFactory.times(1))
                .copyInputStreamToOutputStreamFromWriteChannel(any(MultipartFile.class), any(WriteChannel.class));
    }
}