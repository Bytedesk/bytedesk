/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 10:54:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 07:53:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.bytedesk;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.alibaba.utils.FileContent;
import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/springai/demo/bytedesk")
@RequiredArgsConstructor
public class BytedeskAIController {

    private final BytedeskAIService springAIBytedeskService;
    
    @GetMapping("/chat")
    public String chat() {
        return "Hello, World!";
    }

    // 获取所有文件
    // http://127.0.0.1:9003/springai/demo/bytedesk/files
    @GetMapping("/files")
    public ResponseEntity<JsonResult<?>> getFiles() {

        List<FileContent> files = springAIBytedeskService.getAllFiles();
        
        return ResponseEntity.ok(JsonResult.success(files));
    }

    // 获取所有文件夹
    // http://127.0.0.1:9003/springai/demo/bytedesk/folders
    @GetMapping("/folders")
    public ResponseEntity<JsonResult<?>> getFolders() {

        List<String> folders = springAIBytedeskService.getAllFolders();
        
        return ResponseEntity.ok(JsonResult.success(folders));
    }
    
    
}
