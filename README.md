<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:43:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-15 18:36:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Bytedesk - Chat as a Service

AI powered Omnichannel customer service With Team Cooperation

## Language

- [English](./README.md)
- [中文](./README.zh.md)

![statistics](./images/admin/statistics.png)
![chat](./images/admin/chat.png)
![channel](./images/admin/channel.png)
![agent](./images/agent/agent_chat.png)

## Introduction

### [TeamIM](./modules/team/readme.md)

- Multi-level organizational structure
- Role management
- Permission management
- ...

### [Customer Service](./modules/service/readme.md)

- Support multiple channels
- multiple routing strategies, and detailed assessment indicators
- Seating workbench
- ...

### [Knowledge Base](./modules/kbase/readme.md)

- Internal Docs
- HelpCenter
- FAQ

### [Ticket](./modules/ticket/readme.md)

- Ticket management
- Ticket SLA management
- Ticket statistics and reports
- ...

### [AI Agent](./modules/ai/readme.md)

- Chat with Ollama/DeepSeek/ZhipuAI/...
- Chat with Knowledge base(RAG)
- Function calling
- Mcp

### [workflow](./modules/core/readme.workflow.md)

- form
- process
- ticket process
- ...

### [Voice Of Customer](./modules/voc/readme.md)

- feedback
- survey
- ...

### [Call Center](./plugins/freeswitch/readme.zh.md)

- Professional call platform based on FreeSwitch
- Supports incoming call pop-up screens, automatic allocation, and call recording
- Data statistics, seamless integration of voice and text services

### [Video Customer Service](./plugins/webrtc/readme.zh.md)

- High-definition video calls based on WebRTC technology
- Supports one-click video conversations and screen sharing
- Suitable for service scenarios requiring intuitive demonstrations

### Open Platform

## Docker Quick Start

### method 1: clone project and start docker compose container，need zhipuai

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

#### get zhipuai [API Key](https://www.bigmodel.cn/usercenter/proj-mgmt/apikeys)

```bash
# zhipuai
# SPRING_AI_ZHIPUAI_BASE_URL: https://open.bigmodel.cn/api/paas
SPRING_AI_ZHIPUAI_API_KEY: 'sk-xxx' // please replace sk-xxx with your zhipuai API key
SPRING_AI_ZHIPUAI_CHAT_ENABLED: "true"
SPRING_AI_ZHIPUAI_CHAT_OPTIONS_MODEL: glm-4-flash
SPRING_AI_ZHIPUAI_CHAT_OPTIONS_TEMPERATURE: 0.7
SPRING_AI_ZHIPUAI_EMBEDDING_ENABLED: "true"
SPRING_AI_ZHIPUAI_EMBEDDING_OPTIONS_MODEL: embedding-2
```

### method 2: run docker compose with ollama

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

#### docker ollama pull model

```bash
# chat model
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# embedding model
docker exec ollama-bytedesk ollama pull bge-m3:latest
# rerank model
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# or download model from huggingface
# docker exec ollama-bytedesk ollama pull hf.co/<username>/<model-repository>
```

#### stop container

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

#### change config, otherwise upload image, file and knowledge base cannot be displayed normally

- change `docker-compose.yaml` file or `docker-compose-ollama.yaml`

```bash
# please replace 127.0.0.1 with your server ip
BYTEDESK_FEATURES_AVATAR_BASE_URL: http://127.0.0.1:9003
BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
```

### method 3: run from source code

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk && ./mvnw install -Dmaven.test.skip=true && cd starter && ./mvnw spring-boot:run
```

## Preview

local preview

```bash
# please replace 127.0.0.1 with your server ip
http://127.0.0.1:9003/
# open port: 9003, 9885
demo username: admin@email.com
demo password: admin
```

## Appkey

```bash
# Community
bytedesk.appkey=ZjoyMDI1LTA4LTA2OkNPTU1VTklUWTo6Ojo=
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OkNPTU1VTklUWTo6Ojo=
# Enterprise Trial: 2025-08-06
bytedesk.appkey=ZjoyMDI1LTA4LTA2OkVOVEVSUFJJU0U6Ojo6
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OkVOVEVSUFJJU0U6Ojo6
# Platform Trial: 2025-08-06
bytedesk.appkey=ZjoyMDI1LTA4LTA2OlBMQVRGT1JNOjo6Og==
BYTEDESK_APPKEY: ZjoyMDI1LTA4LTA2OlBMQVRGT1JNOjo6Og==
```

## architecture

- [architecture](https://www.weiyuai.cn/architecture.html)

## Open Source Client

- [desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [mobile](https://github.com/Bytedesk/bytedesk-mobile)

## Open Source Demo + SDK

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

## Links

- [Download](https://www.weiyuai.cn/download.html)
- [Docs](https://www.weiyuai.cn/docs/)

<!-- ## Dev Stack -->
<!-- - [sofaboot](https://github.com/sofastack/sofa-boot/blob/master/README_ZH.md) for im server -->
<!-- - [springboot-3.x for im server](https://github.com/Bytedesk/bytedesk) -->
<!-- - [python for ai](https://github.com/Bytedesk/bytedesk-ai) -->
<!-- - [react for web](https://github.com/Bytedesk/bytedesk-react) -->
<!-- - [flutter for ios&android](https://github.com/Bytedesk/bytedesk-mobile) -->
<!-- - [electron for windows&mac&linux](https://github.com/Bytedesk/bytedesk-desktop) -->

## License

Copyright (c) 2013-2025 微语 Bytedesk.com, All rights reserved.

Licensed under GNU AFFERO GENERAL PUBLIC LICENSE(AGPL v3)  (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

<https://www.gnu.org/licenses/agpl-3.0.html>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Terms of Use

- **Prohibited Uses**: Strictly prohibited for use in illegal and non-compliant businesses including trojans, viruses, pornography, gambling, fraud, and other illegal activities
- **Disclaimer**: This software does not guarantee any form of legal liability. Users are responsible for their own usage risks
