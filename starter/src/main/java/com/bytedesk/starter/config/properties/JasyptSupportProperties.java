package com.bytedesk.starter.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Centralized settings for Jasypt encryption so that we never have to commit
 * secrets to the repository. Passwords should be provided through environment
 * variables or external configuration.
 */
@Data
@ConfigurationProperties(prefix = "bytedesk.security.jasypt")
public class JasyptSupportProperties {

    /**
     * Optional password provided through a secure channel. Prefer the
     * JASYPT_ENCRYPTOR_PASSWORD environment variable instead of setting it here.
     */
    private String password;
    // Encryption algorithm to use for Jasypt
    private String algorithm = "PBEWITHHMACSHA512ANDAES_256";

    private String stringOutputType = "base64";

    private String ivGeneratorClassName = "org.jasypt.iv.RandomIvGenerator";

    private String saltGeneratorClassName = "org.jasypt.salt.RandomSaltGenerator";

    private int poolSize = 1;

    private int keyObtentionIterations = 1000;

    /**
     * Fail fast when the password is missing. Set to false only when running
     * locally without encrypted values.
     */
    private boolean failOnMissingPassword = false;

    /** Optional provider class name (e.g. org.bouncycastle.jce.provider.BouncyCastleProvider). */
    private String providerClassName;
}
