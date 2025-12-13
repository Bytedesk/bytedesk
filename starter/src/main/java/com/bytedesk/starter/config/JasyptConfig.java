package com.bytedesk.starter.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.bytedesk.starter.config.properties.JasyptSupportProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Central Jasypt setup so that encrypted values in properties files are
 * resolved automatically at startup.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JasyptSupportProperties.class)
public class JasyptConfig {

    private final Environment environment;

    @Bean(name = "jasyptStringEncryptor")
    @Primary
    public StringEncryptor jasyptStringEncryptor(JasyptSupportProperties properties) {
        String password = resolvePassword(properties);
        if (!StringUtils.hasText(password)) {
            String message = "Missing Jasypt password. Set JASYPT_ENCRYPTOR_PASSWORD or bytedesk.security.jasypt.password.";
            if (properties.isFailOnMissingPassword()) {
                throw new IllegalStateException(message);
            }
            log.warn("{} Property values wrapped with ENC() will not be decrypted.", message);
            return new NoOpStringEncryptor();
        }

        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(properties.getAlgorithm());
        config.setStringOutputType(properties.getStringOutputType());
        config.setIvGeneratorClassName(properties.getIvGeneratorClassName());
        config.setSaltGeneratorClassName(properties.getSaltGeneratorClassName());
        config.setKeyObtentionIterations(String.valueOf(properties.getKeyObtentionIterations()));
        config.setPoolSize(String.valueOf(properties.getPoolSize()));
        if (StringUtils.hasText(properties.getProviderClassName())) {
            config.setProviderClassName(properties.getProviderClassName());
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }

    private String resolvePassword(JasyptSupportProperties properties) {
        if (StringUtils.hasText(properties.getPassword())) {
            return properties.getPassword();
        }
        String propertyPassword = environment.getProperty("jasypt.encryptor.password");
        if (StringUtils.hasText(propertyPassword)) {
            return propertyPassword;
        }
        return environment.getProperty("JASYPT_ENCRYPTOR_PASSWORD");
    }

    private static final class NoOpStringEncryptor implements StringEncryptor {
        @Override
        public String encrypt(String message) {
            return message;
        }

        @Override
        public String decrypt(String encryptedMessage) {
            return encryptedMessage;
        }
    }
}
