<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:43:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-21 16:23:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Bytedesk - Chat as a Service

Team Cooperation with AI powered Omnichannel customer service

## Language

- [English](./README.md)
- [中文](./README.zh.md)

![admin](./images/admin/chat.png)

## Introduction

### Team IM

- Multi-level organizational structure
- Role management
- Permission management
- ...

### AI Chat

- Chat with LLM
- Chat with Knowledge base(RAG)
- ...

### Customer Service

- Support multiple channels
- multiple routing strategies, and detailed assessment indicators
- Seating workbench
- ...

### Ticket

- Ticket management
- Ticket SLA management
- Ticket statistics and reports
- ...

## Docker Quick Start

### clone project and start docker compose container

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### stop container

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## Preview

```bash
http://127.0.0.1:9003/dev
```

## Chat SDK

| Project     | Description           | Forks          | Stars             |
|-------------|-----------------------|----------------|-------------------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | iOS  | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift)                 |
| [Android](https://github.com/bytedesk/bytedesk-android) | Android | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android)  |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter)| ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | Uniapp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Web | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |

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

- [MIT](./LICENSE)
