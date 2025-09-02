/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-02 09:55:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow;

/**
 * 工作流类型枚举
 * 根据业内实践分类
 * 
 * 各分类的典型应用场景
 *   CHATBOT: 电商客服机器人、智能助手、FAQ自动回复
 *   SALES: CRM系统、销售线索管理、客户关系维护
 *   MARKETING: 自动化营销、用户触达、转化率优化
 *   ONBOARDING: SaaS产品用户引导、新员工培训
 *   SUPPORT: IT支持、产品技术支持、故障处理
 *   APPROVAL: 企业OA系统、流程审批、权限管理
 *   INTEGRATION: 企业系统集成、数据中台、API管理
 *   ANALYTICS: 商业智能、用户行为分析、决策支持
 *   WORKFLOW: 一般工作流
 */
public enum WorkflowTypeEnum {
    
    /**
     * 聊天机器人
     * 处理自动回复、智能问答、意图识别等AI对话流程
     */
    CHATBOT,
    
    /**
     * 销售流程
     * 处理销售线索管理、客户跟进、销售漏斗等
     */
    SALES,
    
    /**
     * 营销活动
     * 处理邮件营销、短信营销、社交媒体营销等自动化流程
     */
    MARKETING,
    
    /**
     * 用户引导
     * 处理新用户注册、产品使用引导、用户激活等
     */
    ONBOARDING,
    
    /**
     * 技术支持
     * 处理故障报修、技术咨询、远程协助等专业支持流程
     */
    SUPPORT,
    
    /**
     * 审批流程
     * 处理请假申请、采购审批、合同审批等企业内部流程
     */
    APPROVAL,
    
    /**
     * 系统集成
     * 处理数据同步、API调用、事件触发等第三方系统集成
     */
    INTEGRATION,
    
    /**
     * 数据分析
     * 处理用户行为分析、业务报表、数据挖掘等分析流程
     */
    ANALYTICS,
    
    /**
     * 一般工作流
     * 适用于各种通用业务流程
     */
    WORKFLOW
}
