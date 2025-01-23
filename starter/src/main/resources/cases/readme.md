<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-15 18:02:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-21 13:37:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 工单处理系统

## 流程结构

### 1. CMMN 案例模型 (complex-ticket.cmmn)

- 创建工单阶段
  - 记录工单详情
  - 设置优先级
- 技术分析阶段
  - 调查问题
- 客户验证阶段
  - 验证问题
  - 满意度评价

### 2. DMN 决策表 (ticket-assignment.dmn)

根据工单类型和优先级决定：

- 处理组
- 响应时限
- SLA 要求

### 3. 服务实现

- TicketSLAService: SLA 监控和违规处理
- TicketCaseListener: 工单状态变更监听

## 使用说明

1. 创建工单时必须填写类型和优先级
2. 系统自动根据决策表分配处理组
3. SLA 服务自动监控处理时限
4. 状态变更时自动通知相关人员
