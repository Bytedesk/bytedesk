/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2022-03-10 14:41:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-05 14:32:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * https://github.com/lionsoul2014/ip2region/blob/master/binding/java/ReadMe.md
 * 
 * @author bytedesk.com on 2019/5/5
 */
@Slf4j
@Configuration
public class IP2RegionConfig {

    @Bean
    public Searcher searcher() {
        String dbPath = IP2RegionConfig.class.getClassLoader().getResource("ip2region.xdb").getPath();
        log.info("ip2region path {}", dbPath);

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff = null;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load content from `%s`: %s\n", dbPath, e);
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
        }

        return searcher;
    }

    
}
