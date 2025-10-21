package com.bytedesk.call.xmlcurl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(XmlCurlProperties.class)
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch.xmlcurl", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class XmlCurlAutoConfiguration {

    private final XmlCurlProperties properties;

    @Bean
    public FilterRegistrationBean<XmlCurlAuthFilter> xmlCurlAuthFilterRegistration(XmlCurlService xmlCurlService) {
        FilterRegistrationBean<XmlCurlAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XmlCurlAuthFilter(properties, xmlCurlService));
        registration.addUrlPatterns(properties.getPathPrefix(), properties.getPathPrefix() + "/*");
        registration.setName("xmlCurlAuthFilter");
        registration.setOrder(0);
        return registration;
    }
}
