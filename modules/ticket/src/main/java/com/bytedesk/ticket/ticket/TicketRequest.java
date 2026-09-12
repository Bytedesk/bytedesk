/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:03:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.black.BlacklistCheckable;
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
public class TicketRequest extends BaseRequest implements BlacklistCheckable {

    private static final long serialVersionUID = 1L;

    private String title;
    // 问题描述
    private String description;
    // 联系称呼
    private String contactName;
    // 联系手机号
    private String phone;
    // 联系邮箱
    private String email;
    // 联系微信
    private String wechat;
    // 
    private String ticketNumber;
    // 
    private String status;
    private String priority;
    // 
    private String threadTopic;
    private String threadUid;
    // 内部工单：关联的客服会话（非工单会话）
    private String visitorThreadUid;
    // 内部工单：关联的客服会话topic（非工单会话）
    private String visitorThreadTopic;
    // 
    private String categoryUid;
    private String workgroupUid;
    private String departmentUid;
    private Boolean assignmentAll;

    // 工单处理人
    private String assigneeUid; // 方便get查询
    private UserProtobuf assignee; // 方便post存储

    // 工单提出者
    private String reporterUid; // 方便get查询
    private UserProtobuf reporter; // 方便post存储

    // 前端根据创建时间范围查询
    private String createdAtStart;
    private String createdAtEnd;
    private Set<String> uploadUids;

    // 
    private String ticketSettingsUid;
    // 流程实例ID
    private String processInstanceId;
    // 流程定义实体UID
    private String processEntityUid;

    // 客户验证
    private Boolean verified;
    // SLA 状态过滤：RUNNING/PAUSED/WARNED/BREACHED/COMPLETED/CANCELED
    private String slaStatus;
    // 自定义表单 json schema
    private String schema;

    private Boolean visibilityRestricted;
    private String visibilityMode;
    private String visibilityCurrentUserUid;
    // 当前用户对应的组织成员 uid：工单 userUid（报告人）/assignee JSON 中存储的是 member uid，
    // 与 user uid 不同，始终可见判定需双口径匹配（user uid + member uid）
    private String visibilityCurrentUserMemberUid;
    private String visibilityCurrentUserDepartmentUid;
    private Boolean visibilityOrgAdmin;
    @Builder.Default
    private List<String> visibilityRestrictedCategoryUids = new ArrayList<>();
    // 按分类 + 指定部门限制可见范围：categoryUid -> 允许查看该分类工单的部门 uid 列表
    @Builder.Default
    private Map<String, List<String>> visibilityRestrictedCategoryDepartmentUids = new HashMap<>();
    // 顶层指定部门限制可见范围：允许查看当前设置对应工单的部门 uid 列表
    @Builder.Default
    private List<String> visibilityAllowedDepartmentUids = new ArrayList<>();
    // G2 组合可见性上下文：无 type 混合列表查询时按工单类型分别应用 INTERNAL/EXTERNAL 设置；
    // 显式传 type 或 ticketSettingsUid 时为 null（走上方单类型字段路径）
    private TicketVisibilityQueryContext visibilityInternalContext;
    private TicketVisibilityQueryContext visibilityExternalContext;

    // ===================== 工作流增强操作参数（Flowable） =====================
    // 当一个流程实例可能存在多个并行任务（如会签/或签）时，建议明确传入 taskId
    private String taskId;
    // 委托：将当前任务委托给指定处理人（member uid）
    private String delegateUid;
    // 指派/转派：目标处理人（member uid）
    private String targetAssigneeUid;
    // 跨部门转派：目标部门 uid
    private String targetDepartmentUid;
    // 抄送：知会人员列表（member uid）
    private Set<String> ccUids;
    // 加签：新增候选审批人（member uid），最小实现为追加候选人
    private Set<String> addSignUids;
    // 退回：从当前节点退回到指定 activityId（BPMN 节点 id）
    private String rollbackToActivityId;
    // 退回：指定从哪个 activityId 退回（可选，默认取当前任务 definitionKey）
    private String rollbackFromActivityId;
    // 撤销/退回/加签/抄送等操作原因
    private String reason;
    // 处理意见/暂存说明/关单说明
    private String processComment;
    // 统一工作流动作：claim/complete/delegate/rollback/revoke 等
    private String actionKey;
    // 完成任务时传入的流程变量
    private Map<String, Object> variables;

    public String getAssigneeJson() {
        if (assignee == null) {
            return null;
        }
        return assignee.toJson();

    }

    public String getReporterJson() {
        if (reporter == null) {
            return null;
        }
        return reporter.toJson();
    }

    /**
     * 黑名单校验：访客创建/操作工单时，操作者为工单报告人（reporter）
     */
    @Override
    public String getBlacklistOperatorUid() {
        if (StringUtils.hasText(reporterUid)) {
            return reporterUid;
        }
        if (reporter != null && StringUtils.hasText(reporter.getUid())) {
            return reporter.getUid();
        }
        return getUserUid();
    }
} 