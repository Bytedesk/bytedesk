/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 11:37:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-30 22:12:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import lombok.experimental.UtilityClass;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class AIFileUtils {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocalFileContent {
        private String filename;
        private String content;
    }
    
    // public static List<LocalFileContent> getAllFiles() {
    //     List<LocalFileContent> fileContents = new ArrayList<>();
    //     try {
    //         ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    //         // 获取 aidemo/bytedesk 目录下所有的 .md 文件
    //         Resource[] resources = resolver.getResources("classpath:aidemo/bytedesk/**/*.md");           
    //         for (Resource resource : resources) {
    //             try {
    //                 // 读取文件内容
    //                 byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
    //                 String content = new String(bytes, StandardCharsets.UTF_8);
    //                 // 获取文件名
    //                 String filename = resource.getFilename();
    //                 log.info("Reading file: {}", filename);         
    //                 fileContents.add(LocalFileContent.builder().filename(filename).content(content).build());
    //             } catch (IOException e) {
    //                 log.error("Error reading file: " + resource.getFilename(), e);
    //             }
    //         }
    //     } catch (IOException e) {
    //         log.error("Error getting resources", e);
    //     }
    //     return fileContents;
    // }

    // public static List<String> getAllFolders() {
    //     List<String> folders = new ArrayList<>();
    //     try {
    //         ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    //         // 获取 aidemo/bytedesk 目录下所有的文件夹
    //         Resource[] resources = resolver.getResources("classpath:aidemo/bytedesk/**/");
    //         for (Resource resource : resources) {
    //             String path = resource.getURL().getPath();
    //             // 从路径中提取文件夹名
    //             String folderName = path.substring(path.indexOf("aidemo/bytedesk"));
    //             log.info("Found folder: {}", folderName);
    //             folders.add(folderName);
    //         }
    //     } catch (IOException e) {
    //         log.error("Error getting folders", e);
    //     }
    //     return folders;
    // }

    /**
     * 判断是否为本地回环 HTTP(S) 地址（127.* 或 localhost）
     */
    public static boolean isLocalLoopbackHttpUrl(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            String protocol = u.getProtocol();
            if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
                return false;
            }
            if (host == null) return false;
            String h = host.trim().toLowerCase();
            return h.equals("localhost") || h.startsWith("127.");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 读取 HTTP(S) 资源并转为 Base64，带大小上限
     */
    public static String fetchHttpAsBase64(String url, int maxBytes) {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                log.warn("fetchHttpAsBase64 non-200: {} for url {}", code, url);
                return null;
            }
            try (InputStream in = conn.getInputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int total = 0;
                int n;
                while ((n = in.read(buf)) > 0) {
                    bos.write(buf, 0, n);
                    total += n;
                    if (total > maxBytes) {
                        log.warn("fetchHttpAsBase64 exceeded maxBytes ({}), url {}", maxBytes, url);
                        return null;
                    }
                }
                byte[] bytes = bos.toByteArray();
                if (bytes.length == 0) return null;
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            log.warn("fetchHttpAsBase64 error: {} for url {}", e.getMessage(), url);
            return null;
        } finally {
            if (conn != null) {
                try { conn.disconnect(); } catch (Exception ignore) {}
            }
        }
    }

}
