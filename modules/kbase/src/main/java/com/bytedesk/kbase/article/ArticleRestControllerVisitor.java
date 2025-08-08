/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 17:28:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/article")
@AllArgsConstructor
@Tag(name = "文章匿名管理", description = "文章匿名相关接口")
public class ArticleRestControllerVisitor {

    private final ArticleRestService articleRestService;

    @RequestMapping("/search")
    @Operation(summary = "搜索文章", description = "访客搜索文章")
    public ResponseEntity<?> searchKb(ArticleRequest request) {
        
        Page<ArticleResponse> page = articleRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }
    

}
