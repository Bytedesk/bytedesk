/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2022-03-10 14:41:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 21:50:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config;

import java.io.InputStream;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 * 
 * @author bytedesk.com on 2019/5/5
 */
@Slf4j
@Configuration
public class IP2RegionConfig {

    private static final String DB_FILE_NAME = "ip2region.xdb";

    @Bean
    public Searcher searcher() {
        try {
            // 1. 直接从 classpath 读取文件到内存
            ClassPathResource resource = new ClassPathResource(DB_FILE_NAME);
            try (InputStream inputStream = resource.getInputStream()) {
                // 2. 将文件内容读入字节数组
                byte[] cBuff = StreamUtils.copyToByteArray(inputStream);
                if (cBuff == null || cBuff.length == 0) {
                    log.error("Failed to read ip2region.xdb content");
                    return null;
                }
                
                // 3. 创建查询对象
                return Searcher.newWithBuffer(cBuff);
            }
        } catch (Exception e) {
            log.error("Failed to initialize ip2region searcher: {}", e.getMessage());
            return null;
        }
    }
}
