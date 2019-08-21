package com.experimentation.filestorage.config;

import com.experimentation.filestorage.parser.JsonParser;
import com.experimentation.filestorage.parser.ParserFactory;
import com.experimentation.filestorage.parser.XMLParser;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ParserConfig {

    /**
     * Configure the ServiceLocatorFactoryBean to utilize the ParserFactory as the service locator interface
     * @return
     */
    @Bean(name = "parserFactory")
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(ParserFactory.class);
        return factoryBean;
    }
}
