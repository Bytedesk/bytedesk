<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 17:44:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 18:42:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 工单处理流程

## 1. 优先级评估

- 自动评估工单优先级
- 紧急工单自动升级处理

## 2. SLA监控

- 设置4小时的处理时限
- 超时自动发送违规通知
- 可以循环提醒直到处理完成

## 3. 多级审批

- 处理人处理
- 主管审核
- 客户验证
- 满意度评价
- 增加客户满意度调查环节
- 作为工单关闭的必要步骤

## 4. 自动化任务

- 优先级评估
- 工单升级
- SLA违规通知
- 工单关闭

## 5. 灵活的处理流程

- 客户不满意可以重新处理
- 紧急工单可以快速升级
- 主管参与审核保证质量

这个流程更适合正式的工单系统使用，能够更好地保证服务质量和客户满意度。

## 6. 简化处理流程

- 移除了常规流程中的主管审核环节
- 客户验证不通过时才进入主管审核
- 主管可以决定：
  - 重新分配工单（reassign=true）
  - 让当前处理人继续处理（reassign=false）
- 只有客户满意后才进入满意度评价环节
- 评价完成后自动关闭工单

这样的流程更符合实际工作场景，避免了不必要的审核环节，提高了处理效率。

## 7. 自动关闭工单

1. 如果客户24小时内完成评价，走正常关闭流程
2. 如果客户24小时内未评价，触发定时器，自动关闭工单
3. 自动关闭时会记录相关原因
