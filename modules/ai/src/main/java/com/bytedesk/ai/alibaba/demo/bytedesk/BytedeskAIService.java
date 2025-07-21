/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 11:37:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 07:53:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.bytedesk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.bytedesk.ai.alibaba.utils.FileContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BytedeskAIService {
    
    public List<FileContent> getAllFiles() {
        List<FileContent> fileContents = new ArrayList<>();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 获取 aidemo/bytedesk 目录下所有的 .md 文件
            Resource[] resources = resolver.getResources("classpath:aidemo/bytedesk/**/*.md");
            
            for (Resource resource : resources) {
                try {
                    // 读取文件内容
                    byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    
                    // 获取文件名
                    String filename = resource.getFilename();
                    log.info("Reading file: {}", filename);
                    
                    fileContents.add(new FileContent(filename, content));
                } catch (IOException e) {
                    log.error("Error reading file: " + resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            log.error("Error getting resources", e);
        }
        return fileContents;
    }

    public List<String> getAllFolders() {
        List<String> folders = new ArrayList<>();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 获取 aidemo/bytedesk 目录下所有的文件夹
            Resource[] resources = resolver.getResources("classpath:aidemo/bytedesk/**/");
            
            for (Resource resource : resources) {
                String path = resource.getURL().getPath();
                // 从路径中提取文件夹名
                String folderName = path.substring(path.indexOf("aidemo/bytedesk"));
                log.info("Found folder: {}", folderName);
                folders.add(folderName);
            }
        } catch (IOException e) {
            log.error("Error getting folders", e);
        }
        return folders;
    }

    
}
