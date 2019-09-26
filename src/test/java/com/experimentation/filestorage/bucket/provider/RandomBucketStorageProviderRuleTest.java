package com.experimentation.filestorage.bucket.provider;

import com.experimentation.filestorage.bucket.BucketStorageType;
import com.experimentation.filestorage.bucket.BucketStorageTypeConstants;
import com.experimentation.filestorage.bucket.util.BucketStorageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RandomBucketStorageProviderRule.class})
public class RandomBucketStorageProviderRuleTest {

    @Autowired
    private RandomBucketStorageProviderRule randomBucketStorageProviderRule;

    @MockBean
    private ThreadLocalRandom threadLocalRandom;

    // final classes
    private String bucketStorageProviderValid;
    private String bucketStorageProviderInvalid;

    // other classes
    private BucketStorageType bucketStorageType;

    @Before
    public void setUp() {

        bucketStorageProviderValid = BucketStorageTypeConstants.AWS_S3;
        bucketStorageProviderInvalid = "FRUIT_PROVIDER";

        bucketStorageType = BucketStorageType.AWS_S3;
    }

    /**
     * Test relies on the BucketStorageType enums.
     * There should always be at least one BucketStorageType enum.
     */
    @Test
    public void chooseProvider_shouldReturnBucketStorageType_whenCalledWithNoArguments() {

        // Arrange
        // Use a spy to mock the getRandomNum() called in same class
        RandomBucketStorageProviderRule rule = new RandomBucketStorageProviderRule();
        RandomBucketStorageProviderRule spyRule = Mockito.spy(rule);

        BucketStorageType[] bucketStorageTypes = BucketStorageType.class.getEnumConstants();
        int bucketStorageTypeSize = bucketStorageTypes.length;

        Mockito.doReturn(0)
                .when(spyRule).getRandomNum(bucketStorageTypeSize);

        // Act
        BucketStorageType bucketStorageTypeReturned = spyRule.chooseProvider();

        // Assert
        assertThat(bucketStorageTypes[0]).isEqualTo(bucketStorageTypeReturned);
    }

    @Test
    public void chooseProvider_shouldReturnBucketStorageType_whenCalledWithACorrectArgument() {

        // Act
        BucketStorageType bucketStorageTypeReturned = randomBucketStorageProviderRule
                .chooseProvider(bucketStorageProviderValid);

        // Assert
        assertThat(bucketStorageType).isEqualTo(bucketStorageTypeReturned);
    }

    @Test(expected = BucketStorageServiceException.class)
    public void chooseProvider_shouldThrowBucketStorageServiceException_whenCalledWithAnIncorrectArgument() {

        // Act
        randomBucketStorageProviderRule.chooseProvider(bucketStorageProviderInvalid);

        // Assert
        // Test annotation expects a BucketStorageServiceException
    }
}