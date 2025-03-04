/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 11:21:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.Set;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest extends BaseRequest {

    private String title;
    private String description;
    private String searchText;
    // 
    private String status;
    private String priority;
    // 
    private String type;
    // 
    private String serviceThreadTopic;
    private String threadUid;
    private String categoryUid;
    // 
    private String workgroupUid;
    // 
    private Boolean assignmentAll;
    private String assigneeUid;
    // private String reporterUid;
    private String reporter;  // 原始 JSON 字符串
    // 
    private String startDate;
    private String endDate;
    // 
    private Set<String> uploadUids;
    // 流程实例ID
    private String processInstanceId;
    // 流程定义实体UID
    private String processEntityUid;

    // 是否评价
    private Boolean rated;
    // 满意度评价
    private Integer rating;
    // 客户验证
    private Boolean verified;

    
        
    // 添加 getter 方法转换为 UserProtobuf
    public UserProtobuf getReporter() {
        if (StringUtils.hasText(reporter)) {
            try {
                // 处理可能的双重转义情况
                String jsonStr = reporter;
                if (reporter.startsWith("\"") && reporter.endsWith("\"")) {
                    jsonStr = reporter.substring(1, reporter.length() - 1)
                        .replace("\\\"", "\"");
                }
                return JSON.parseObject(jsonStr, UserProtobuf.class);
            } catch (Exception e) {
                log.error("Failed to parse reporter JSON: {}", reporter, e);
                return null;
            }
        }
        return null;
    }
} 