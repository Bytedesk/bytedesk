<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:43:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 11:42:56
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

- [English](./readme.md)
- [中文](./readme.zh.md)

## Admin Dashboard

![statistics](./images/admin/statistics.png)

## Admin Chat

![chat](./images/admin/chat.png)

## Admin LLM+Agent

![llm_agent](./images/admin/llm_agent.png)

## Admin Channel

![channel](./images/admin/channel.png)

## Agent

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

### [Open Platform](./plugins/readme.md)

- Provides complete RESTful API interfaces and SDK toolKits
- Supports seamless integration with third-party systems for data interoperability
- Multi-language SDK support to simplify development and integration processes

## Quick Start

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# start without ai
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# or default zhipuai
docker compose -p bytedesk -f docker-compose.yaml up -d
# or default ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

```bash
# Please replace 127.0.0.1 with your server IP
Access address: http://127.0.0.1:9003/
Default account: admin@email.com
Default password: admin
```

- [Docker Deploy](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Baota Deploy](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [Source Code](https://www.weiyuai.cn/docs/docs/deploy/source)

## Project Structure

Monorepo powered by Maven (root `pom.xml`) with multiple modules and deploy assets.

```text
bytedesk/
├─ channels/           # Channel integrations (douyin, shop, social, wechat)
├─ demos/              # Example projects and sample code
├─ deploy/             # Deployment assets: docker, k8s, server configs
├─ enterprise/         # Enterprise features (ai, call, core, kbase, service, ticket)
├─ images/             # Images used in docs and UI previews
├─ jmeter/             # Performance tests and scripts
├─ logs/               # Runtime logs (local/dev)
├─ modules/            # Core product modules (TeamIM, Service, KBase, Ticket, AI, ...)
├─ plugins/            # Optional plugins (freeswitch, webrtc, open platform)
├─ projects/           # Custom projects or extensions
├─ starter/            # Starters/entry points
```

## architecture

- [architecture](https://www.weiyuai.cn/architecture.html)

## Open Source Client

- [desktop](https://github.com/Bytedesk/bytedesk-desktop)
- [QT client](https://github.com/Bytedesk/bytedesk-qt)
- [mobile](https://github.com/Bytedesk/bytedesk-mobile)
- [siphone](https://github.com/Bytedesk/bytedesk-phone)
- [conference](https://github.com/Bytedesk/bytedesk-conference)
- [freeswitch docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [jitsi docker](https://github.com/Bytedesk/bytedesk-jitsi)

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
<!-- | [Magento](https://github.com/bytedesk/bytedesk-magento) | Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [Prestashop](https://github.com/bytedesk/bytedesk-prestashop) | Prestashop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [Opencart](https://github.com/bytedesk/bytedesk-opencart) | Opencart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) | -->

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

- **Allowed Uses**: Can be used for commercial purposes, but prohibited from resale without prior permission
- **Prohibited Uses**: Strictly prohibited for use in illegal and non-compliant businesses including trojans, viruses, pornography, gambling, fraud, and other illegal activities
- **Disclaimer**: This software does not guarantee any form of legal liability. Users are responsible for their own usage risks
