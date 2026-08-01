/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2022-03-10 14:41:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-21 22:28:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip.ip2region;

import java.io.InputStream;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.bytedesk.core.ip.IpProperties;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 * 
 * @author bytedesk.com on 2019/5/5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IP2RegionConfig {

    private final IpProperties ipProperties;

    private volatile Searcher searcher;

    public Searcher getSearcher() {
        if (searcher == null) {
            synchronized (this) {
                if (searcher == null) {
                    searcher = loadSearcher();
                }
            }
        }
        return searcher;
    }

    public boolean isAvailable() {
        return getSearcher() != null;
    }

    private Searcher loadSearcher() {
        String dbFile = ipProperties.getIp2region().getDbFile();
        try {
            ClassPathResource resource = new ClassPathResource(dbFile);
            if (!resource.exists()) {
                log.warn("ip2region database file not found in classpath: {}", dbFile);
                return null;
            }
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] cBuff = StreamUtils.copyToByteArray(inputStream);
                if (cBuff == null || cBuff.length == 0) {
                    log.error("Failed to read ip2region database content: {}", dbFile);
                    return null;
                }
                Searcher loadedSearcher = Searcher.newWithBuffer(cBuff);
                // log.info("Initialized ip2region searcher with classpath resource: {}", dbFile);
                return loadedSearcher;
            }
        } catch (Exception e) {
            log.error("Failed to initialize ip2region searcher: {}", dbFile, e);
            return null;
        }
    }

    @PreDestroy
    public void destroy() {
        if (searcher != null) {
            try {
                searcher.close();
            } catch (Exception e) {
                log.warn("Failed to close ip2region searcher", e);
            }
        }
    }
}
