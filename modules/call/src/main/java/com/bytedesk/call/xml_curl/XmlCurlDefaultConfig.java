package com.bytedesk.call.xml_curl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.call.xml_curl.impl.DefaultXmlCurlService;

@Configuration(proxyBeanMethods = false)
public class XmlCurlDefaultConfig {

    @Bean
    @ConditionalOnMissingBean(XmlCurlService.class)
    public XmlCurlService xmlCurlService() {
        return new DefaultXmlCurlService();
    }
}