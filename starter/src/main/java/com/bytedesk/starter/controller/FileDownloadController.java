/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-02
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
@RequestMapping("/files")
public class FileDownloadController {

    private final ResourceLoader resourceLoader;
    private static final String FILES_PATH = "classpath:static/files/";

    public FileDownloadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // 直接查看文件: http://127.0.0.1:9003/files/example.pdf
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        try {
            Resource resource = resourceLoader.getResource(FILES_PATH + filename);
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(getMediaTypeForFile(filename))
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 下载文件: http://127.0.0.1:9003/files/download/example.pdf
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = resourceLoader.getResource(FILES_PATH + filename);
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(getMediaTypeForFile(filename))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 根据文件扩展名确定 MediaType
    private MediaType getMediaTypeForFile(String filename) {
        if (filename.toLowerCase().endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (filename.toLowerCase().endsWith(".txt")) {
            return MediaType.TEXT_PLAIN;
        } else if (filename.toLowerCase().endsWith(".html") || filename.toLowerCase().endsWith(".htm")) {
            return MediaType.TEXT_HTML;
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.toLowerCase().endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (filename.toLowerCase().endsWith(".zip")) {
            return MediaType.parseMediaType("application/zip");
        } else if (filename.toLowerCase().endsWith(".doc") || filename.toLowerCase().endsWith(".docx")) {
            return MediaType.parseMediaType("application/msword");
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")) {
            return MediaType.parseMediaType("application/vnd.ms-excel");
        } else if (filename.toLowerCase().endsWith(".ppt") || filename.toLowerCase().endsWith(".pptx")) {
            return MediaType.parseMediaType("application/vnd.ms-powerpoint");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    // List all files: http://127.0.0.1:9003/files/download
    @GetMapping({"/download", "/download/"})
    public String listFiles(Model model) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
            Resource[] resources = resolver.getResources(FILES_PATH + "**/*.*");
            
            List<FileInfo> files = Arrays.stream(resources)
                .filter(resource -> !resource.getFilename().startsWith("."))
                .map(resource -> {
                    try {
                        return new FileInfo(
                            resource.getFilename(),
                            resource.contentLength(),
                            resource.lastModified(),
                            getFileDescription(resource.getFilename())
                        );
                    } catch (IOException e) {
                        return new FileInfo(resource.getFilename(), 0, 0, getFileDescription(resource.getFilename()));
                    }
                })
                .collect(Collectors.toList());
            
            model.addAttribute("files", files);
            return "files/list";
        } catch (IOException e) {
            model.addAttribute("error", "无法读取文件列表");
            return "error";
        }
    }

    /**
     * 根据文件名获取文件用途描述
     */
    private String getFileDescription(String filename) {
        if (filename == null) return "未知";
        
        String name = filename.toLowerCase();
        String extension = "";
        int lastDotPos = name.lastIndexOf('.');
        if (lastDotPos > 0) {
            extension = name.substring(lastDotPos + 1);
        }
        
        // 根据扩展名进行分类
        switch (extension) {
            case "pdf":
                return "PDF文档";
            case "doc":
            case "docx":
                return "Word文档";
            case "xls":
            case "xlsx":
                return "Excel表格";
            case "ppt":
            case "pptx":
                return "幻灯片";
            case "txt":
                return "文本文件";
            case "zip":
            case "rar":
            case "7z":
                return "压缩文件";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "图片";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
                return "视频";
            case "mp3":
            case "wav":
            case "ogg":
                return "音频";
            default:
                return "其他文件";
        }
    }

    // 文件信息类
    public static class FileInfo {
        private String name;
        private long size;
        private long lastModified;
        private String description;

        public FileInfo(String name, long size, long lastModified, String description) {
            this.name = name;
            this.size = size;
            this.lastModified = lastModified;
            this.description = description;
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

        public String getDescription() {
            return description;
        }
    }
}
