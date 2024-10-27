/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:16:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.article;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/article")
@AllArgsConstructor
public class ArticleController extends BaseController<ArticleRequest> {

    private final ArticleService articleService;

    @Override
    public ResponseEntity<?> queryByOrg(ArticleRequest request) {

        Page<ArticleResponse> page = articleService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(ArticleRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'query'");
    }

    @Override
    public ResponseEntity<?> create(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleService.create(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Override
    public ResponseEntity<?> update(@RequestBody ArticleRequest request) {

        ArticleResponse article = articleService.update(request);

        return ResponseEntity.ok(JsonResult.success(article));
    }

    @Override
    public ResponseEntity<?> delete(@RequestBody ArticleRequest request) {

        articleService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

}
