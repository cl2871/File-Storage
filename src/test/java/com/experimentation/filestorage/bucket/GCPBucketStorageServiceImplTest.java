package com.experimentation.filestorage.bucket;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GCPBucketStorageServiceImpl.class})
public class GCPBucketStorageServiceImplTest {

    @Autowired
    private GCPBucketStorageServiceImpl gcpBucketStorageService;

    @MockBean
    private Storage storage;

    @MockBean
    private GCPBucketStorageUtil gcpBucketStorageUtil;

    @MockBean
    private FileStorageUtil fileStorageUtil;

    @MockBean
    private Blob blob;

    @MockBean
    private MultipartFile multipartFile;

    // Final classes
    private String bucketName;
    private String fileName;
    private BlobId blobId;
    private String contentType;
    private byte[] content;

    // Other fields to initialize
    private FileStorageDTO fileStorageDTO;
    private BlobInfo blobInfo;

    @Before
    public void setUp() throws Exception {
        bucketName = "example";
        fileName = "test.txt";
        blobId = BlobId.of(bucketName, fileName);
        contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        content = "Example text".getBytes();

        fileStorageDTO = new FileStorageDTO(fileName, contentType, content);
        blobInfo = gcpBucketStorageUtil.createBlobInfo(blobId, contentType);
    }

    @Test
    public void getFile_shouldReturnAFileStorageDTO_whenCalledWithFileName() {

        // Arrange
        Mockito.doReturn(blobId)
                .when(gcpBucketStorageUtil).createBlobId(bucketName, fileName);
        Mockito.doReturn(blob)
                .when(storage).get(blobId);
        Mockito.doReturn(contentType)
                .when(blob).getContentType();
        Mockito.doReturn(content)
                .when(blob).getContent();
        Mockito.doReturn(fileStorageDTO)
                .when(fileStorageUtil).createFileStorageDTO(fileName, contentType, content);

        // Act
        FileStorageDTO fileStorageDTO = gcpBucketStorageService.getFile(bucketName, fileName);

        // Assert
        assertThat(fileStorageDTO.getFileName()).isEqualTo(fileName);
        assertThat(fileStorageDTO.getContentType()).isEqualTo(contentType);
        assertThat(fileStorageDTO.getData()).isEqualTo(content);
    }

    @Test
    public void uploadMultipartFile_shouldCompleteUpload_whenCalledWithBucketNameAndFileNameAndMultipartFile()
            throws IOException {

        Mockito.doReturn(blobId)
                .when(gcpBucketStorageUtil).createBlobId(bucketName, fileName);
        Mockito.doReturn(contentType)
                .when(multipartFile).getContentType();
        Mockito.doReturn(blobInfo)
                .when(gcpBucketStorageUtil).createBlobInfo(blobId, contentType);
        Mockito.doReturn(content)
                .when(multipartFile).getBytes();
        Mockito.doReturn(blob)
                .when(storage).create(blobInfo, content);

        gcpBucketStorageService.uploadMultipartFile(bucketName, fileName, multipartFile);
    }

    @Test
    public void deleteFile() {
    }
}