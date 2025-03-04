/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-04 21:14:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 21:51:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/excel")
public class ExcelDownloadController {

    private final ResourceLoader resourceLoader;
    private static final String EXCEL_PATH = "classpath:static/excel/";

    public ExcelDownloadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // open: http://127.0.0.1:9003/excel/kbase_auto_reply_keyword.xlsx
    // download: http://127.0.0.1:9003/excel/download/kbase_auto_reply_keyword.xlsx
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = resourceLoader.getResource(EXCEL_PATH + filename);
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // List all excel files: http://127.0.0.1:9003/excel/download
    @GetMapping({"/download", "/download/"})
    public String listFiles(Model model) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            Resource[] resources = resolver.getResources(EXCEL_PATH + "**/*.xlsx");
            
            List<FileInfo> files = Arrays.stream(resources)
                .filter(resource -> !resource.getFilename().startsWith("."))
                .map(resource -> {
                    try {
                        return new FileInfo(
                            resource.getFilename(),
                            resource.contentLength(),
                            resource.lastModified()
                        );
                    } catch (IOException e) {
                        return new FileInfo(resource.getFilename(), 0, 0);
                    }
                })
                .collect(Collectors.toList());
            
            model.addAttribute("files", files);
            return "excel/list";
        } catch (IOException e) {
            model.addAttribute("error", "无法读取文件列表");
            return "error";
        }
    }

    // 文件信息类
    public static class FileInfo {
        private String name;
        private long size;
        private long lastModified;

        public FileInfo(String name, long size, long lastModified) {
            this.name = name;
            this.size = size;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public long getSize() {
            return size;
        }

        public long getLastModified() {
            return lastModified;
        }
    }
} 