<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-07 11:20:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-07 11:21:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 关键点说明

## 1. 访客分配核心逻辑

- 获取可用客服列表
- 根据多维度计算客服权重
- 选择最优客服进行分配
- 更新会话和客服状态
- 发送相关通知

## 2. 排队管理核心逻辑

- 计算访客优先级
- 维护优先级队列
- 定时处理排队项
- 预估等待时间
- 队列状态管理

## 3. 关键技术

- 事务管理
- 定时任务
- 并发控制
- 异常处理
- 状态同步
