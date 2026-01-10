/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-07 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.process;

public class ProcessDemoConsts {

    // 请假审批流程
    public static final String LEAVE_APPROVAL_PROCESS_NAME = "请假审批流程";
    public static final String LEAVE_APPROVAL_PROCESS_KEY = "leaveApprovalProcess";
    public static final String LEAVE_APPROVAL_PROCESS_PATH = "processes/leave-approval-process.bpmn20.xml";

    // 报销审批流程
    public static final String REIMBURSEMENT_APPROVAL_PROCESS_NAME = "报销审批流程";
    public static final String REIMBURSEMENT_APPROVAL_PROCESS_KEY = "reimbursementApprovalProcess";
    public static final String REIMBURSEMENT_APPROVAL_PROCESS_PATH = "processes/reimbursement-approval-process.bpmn20.xml";

    // IT支持流程
    public static final String IT_SUPPORT_PROCESS_NAME = "IT支持流程";
    public static final String IT_SUPPORT_PROCESS_KEY = "itSupportProcess";
    public static final String IT_SUPPORT_PROCESS_PATH = "processes/it-support-process.bpmn20.xml";

    // 流程变量名常量 - 请假审批流程
    public static final String LEAVE_VARIABLE_APPLICANT_UID = "applicantUid";
    public static final String LEAVE_VARIABLE_LEAVE_DAYS = "leaveDays";
    public static final String LEAVE_VARIABLE_SUPERVISOR_GROUP_ID = "supervisorGroupId";
    public static final String LEAVE_VARIABLE_MANAGER_GROUP_ID = "managerGroupId";
    public static final String LEAVE_VARIABLE_HR_GROUP_ID = "hrGroupId";
    public static final String LEAVE_VARIABLE_SUPERVISOR_APPROVED = "supervisorApproved";
    public static final String LEAVE_VARIABLE_MANAGER_APPROVED = "managerApproved";

    // 流程变量名常量 - 报销审批流程
    public static final String REIMBURSEMENT_VARIABLE_APPLICANT_UID = "applicantUid";
    public static final String REIMBURSEMENT_VARIABLE_AMOUNT = "amount";
    public static final String REIMBURSEMENT_VARIABLE_FINANCE_GROUP_ID = "financeGroupId";
    public static final String REIMBURSEMENT_VARIABLE_MANAGER_GROUP_ID = "managerGroupId";
    public static final String REIMBURSEMENT_VARIABLE_CFO_GROUP_ID = "cfoGroupId";
    public static final String REIMBURSEMENT_VARIABLE_CEO_GROUP_ID = "ceoGroupId";
    public static final String REIMBURSEMENT_VARIABLE_FINANCE_APPROVED = "financeApproved";
    public static final String REIMBURSEMENT_VARIABLE_MANAGER_APPROVED = "managerApproved";
    public static final String REIMBURSEMENT_VARIABLE_CFO_APPROVED = "cfoApproved";
    public static final String REIMBURSEMENT_VARIABLE_CEO_APPROVED = "ceoApproved";

    // 流程变量名常量 - IT支持流程
    public static final String IT_SUPPORT_VARIABLE_REQUESTER_UID = "requesterUid";
    public static final String IT_SUPPORT_VARIABLE_CATEGORY = "category";
    public static final String IT_SUPPORT_VARIABLE_PRIORITY = "priority";
    public static final String IT_SUPPORT_VARIABLE_HARDWARE_GROUP_ID = "hardwareGroupId";
    public static final String IT_SUPPORT_VARIABLE_SOFTWARE_GROUP_ID = "softwareGroupId";
    public static final String IT_SUPPORT_VARIABLE_NETWORK_GROUP_ID = "networkGroupId";
    public static final String IT_SUPPORT_VARIABLE_IT_MANAGER_GROUP_ID = "itManagerGroupId";
    public static final String IT_SUPPORT_VARIABLE_SOLVED = "solved";
    public static final String IT_SUPPORT_VARIABLE_SLA_TIME_ISO = "slaTimeISO";

    // 分类常量 - IT支持流程
    public static final String CATEGORY_HARDWARE = "HARDWARE";
    public static final String CATEGORY_SOFTWARE = "SOFTWARE";
    public static final String CATEGORY_NETWORK = "NETWORK";
    public static final String CATEGORY_ACCOUNT = "ACCOUNT";

    // 优先级常量 - IT支持流程
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";
}
