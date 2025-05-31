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
                            resource.lastModified(),
                            getFileDescription(resource.getFilename())
                        );
                    } catch (IOException e) {
                        return new FileInfo(resource.getFilename(), 0, 0, getFileDescription(resource.getFilename()));
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

    /**
     * 根据文件名获取文件用途描述
     */
    private String getFileDescription(String filename) {
        if (filename == null) return "未知";
        
        String name = filename.toLowerCase();
        
        // 精确匹配特定文件名
        if (name.equals("kbase_auto_reply_fixed.xlsx")) {
            return "固定自动回复";
        } else if (name.equals("kbase_auto_reply_keyword.xlsx")) {
            return "根据关键词自动回复";
        } else if (name.equals("kbase_faq.xlsx")) {
            return "常见问题模板";
        } else if (name.equals("kbase_quick_reply.xlsx")) {
            return "快捷回复模板";
        } else if (name.equals("kbase_replace.xlsx")) {
            return "知识库替换模板";
        } else if (name.equals("kbase_taboo.xlsx")) {
            return "敏感词模板";
        } else if (name.equals("kbase_text.xlsx")) {
            return "大模型纯文本导入模板";
        } else if (name.equals("kbase_transfer.xlsx")) {
            return "自动触发转人工模板";
        } else if (name.equals("team_member.xlsx")) {
            return "导入组织成员模板";
        }
        // 通用匹配规则（作为后备）
        else if (name.contains("kbase") && name.contains("auto") && name.contains("reply")) {
            return "知识库自动回复模板";
        } else if (name.contains("kbase") && name.contains("faq")) {
            return "知识库常见问题模板";
        } else if (name.contains("kbase") && name.contains("quick")) {
            return "知识库快捷回复模板";
        } else if (name.contains("kbase") && name.contains("taboo")) {
            return "知识库敏感词模板";
        } else if (name.contains("kbase") && name.contains("text")) {
            return "知识库文本导入模板";
        } else if (name.contains("kbase") && name.contains("transfer")) {
            return "知识库转人工模板";
        } else if (name.contains("kbase") && name.contains("replace")) {
            return "知识库替换模板";
        } else if (name.contains("team") && name.contains("member")) {
            return "团队成员导入模板";
        } else if (name.contains("keyword")) {
            return "关键词管理模板";
        } else if (name.contains("faq")) {
            return "常见问题模板";
        } else if (name.contains("user") || name.contains("member")) {
            return "用户信息导入模板";
        } else if (name.contains("agent") || name.contains("staff")) {
            return "客服人员管理模板";
        } else if (name.contains("category") || name.contains("cate")) {
            return "分类管理模板";
        } else if (name.contains("feedback")) {
            return "反馈信息导入模板";
        } else if (name.contains("message") || name.contains("chat")) {
            return "聊天记录导入模板";
        } else if (name.contains("statistics") || name.contains("stat")) {
            return "统计数据导出模板";
        } else if (name.contains("config") || name.contains("setting")) {
            return "配置信息模板";
        } else {
            return "数据导入/导出模板";
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