package com.bytedesk.disk.archive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties("bytedesk.disk.archive")
public class DiskArchiveSettings {
    private String privateDirectory = "";
    private long maxFileSize = 20L * 1024 * 1024;
    private long maxFilesPerOrgModule = 100000;

    @jakarta.annotation.PostConstruct
    void validate() {
        if (maxFileSize < 1 || maxFilesPerOrgModule < 1) {
            throw new IllegalArgumentException("Invalid disk archive upload limits");
        }
        if (StringUtils.hasText(privateDirectory)) {
            java.nio.file.Path.of(privateDirectory);
        }
    }

    public String getPrivateDirectory() { return privateDirectory; }

    public void setPrivateDirectory(String privateDirectory) { this.privateDirectory = privateDirectory; }

    public long getMaxFileSize() { return maxFileSize; }

    public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }

    public long getMaxFilesPerOrgModule() { return maxFilesPerOrgModule; }

    public void setMaxFilesPerOrgModule(long maxFilesPerOrgModule) { this.maxFilesPerOrgModule = maxFilesPerOrgModule; }
}
