<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-01 13:21:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-07 21:45:52
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

## 工单管理

- 创建、查看、更新、删除工单
- 工单分类和优先级管理
- 工单状态流转(开启->处理中->已解决->已关闭)
- 工单分配和转派
- 工单截止日期设置
- 工单附件上传和管理

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

## 统计和报表

- 工单量统计
- 响应时间统计
- 解决时间统计
- 满意度统计
- 客服绩效统计

## 自动化功能

- 自动分配工单
- 自动分类
- 自动提醒
- 工单升级规则

修复已知bug，使项目能够运行
