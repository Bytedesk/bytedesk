<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-02 13:21:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-06 17:56:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 在线客服模块主要功能

- agent：一对一人工客服，不支持机器人接待
- robot：机器人客服，不支持转人工
- workgroup：工作组，支持机器人接待，支持转人工

## 1. [客服管理](./readme.zh.account.md)

- 客服账号管理(添加/删除/禁用客服)
- 客服分组管理
- 客服权限管理
- 客服状态管理(在线/离线/忙碌等)
- 客服工作时间管理
- 客服技能标签管理

## 2. [会话管理](./readme.zh.thread.md)

- 访客会话分配
- 会话排队管理
- 会话转接
- 会话监控
- 会话记录存储
- 历史会话查询

## 3. [消息管理](./readme.zh.message.md)

- 实时消息收发
- 消息类型支持(文本/图片/文件等)
- 消息存储
- 消息撤回
- 敏感词过滤
- 消息记录查询

## 4. [访客管理](./readme.zh.visitor.md)

- 访客信息采集
- 访客轨迹记录
- 访客黑名单管理
- 访客标签管理
- 访客分流规则

## 5. [自动化功能管理](./readme.zh.automation.md)

- 智能分配规则
- 自动欢迎语
- 自动回复
- 排队提醒
- 超时提醒
- 满意度评价

## 6. [统计分析](./readme.zh.statistic.md)

- 客服工作量统计
- 会话量统计
- 响应时间统计
- 满意度统计
- 分流统计
- 客服绩效分析

## 7. [系统配置](./readme.zh.config.md)

- 工作时间配置
- 分配规则配置
- 自动回复配置
- 评价配置
- 提醒配置
- 黑名单配置

## 8. [集成接口](./readme.zh.api.md)

- REST API接口
- WebSocket接口
- 事件回调接口
- 第三方系统集成
