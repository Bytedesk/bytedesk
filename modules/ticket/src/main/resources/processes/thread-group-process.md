<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-24 10:12:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 15:58:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 客服会话流程说明

## 流程概述

该流程文件(`thread-group-process.bpmn20.xml`)定义了完整的在线客服会话流程，从访客发起咨询到满意度评价的全过程。流程采用Flowable工作流引擎实现，确保每个环节都有明确的处理逻辑和角色分配。

## 流程组成部分

### 1. 初始状态

- 访客发起会话
- 系统首先判断是否开启机器人接待

### 2. 机器人接待

- 如果开启了机器人接待，则由机器人处理访客请求
- 机器人服务结束后，判断是否需要转人工
- 如果访客问题已解决，可以继续机器人服务

### 3. 人工接待判断

- 如果访客从机器人服务转人工，或者直接进入人工服务
- 系统会判断当前人工坐席是否繁忙

### 4. 排队机制

- 如果坐席繁忙，访客进入排队等待
- 排队结束后进入人工接待

### 5. 人工接待

- 客服人员接待访客，处理问题
- 设置了SLA计时器，超时后会触发通知
- 支持多种操作：继续接待、转接给其他客服、邀请其他客服协助、标记为已解决

### 6. 转接与协助

- 支持客服之间的转接
- 支持邀请其他客服进行协助

### 7. 结束流程

- 当问题标记为已解决时，流程进入满意度评价阶段
- 访客可以评价服务满意度，并标注问题是否已解决
- 评价完成后，整个会话流程结束

## 技术实现

这个BPMN流程文件使用了Flowable工作流引擎的语法，包含了完整的流程定义和可视化布局。需要实现以下委托类(Delegate)来处理各个服务任务：

- `threadRobotServiceDelegate` - 处理机器人接待服务
- `threadQueueServiceDelegate` - 管理排队等待机制
- `threadSLATimeoutNotificationDelegate` - 处理SLA超时通知
- `threadTransferServiceDelegate` - 处理转接服务
- `threadInviteServiceDelegate` - 处理邀请协助服务
- `threadExecutionListener` - 流程执行监听器
- `threadTaskListener` - 任务事件监听器

## 业务价值

使用这个流程定义文件，可以：

- 规范化整个客服会话处理流程
- 确保每个环节都有明确的处理逻辑和角色分配
- 提高客服响应效率和服务质量
- 支持灵活的人工介入和协作机制
- 完整记录会话处理过程，便于后续分析和改进
