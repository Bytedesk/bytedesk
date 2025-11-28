<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-01 13:21:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-16 23:44:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 工单系统

- 参考[腾讯云-工单系统](https://console.cloud.tencent.com/workorder)实现第一版

这个工单系统具有以下主要功能：

## 通知

- 钉钉
- 企业微信
- 飞书
- 邮件
- 短信
- 微信公众号

## 工单管理

- 创建、查看、更新、删除工单
- 工单分类和优先级管理
- 工单状态流转(开启->处理中->已解决->已关闭)
- 工单分配和转派
- 工单截止日期设置
- 工单附件上传和管理
- 工单导出Excel
- 工单分类
- 工单标签
- 工单模板
- 工单统计
- 工单报表
- 工单自动回复

## 评论系统

- 支持客户和客服的工单交互
- 内部评论功能(对客户不可见)
- 评论回复功能
- 评论附件支持

## SLA管理

- 服务级别协议设置
- 响应时限和解决时限监控
- 工作时间设置
- 超时提醒和升级
- SLA规则配置
- 响应时间和解决时间监控
- 自动升级处理
- 违规提醒

## 统计和报表

- 工单量统计
- 响应时间统计
- 解决时间统计
- 满意度统计
- 客服绩效统计
- 导出Excel/PDF报表

## 自动化功能

- 多种分配策略(轮询、最少活跃、负载均衡、技能匹配等)
- 自定义分配规则
- 考虑在线状态和工作负载
- 自动分类
- 自动提醒
- 工单升级规则

修复已知bug，使项目能够运行

## 增值功能

- 消息通知：站内信、用户端推送等
- 关键数据：展示当前节点处理人最希望看到的数据
- 移动办公：H5形式，接入钉钉、微信服务号、企业微信等
- 增值操作：“协同”，即可以选择指定人（通常是助理），替自己处理一些审批
- 实时跟踪：可以实时查看当前所处节点，以及对应处理人，并可对其发起“催办”

## 更多

支持跨组织、跨部门工单协同

### 支持多渠道、多角色、多场景，跨平台创建工单，让任务发起足够便利

支持多种工单创建方式，访客创建、表格创建、邮件创建、跨平台创建、接口创建等。

### 支持高效率调度及处理工单，让任务能被快速解决

工单关联、预设工单回复、复制工单等工具提升坐席复杂问题的处理效率。

### 支持满意度评价，让服务质量不断提升

多渠道邀请坐席及访客评价满意度，考核坐席绩效的同时不断提升服务质量。

### 支持邮件场景，让邮件收发畅通无阻

支持将邮件内容解析为工单，支持邮件回复/抄送等，保障邮件任务被快速解决。
