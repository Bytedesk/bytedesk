/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-22 16:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 22:49:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.ai.springai.service.SpringAIFullTextService;
import com.bytedesk.kbase.llm.qa.QaEntity;
import com.bytedesk.kbase.llm.qa.QaRestService;
import com.bytedesk.kbase.llm.qa.QaElastic;

import lombok.extern.slf4j.Slf4j;

/**
 * 全文搜索控制器
 */
@RestController
@RequestMapping(value = "/api/v1/search")
@Slf4j
public class SearchController {

    @Autowired
    private SpringAIFullTextService springAIFullTextService;
    
    @Autowired
    private QaRestService qaRestService;
    
    /**
     * 全文搜索QA: http://127.0.0.1:9003/api/v1/search/qa?query=你好&kbUid=xxxx&categoryUid=xxxx&orgUid=xxxx
     * @param query 搜索关键词
     * @param kbUid 知识库UID
     * @param categoryUid 分类UID
     * @param orgUid 组织UID
     * @return 搜索结果
     */
    @GetMapping("/qa")
    public Map<String, Object> searchQa(
            @RequestParam("query") String query,
            @RequestParam(value = "kbUid", required = false) String kbUid,
            @RequestParam(value = "categoryUid", required = false) String categoryUid,
            @RequestParam(value = "orgUid", required = false) String orgUid) {
        
        log.info("搜索QA: query={}, kbUid={}, categoryUid={}, orgUid={}", query, kbUid, categoryUid, orgUid);
        
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用全文搜索服务搜索QA - 现在返回的是QaElastic对象列表
            List<QaElastic> qaElasticList = springAIFullTextService.searchQa(query, kbUid, categoryUid, orgUid);
            
            // 根据QaElastic列表获取QA实体列表或者直接使用QaElastic列表
            List<QaEntity> qaList = new ArrayList<>();
            for (QaElastic qaElastic : qaElasticList) {
                qaRestService.findByUid(qaElastic.getUid()).ifPresent(qaList::add);
            }
            
            // 构建返回结果
            result.put("code", 200);
            result.put("message", "搜索成功");
            result.put("data", qaList);
            result.put("total", qaList.size());
            
        } catch (Exception e) {
            log.error("搜索QA失败: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("message", "搜索失败: " + e.getMessage());
        }
        
        return result;
    }
}
