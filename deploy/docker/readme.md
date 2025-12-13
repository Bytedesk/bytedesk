<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:28:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

## 文件说明

```bash
.
├── docker-compose-noai.yaml # 不使用ai，无机器人问答
├── docker-compose-ollama.yaml # 启动微语，同时启动mysql,redis,ollama,elasticsearch依赖和微语，内含ollama，默认使用ollama对话
├── docker-compose.yaml # 启动微语，同时启动mysql,redis,elasticsearch依赖和微语，不内含ollama，默认使用zhipuai
```

## docker compose

```bash
# https://www.weiyuai.cn/docs/docs/deploy/docker
# clone project
git clone https://github.com/Bytedesk/bytedesk.git
# enter docker directory
cd bytedesk/deploy/docker
# configure environment variables, modify as needed
cp .env.example .env
# start docker compose container, -f flag to specify file path, -d flag to start container in background mode
# start mysql, redis, elasticsearch dependencies and weiyu
docker compose -p bytedesk -f docker-compose.yaml up -d
# start mysql, redis, ollama, elasticsearch dependencies and weiyu, with ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
# start without ai
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# chat model
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# embedding model
docker exec ollama-bytedesk ollama pull bge-m3:latest
# rerank model
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# stop container
docker compose -p bytedesk -f docker-compose.yaml stop
# stop ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml stop

## Secrets & Jasypt (optional)

Some docker compose entries may be stored as `ENC(...)`. Only when you actually use those encrypted values do you need to pass the Jasypt password into Docker:

```bash
# 1. Add the password to .env so compose picks it up (never commit real secrets).
echo 'JASYPT_ENCRYPTOR_PASSWORD=please-change-me' >> .env

# 2. Start any compose stack as usual. The Bytedesk service will read the env var.
docker compose -p bytedesk -f docker-compose.yaml up -d
```

- Leave `JASYPT_ENCRYPTOR_PASSWORD` blank (or remove the line) when no encrypted values are in use—startup will fall back to plain text.
- You can also override algorithms or iterations with additional variables (for example `BYTEDESK_SECURITY_JASYPT_ALGORITHM=PBEWITHHMACSHA512ANDAES_256`).
```
