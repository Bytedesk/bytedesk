/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 09:37:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form;

/**
 * 表单类型枚举
 * 支持各种业务场景的自定义表单类型，可插入快捷菜单
 */
public enum FormTypeEnum {
    
    // ========== 基础表单类型 ==========
    /**
     * 内部工单表单 - 用于创建和处理客户服务工单
     */
    TICKET_INTERNAL,
    /**
     * 外部工单表单 - 用于访客提交的工单
     */
    TICKET_EXTERNAL,
    
    /**
     * 会话表单 - 用于客服会话中的信息收集
     */
    THREAD,
    
    /**
     * 流程表单 - 用于工作流程中的表单节点
     */
    FLOW,
    
    // ========== 客户服务表单 ==========

    /**
     * 询前问卷表单 - 用于客户咨询前的信息收集
     */
    PRE_SALES_QUESTIONNAIRE,

    /**
     * 客户留言表单 - 客户留言和咨询
     */
    MESSAGE_LEAVE,
    
    /**
     * 客户投诉表单 - 处理客户投诉和纠纷
     */
    COMPLAINT,
    
    /**
     * 意见反馈表单 - 收集用户意见和建议
     */
    FEEDBACK,
    
    /**
     * 产品建议表单 - 收集产品改进建议
     */
    PRODUCT_SUGGESTION,
    
    /**
     * Bug报告表单 - 软件问题和缺陷报告
     */
    BUG_REPORT,
    
    // ========== 营销和销售表单 ==========
    /**
     * 预约试听表单 - 教育培训行业试听预约
     */
    APPOINTMENT_TRIAL,
    
    /**
     * 产品咨询表单 - 产品信息咨询和询价
     */
    PRODUCT_INQUIRY,
    
    /**
     * 服务预约表单 - 各类服务预约
     */
    SERVICE_APPOINTMENT,
    
    /**
     * 报价申请表单 - 产品或服务报价申请
     */
    QUOTE_REQUEST,
    
    /**
     * 演示申请表单 - 产品演示预约
     */
    DEMO_REQUEST,
    
    // ========== 人力资源表单 ==========
    /**
     * 求职申请表单 - 招聘和求职
     */
    JOB_APPLICATION,
    
    /**
     * 员工反馈表单 - 内部员工意见收集
     */
    EMPLOYEE_FEEDBACK,
    
    /**
     * 培训申请表单 - 员工培训需求
     */
    TRAINING_APPLICATION,
    
    // ========== 业务申请表单 ==========
    /**
     * 售后服务表单 - 售后服务申请
     */
    AFTER_SALES_SERVICE,
    
    /**
     * 退换货申请表单 - 商品退换货流程
     */
    RETURN_EXCHANGE,
    
    /**
     * 合作申请表单 - 商务合作意向
     */
    PARTNERSHIP_APPLICATION,
    
    /**
     * 入驻申请表单 - 平台入驻申请
     */
    MERCHANT_APPLICATION,
    
    // ========== 调研和评估表单 ==========
    /**
     * 客户满意度调研表单 - 服务质量评估
     */
    SATISFACTION_SURVEY,
    
    /**
     * 市场调研表单 - 市场调查和数据收集
     */
    MARKET_RESEARCH,
    
    /**
     * 需求调研表单 - 用户需求分析
     */
    REQUIREMENT_SURVEY,
    
    // ========== 技术支持表单 ==========
    /**
     * 技术支持表单 - 技术问题求助
     */
    TECHNICAL_SUPPORT,
    
    /**
     * 系统故障报告表单 - 系统问题报告
     */
    SYSTEM_FAULT_REPORT,
    
    /**
     * 功能需求表单 - 新功能需求提交
     */
    FEATURE_REQUEST,
    
    // ========== 通用表单 ==========
    /**
     * 自定义表单 - 用户自定义的其他类型表单
     */
    CUSTOM,
    
    /**
     * 通用表单 - 通用目的表单
     */
    GENERAL;
    
    /**
     * 根据字符串查找对应的枚举常量
     * @param value 字符串值
     * @return 对应的枚举常量，找不到时返回GENERAL
     */
    public static FormTypeEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return GENERAL;
        }
        
        for (FormTypeEnum type : FormTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return GENERAL; // 默认返回通用类型
    }
    
    /**
     * 检查是否为客户服务相关表单
     * @return true if it's a customer service form
     */
    public boolean isCustomerServiceForm() {
        return this == MESSAGE_LEAVE || 
               this == COMPLAINT || 
               this == FEEDBACK || 
               this == AFTER_SALES_SERVICE ||
               this == TECHNICAL_SUPPORT;
    }
    
    /**
     * 检查是否为营销销售相关表单
     * @return true if it's a marketing/sales form
     */
    public boolean isMarketingForm() {
        return this == APPOINTMENT_TRIAL || 
               this == PRODUCT_INQUIRY || 
               this == SERVICE_APPOINTMENT ||
               this == QUOTE_REQUEST ||
               this == DEMO_REQUEST;
    }
    
    /**
     * 检查是否为调研评估相关表单
     * @return true if it's a survey/research form
     */
    public boolean isSurveyForm() {
        return this == SATISFACTION_SURVEY || 
               this == MARKET_RESEARCH || 
               this == REQUIREMENT_SURVEY;
    }
}
