# 微语 - 重复工作自动化

企业级多租户团队协作工具，免费开源N件套：企业IM、在线客服、企业知识库/帮助文档、客户之声、工单系统、AI对话、工作流、呼叫中心、视频客服、开放平台。

## 语言

- [English](./README.md)
- [中文](./README.zh.md)

![weiyu](./images/nin1.png)

## 管理端

![chat](./images/admin/chat_zh.png)

## 多渠道

![channel](./images/admin/channel.png)

## 客服端

![agent](./images/agent/agent_chat.png)

## 介绍

### [企业IM](./modules/team/readme.zh.md)

- 局域网即时通讯
- 企业成员管理
- 聊天记录监控
- ...

### [全渠道客服](./modules/service/readme.zh.md)

- 多渠道接入
- 人工客服
- 客服Agent智能体，对接自有数据，自动执行操作
- ...

### [知识库](./modules/kbase/readme.zh.md)

- 对接大模型
- 自定义知识库
- Function Calling
- Mcp
- ...

### [工单系统](./modules/ticket/readme.zh.md)

- 工单管理
- 工单SLA管理
- 工单统计和报表
- ...

### [AI Agent](./modules/ai/readme.zh.md)

- Ollama/DeepSeek/ZhipuAI/...
- 智能体
- 工作流
- ...

### [工作流](./modules/core/readme.workflow.md)

- 自定义表单
- 自定义流程
- 工单流程可视化
- ...

### [客户之声](./modules/voc/readme.zh.md)

- 意见反馈
- 服务投诉
- 问卷调查
- ...

### [呼叫中心](./plugins/freeswitch/readme.zh.md)

- 基于FreeSwitch的专业呼叫平台
- 支持来电弹屏、自动分配、通话录音
- 数据统计，语音与文字服务无缝集成

### [视频客服](./plugins/webrtc/readme.zh.md)

- 基于WebRTC技术的高清视频通话
- 支持一键视频对话与屏幕共享
- 适用于需要直观展示的服务场景

### 开放平台

## Docker 快速开始

### 方法一：克隆项目并启动docker compose容器，默认使用云模型 智谱AI

```bash
git clone https://gitee.com/270580156/weiyu.git && cd weiyu/deploy/docker && docker compose -p weiyu -f docker-compose.yaml up -d
```

#### 申请智谱AI [API Key](https://www.bigmodel.cn/usercenter/proj-mgmt/apikeys)

```bash
# zhipuai
# SPRING_AI_ZHIPUAI_BASE_URL: https://open.bigmodel.cn/api/paas
SPRING_AI_ZHIPUAI_API_KEY: 'sk-xxx' // 替换为智谱AI API Key
SPRING_AI_ZHIPUAI_CHAT_ENABLED: "true"
SPRING_AI_ZHIPUAI_CHAT_OPTIONS_MODEL: glm-4-flash
SPRING_AI_ZHIPUAI_CHAT_OPTIONS_TEMPERATURE: 0.7
SPRING_AI_ZHIPUAI_EMBEDDING_ENABLED: "true"
SPRING_AI_ZHIPUAI_EMBEDDING_OPTIONS_MODEL: embedding-2
```

### 方法二：使用 docker compose ollama，默认安装ollama，默认使用 qwen3:0.6b 模型

```bash
git clone https://gitee.com/270580156/weiyu.git && cd weiyu/deploy/docker && docker compose -p weiyu -f docker-compose-ollama.yaml up -d
```

#### docker 拉取ollama模型。配置文件中可以配置其他模型，如deepseek-r1等

```bash
# 对话模型
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# 嵌入模型
docker exec ollama-bytedesk ollama pull bge-m3:latest
# 重新排序Rerank模型
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# 或者从 huggingface 下载模型
# docker exec ollama-bytedesk ollama pull hf.co/<username>/<model-repository>
```

<!-- #### 如果不需要知识库AI问答功能，可以修改 `docker-compose.yaml` 或 `docker-compose-ollama.yaml` 关闭ollama对话和嵌入功能，以节省资源

```bash
# 关闭ollama对话
SPRING_AI_OLLAMA_CHAT_ENABLED: false
# 关闭ollama嵌入
SPRING_AI_OLLAMA_EMBEDDING_ENABLED: false
``` -->

#### 停止容器

```bash
docker compose -p weiyu -f docker-compose.yaml stop
```

#### 修改配置，否则上传图片、文件和知识库无法正常显示

- 修改 `docker-compose.yaml` 文件 或 `docker-compose-ollama.yaml` 文件，修改以下配置项：

```bash
# 请将服务器127.0.0.1替换为你的服务器ip
# 头像的访问地址，请修改为服务器实际的地址
BYTEDESK_FEATURES_AVATAR_BASE_URL: http://127.0.0.1:9003
# 文件上传的访问地址，请修改为服务器实际的地址
BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
# 知识库的访问地址，请修改为服务器实际的地址
BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
```

### 方法三：宝塔面板

- [宝塔面板部署](https://www.weiyuai.cn/docs/zh-CN/docs/deploy/baota)

### 方法四：源码启动

- [源码启动](https://www.weiyuai.cn/docs/zh-CN/docs/deploy/source)

## 演示

本地预览

```bash
# 请将127.0.0.1替换为你的服务器ip
http://127.0.0.1:9003/
# 开放端口：9003, 9885
默认用户名: admin@email.com
默认密码: admin
```

## 试用

```bash
# 社区版 永久有效
bytedesk.appkey=ZjoyMDI1LTA4LTA2OkNPTU1VTklUWTo6Ojo=
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OkNPTU1VTklUWTo6Ojo=
# 企业版 试用 有效期：2025-08-06
bytedesk.appkey=ZjoyMDI1LTA4LTA2OkVOVEVSUFJJU0U6Ojo6
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OkVOVEVSUFJJU0U6Ojo6
# 平台版 试用 有效期: 2025-08-06
bytedesk.appkey=ZjoyMDI1LTA4LTA2OlBMQVRGT1JNOjo6Og==
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OlBMQVRGT1JNOjo6Og==
```

## 架构图

- [架构图](https://www.weiyuai.cn/architecture.html)

## 开源客户端

- [桌面客户端](https://github.com/Bytedesk/bytedesk-desktop)
- [移动客户端](https://github.com/Bytedesk/bytedesk-mobile)

## 开源Demo + SDK

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
<!-- - [sofaboot](https://github.com/sofastack/sofa-boot/blob/master/README_ZH.md) for im server 基于金融级云原生架构-->
- [springboot-3.x for 后端](https://github.com/Bytedesk/bytedesk)
- [react for web前端](https://github.com/Bytedesk/bytedesk-web)
- [flutter for 移动客户端(ios&android)](https://github.com/Bytedesk/bytedesk-mobile)
- [electron for 桌面客户端(windows&mac&linux)](https://github.com/Bytedesk/bytedesk-desktop)
<!-- - [python for ai](https://github.com/Bytedesk/bytedesk-ai) -->

## 联系

<!-- - [Email](mailto:270580156@qq.com) -->
<!-- - [微信](./images/wechat.png) -->
- 扫码加微信，入群，备注：微语
- <img src="./images/wechat.png" width="100">
<!-- - 微语技术支持群：
- <img src="./images/wechat_group.jpg" width="100"> -->
- 服务号
- <img src="./images/wechat_mp.jpg" width="100">
- 订阅号
- <img src="./images/wechatai_mp.jpg" width="100">
- 软著
- <img src="./images/copyright.png" width="100">
- 商标
- <img src="./images/trademark.jpg" width="100">

## License

版权所有 (c) 2013-2025 微语 Bytedesk.com，保留所有权利。

根据GNU通用公共许可证第三版(AGPL v3)（"许可证"）授权；除非遵守许可证，否则您不得使用此文件。您可以在以下网址获取许可证副本

<https://www.gnu.org/licenses/agpl-3.0.html>

除非适用法律要求或书面同意，否则根据许可证分发的软件是基于"按原样"分发的，没有任何明示或暗示的保证或条件。有关许可证下的特定语言和限制，请参阅许可证。

## 使用条款

- **禁止用途**：严禁用于含有木马、病毒、色情、赌博、诈骗等违法违规业务
- **免责声明**：本软件不保证任何形式的法律责任，请自行承担使用风险
