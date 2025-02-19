<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 22:13:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-29 22:16:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# Flowable 7.1.0 数据库表说明

## 通用表 (ACT_GE_*)

- **ACT_GE_PROPERTY**: 系统属性表，存储整个流程引擎级别的属性
- **ACT_GE_BYTEARRAY**: 二进制数据表，存储流程定义图片和XML等二进制信息

## 流程定义表 (ACT_RE_*)

- **ACT_RE_DEPLOYMENT**: 部署单元信息表
- **ACT_RE_PROCDEF**: 流程定义表
- **ACT_RE_MODEL**: 流程设计模型部署表

## 运行时表 (ACT_RU_*)

- **ACT_RU_EXECUTION**: 运行时流程执行实例表
- **ACT_RU_TASK**: 运行时任务表
- **ACT_RU_VARIABLE**: 运行时变量表
- **ACT_RU_EVENT_SUBSCR**: 运行时事件表
- **ACT_RU_IDENTITYLINK**: 运行时用户关系表，存储任务节点与参与者的相关信息
- **ACT_RU_JOB**: 运行时作业表，存储异步执行的作业
- **ACT_RU_TIMER_JOB**: 运行时定时器作业表
- **ACT_RU_SUSPENDED_JOB**: 暂停作业表
- **ACT_RU_DEADLETTER_JOB**: 死信作业表
- **ACT_RU_HISTORY_JOB**: 历史作业表

## 历史表 (ACT_HI_*)

- **ACT_HI_PROCINST**: 历史流程实例表
- **ACT_HI_ACTINST**: 历史节点表
- **ACT_HI_TASKINST**: 历史任务表
- **ACT_HI_VARINST**: 历史变量表
- **ACT_HI_DETAIL**: 历史详情表，提供历史变量的细节信息
- **ACT_HI_COMMENT**: 历史评论表
- **ACT_HI_ATTACHMENT**: 历史附件表
- **ACT_HI_IDENTITYLINK**: 历史用户关系表

## 组织机构表 (ACT_ID_*)

- **ACT_ID_USER**: 用户表
- **ACT_ID_GROUP**: 用户组表
- **ACT_ID_MEMBERSHIP**: 用户和用户组对应关系表
- **ACT_ID_INFO**: 用户扩展信息表

## DMN 相关表 (ACT_DMN_*)

- **ACT_DMN_DEPLOYMENT**: DMN部署表
- **ACT_DMN_DECISION_TABLE**: 决策表
- **ACT_DMN_HI_DECISION_EXECUTION**: DMN历史执行表

## CMMN 相关表 (ACT_CMMN_*)

- **ACT_CMMN_DEPLOYMENT**: CMMN部署表
- **ACT_CMMN_CASEDEF**: 案例定义表
- **ACT_CMMN_RU_CASE_INST**: 运行时案例实例表
- **ACT_CMMN_HI_CASE_INST**: 历史案例实例表

## APP 相关表 (ACT_APP_*)

- **ACT_APP_DEPLOYMENT**: APP部署表
- **ACT_APP_APPDEF**: APP定义表

## 主要表用途说明

1. **流程定义相关**
   - ACT_RE_DEPLOYMENT: 存储流程部署信息
   - ACT_RE_PROCDEF: 存储流程定义的具体信息
   - ACT_GE_BYTEARRAY: 存储流程定义的bpmn文件和图片

2. **流程运行时相关**
   - ACT_RU_EXECUTION: 存储流程实例和执行流的信息
   - ACT_RU_TASK: 存储任务信息
   - ACT_RU_VARIABLE: 存储流程变量信息
   - ACT_RU_IDENTITYLINK: 存储任务参与者信息

3. **历史数据相关**
   - ACT_HI_PROCINST: 存储历史流程实例
   - ACT_HI_TASKINST: 存储历史任务实例
   - ACT_HI_ACTINST: 存储历史活动节点
   - ACT_HI_VARINST: 存储历史变量

4. **用户组织相关**
   - ACT_ID_USER: 存储用户信息
   - ACT_ID_GROUP: 存储用户组信息
   - ACT_ID_MEMBERSHIP: 存储用户和用户组的关系

## 表前缀说明

- **ACT_RE_**: Repository 流程定义相关表
- **ACT_RU_**: Runtime 运行时相关表
- **ACT_HI_**: History 历史相关表
- **ACT_GE_**: General 通用数据表
- **ACT_ID_**: Identity 身份信息表
- **ACT_DMN_**: Decision Model and Notation 决策相关表
- **ACT_CMMN_**: Case Management Model and Notation 案例相关表
- **ACT_APP_**: Application 应用相关表
