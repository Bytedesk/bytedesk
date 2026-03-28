package com.bytedesk.core.ip.maxmind;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bytedesk.core.ip.IpProperties;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaxMindConfig {

    private final IpProperties ipProperties;

    private volatile DatabaseReader databaseReader;

    public DatabaseReader getDatabaseReader() {
        if (databaseReader == null) {
            synchronized (this) {
                if (databaseReader == null) {
                    databaseReader = loadDatabaseReader();
                }
            }
        }
        return databaseReader;
    }

    public boolean isAvailable() {
        return getDatabaseReader() != null;
    }

    private DatabaseReader loadDatabaseReader() {
        String dbFile = ipProperties.getMaxmind().getCityDbFile();
        try {
            ClassPathResource resource = new ClassPathResource(dbFile);
            if (!resource.exists()) {
                log.warn("MaxMind database file not found in classpath: {}", dbFile);
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                DatabaseReader.Builder builder = new DatabaseReader.Builder(inputStream)
                        .withCache(new CHMCache());
                List<String> locales = ipProperties.getMaxmind().getLocales();
                if (!CollectionUtils.isEmpty(locales)) {
                    builder.locales(locales);
                }
                DatabaseReader loadedDatabaseReader = builder.build();
                // log.info("Initialized MaxMind database reader with classpath resource: {}", dbFile);
                return loadedDatabaseReader;
            }
        } catch (Exception e) {
            log.error("Failed to initialize MaxMind database reader: {}", dbFile, e);
            return null;
        }
    }

    @PreDestroy
    public void destroy() {
        if (databaseReader != null) {
            try {
                databaseReader.close();
            } catch (Exception e) {
                log.warn("Failed to close MaxMind database reader", e);
            }
        }
    }
}