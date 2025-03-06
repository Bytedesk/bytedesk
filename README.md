<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-05 09:43:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 11:29:44
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

:::tip
Bytedesk is still in the early stage of rapid iteration, the documentation may lag behind the development, resulting in possible functional descriptions that do not match the latest software version
:::

## Language

- [English](./README.md)
- [中文](./README.zh.md)

![statistics](./images/admin/statistics.png)
![chat](./images/admin/chat.png)

## Introduction

### Team IM

- Multi-level organizational structure
- Role management
- Permission management
- ...

### Customer Service

- Support multiple channels
- multiple routing strategies, and detailed assessment indicators
- Seating workbench
- ...

### Knowledge Base

- Internal Docs
- HelpCenter
- FAQ

### Ticket

- Ticket management
- Ticket SLA management
- Ticket statistics and reports
- ...

### AI Chat

- Chat with Ollama/DeepSeek/ZhipuAI/...
- Chat with Knowledge base(RAG)
- Function calling
- Mcp

### Social Group

- like Discord
- ...

### Multi-tenant

- Multi-tenant management
- Tenant isolation
- Tenant statistics
- ...

## Docker Quick Start

### method 1: clone project and start docker compose container，need ollama

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

#### ollama pull model

```bash
ollama pull deepseek-r1:1.5b
ollama pull qwen2.5:1.5b
```

### method 2: run docker compose with ollama

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

#### docker ollama pull model

```bash
docker exec ollama-bytedesk ollama pull deepseek-r1
docker exec ollama-bytedesk ollama pull qwen2.5:1.5b
```

#### if don't need ollama, you can change `docker-compose.yaml` or `docker-compose-ollama.yaml`

```bash
# disable Ollama chat
SPRING_AI_OLLAMA_CHAT_ENABLED: false
# disable Ollama embedding
SPRING_AI_OLLAMA_EMBEDDING_ENABLED: false
```

#### stop container

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

#### change config, otherwise upload image, file and knowledge base cannot be displayed normally

- change `docker-compose.yaml` file or `docker-compose-ollama.yaml`

```bash
# please replace 127.0.0.1 with your server ip
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

- [online preview](https://www.weiyuai.cn/admin/)
- [demo](https://demo.weiyuai.cn)

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

- [Apache License 2.0](./LICENSE.txt)
