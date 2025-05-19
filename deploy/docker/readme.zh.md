<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 18:19:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

## 微语启动

```bash
# https://www.weiyuai.cn/docs/zh-CN/docs/deploy/docker
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git
# 进入docker目录
cd bytedesk/deploy/docker
# 配置环境变量，根据需要修改
cp .env.example .env
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
# 同时启动mysql,redis,elasticsearch依赖和微语
docker compose -p bytedesk -f docker-compose.yaml up -d
# 同时启动mysql,redis,ollama,elasticsearch依赖和微语，内含ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
# 仅启动mysql、redis、elasticsearch依赖，不启动微语
docker compose -p bytedesk -f docker-compose-middleware.yaml up -d
# 拉取ollama模型
# 对话模型
docker exec ollama-bytedesk ollama pull qwen3:0.6b
# 嵌入模型
docker exec ollama-bytedesk ollama pull bge-m3:latest
# 重新排序Rerank模型
docker exec ollama-bytedesk ollama pull linux6200/bge-reranker-v2-m3:latest
# 停止
docker compose -p bytedesk -f docker-compose.yaml down
# 停止，内含ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml down
```

## 故障排除

如果遇到数据库连接问题或服务启动失败，可以尝试以下步骤：

```bash
# 查看容器状态
docker ps -a

# 查看服务日志
docker logs mysql-bytedesk
docker logs bytedesk

# 如果服务启动失败，可以尝试重启
docker compose -p bytedesk -f docker-compose.yaml down
docker compose -p bytedesk -f docker-compose.yaml up -d

# 检查网络连接
docker network inspect bytedesk-network
```
