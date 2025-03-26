/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-26 15:47:42
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

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
    private String topic;
    private String threadUid;
    private String categoryUid;
    // 
    private String departmentUid;
    // 
    private Boolean assignmentAll;
    private UserProtobuf assignee;
    private UserProtobuf reporter;
    
    // 添加原始字符串字段存储JSON
    private String assigneeString;
    private String reporterString;
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

    public String getAssigneeJson() {
        if (assignee != null) {
            return assignee.toJson();
        } else if (StringUtils.hasText(assigneeString)) {
            try {
                assignee = JSON.parseObject(assigneeString, UserProtobuf.class);
                return assigneeString;
            } catch (Exception e) {
                log.error("解析assignee字符串失败: {}", assigneeString, e);
            }
        }
        return null;
    }
        
    public String getReporterJson() {
        if (reporter != null) {
            return reporter.toJson();
        } else if (StringUtils.hasText(reporterString)) {
            try {
                reporter = JSON.parseObject(reporterString, UserProtobuf.class);
                return reporterString;
            } catch (Exception e) {
                log.error("解析reporter字符串失败: {}", reporterString, e);
            }
        }
        return null;
    }
    
    // 便捷方法，用于前端直接传uid的情况
    public void setReporterUid(String uid) {
        if (StringUtils.hasText(uid)) {
            if (reporter == null) {
                reporter = new UserProtobuf();
            }
            reporter.setUid(uid);
        }
    }
    
    // 便捷方法，用于前端直接传uid的情况
    public void setAssigneeUid(String uid) {
        if (StringUtils.hasText(uid)) {
            if (assignee == null) {
                assignee = new UserProtobuf();
            }
            assignee.setUid(uid);
        }
    }
}