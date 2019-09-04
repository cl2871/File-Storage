package com.experimentation.filestorage.config;

import com.experimentation.filestorage.bucket.BucketStorageFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketStorageConfig {

    /**
     * Configure the ServiceLocatorFactoryBean to utilize the BucketStorageFactory as the service locator interface
     * @return
     */
    @Bean(name = "bucketStorageFactory")
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(BucketStorageFactory.class);
        return factoryBean;
    }
}
