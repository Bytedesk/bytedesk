# 微语 - 对话即服务

企业级多租户即时通讯解决方案

## 介绍

### [企业IM]

## 1. 即时通讯核心功能

### 1.1 单聊(一对一)

- 消息收发
  - 文本消息
  - 图片消息
  - 语音消息
  - 视频消息
  - 文件消息
  - 表情/贴纸
  - @提醒功能
  - 消息撤回
  - 已读回执

- 会话管理
  - 会话列表
  - 置顶会话
  - 免打扰设置
  - 会话归档
  - 消息搜索
  - 历史记录

### 1.2 群聊功能

- 群组管理
  - 创建群组
  - 解散群组
  - 群组设置
  - 群公告
  - 群头像
  - 群分类

- 成员管理
  - 邀请成员
  - 移除成员
  - 退出群组
  - 群主转让
  - 管理员设置
  - 成员禁言

## 2. 组织架构管理

### 2.1 部门管理

- 部门结构
  - 多级部门
  - 部门主管
  - 部门成员
  - 跨部门设置

- 权限控制
  - 部门权限
  - 数据权限
  - 功能权限
  - 审批权限

### 2.2 成员管理

- 账号管理
  - 账号创建
  - 账号禁用
  - 密码重置
  - 角色分配
  - 权限设置

- 员工信息
  - 基本信息
  - 联系方式
  - 职位信息
  - 工作状态
  - 考勤打卡

## 3. 协作功能

### 3.1 工作协同

- 任务管理
  - 任务分配
  - 任务跟踪
  - 任务提醒
  - 任务报告

- 日程管理
  - 日程创建
  - 日程共享
  - 日程提醒
  - 会议预约

### 3.2 文件协作

- 文件管理
  - 文件上传
  - 文件共享
  - 文件预览
  - 版本控制
  - 权限管理

- 知识库
  - 文档创建
  - 协同编辑
  - 文档分类
  - 全文检索

## 4. 安全管理

### 4.1 访问控制

- 登录安全
  - 密码策略
  - 双因素认证
  - 登录限制
  - 异地登录

- 数据安全
  - 端到端加密
  - 敏感词过滤
  - 防泄密管理
  - 审计日志

### 4.2 合规管理

- 消息审计
  - 内容审核
  - 行为监控
  - 违规预警
  - 报告导出

- 数据合规
  - 数据备份
  - 数据归档
  - 隐私保护
  - 合规报告

## 5. 系统集成

### 5.1 基础集成

- 单点登录(SSO)
- 通讯录同步
- 消息推送
- 文件存储
- 音视频服务

### 5.2 业务集成

- OA系统集成
- CRM系统集成
- 工单系统集成
- 考勤系统集成
- 审批系统集成

## 关键技术点

### 1. 实时通讯

WebSocket长连接
消息可靠投递
多端消息同步
离线消息处理

### 2. 高可用架构

分布式部署
负载均衡
故障转移
数据备份

### 3. 安全机制

数据加密
访问控制
安全审计
合规管理

### 4. 性能优化

消息分片
大文件传输
消息压缩
缓存策略

### 在线客服模块主要功能

- agent：一对一人工客服，不支持机器人接待
- robot：机器人客服，不支持转人工
- workgroup：工作组，支持机器人接待，支持转人工

## 1. [客服管理]

- 客服账号管理(添加/删除/禁用客服)
- 客服分组管理
- 客服权限管理
- 客服状态管理(在线/离线/忙碌等)
- 客服工作时间管理
- 客服技能标签管理

## 2. [会话管理]

- 访客会话分配
- 会话排队管理
- 会话转接
- 会话监控
- 会话记录存储
- 历史会话查询

## 3. [消息管理]

- 实时消息收发
- 消息类型支持(文本/图片/文件等)
- 消息存储
- 消息撤回
- 敏感词过滤
- 消息记录查询

## 4. [访客管理]

- 访客信息采集
- 访客轨迹记录
- 访客黑名单管理
- 访客标签管理
- 访客分流规则

## 5. [自动化功能管理]

- 智能分配规则
- 自动欢迎语
- 自动回复
- 排队提醒
- 超时提醒
- 满意度评价

## 6. [统计分析]

- 客服工作量统计
- 会话量统计
- 响应时间统计
- 满意度统计
- 分流统计
- 客服绩效分析

## 7. [系统配置]

- 工作时间配置
- 分配规则配置
- 自动回复配置
- 评价配置
- 提醒配置
- 黑名单配置

## 8. [集成接口]

- REST API接口
- WebSocket接口
- 事件回调接口
- 第三方系统集成

### [知识库]

- 对接大模型L
- 自定义知识库
- AI对话
- ...

### [工单系统](./ticket/readme.zh.md)

- 工单管理
- 工单SLA管理
- 工单统计和报表
- ...

### [AI大模型](./ai/readme.zh.md)

- Ollama/DeepSeek/ZhipuAI/...
- 智能体
- 工作流
- ...

### [社交群组](./social/readme.zh.md)

- 类似 Discord
- ...

### 多租户

- 多租户管理
- 租户隔离
- 租户统计
- ...

## Docker 快速开始

### 克隆项目并启动docker compose容器

```bash
git clone https://gitee.com/270580156/weiyu.git && cd weiyu/deploy/docker && docker compose -p weiyu -f docker-compose.yaml up -d
```

### 或者 使用 docker compose ollama

```bash
git clone https://gitee.com/270580156/weiyu.git && cd weiyu/deploy/docker && docker compose -p weiyu -f docker-compose-ollama.yaml up -d
# 运行模型
# docker exec ollama-bytedesk ollama pull deepseek-r1
docker exec ollama-bytedesk ollama pull qwen3:0.6b
```

### 因项目默认使用ollama qwen3:0.6b模型，所以需要提前拉取模型

```bash
ollama pull deepseek-r1:1.5b
ollama pull qwen3:0.6b
```

### 停止容器

```bash
docker compose -p weiyu -f docker-compose.yaml stop
```

### 修改配置，否则上传图片、文件和知识库无法正常显示

- 修改 `docker-compose.yaml` 文件

```bash
# 请将服务器127.0.0.1替换为你的服务器ip
BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
```

### 宝塔面板

- [宝塔面板部署](https://www.weiyuai.cn/docs/zh-CN/docs/deploy/baota)

## 演示

本地预览

```bash
# 请将127.0.0.1替换为你的服务器ip
http://127.0.0.1:9003/
# 开放端口：9003, 9885
```

- [线上预览](https://www.weiyuai.cn/admin/)

## 开源客户端

- [桌面客户端](https://github.com/Bytedesk/bytedesk-desktop)
- [移动客户端](https://github.com/Bytedesk/bytedesk-mobile)

## 开源SDK

| Project     | Description           | Forks          | Stars             |
|-------------|-----------------------|----------------|-------------------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | iOS  | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift)                 |
| [Android](https://github.com/bytedesk/bytedesk-android) | Android | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android)  |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter)| ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Uniapp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Vue/React/Angular/Next.js/JQuery/... | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [Wordpress](https://github.com/bytedesk/bytedesk-wordpress) | Wordpress | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [Woocommerce](https://github.com/bytedesk/bytedesk-woocommerce) | woocommerce | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [Prestashop](https://github.com/bytedesk/bytedesk-prestashop) | Prestashop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [Opencart](https://github.com/bytedesk/bytedesk-opencart) | Opencart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## 链接

- [下载](https://www.weiyuai.cn/download.html)
- [文档](https://www.weiyuai.cn/docs/zh-CN/)

## 技术栈

- [springboot-3.x for 后端](https://github.com/Bytedesk/bytedesk)
- [react for web前端](https://github.com/Bytedesk/bytedesk-web)
- [flutter for 移动客户端(ios&android)](https://github.com/Bytedesk/bytedesk-mobile)
- [electron for 桌面客户端(windows&mac&linux)](https://github.com/Bytedesk/bytedesk-desktop)

## 联系

- [网站](https://www.weiyuai.cn)

## 版权

- [Apache License 2.0](./LICENSE.txt)
- 此为开源社区版，支持免费商用
