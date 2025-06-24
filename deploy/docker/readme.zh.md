<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-24 09:21:23
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

## 停止和重启服务

```bash
# 停止所有服务（保留数据）
docker compose -p bytedesk -f docker-compose-ollama.yaml down

# 停止所有服务并删除数据卷（谨慎操作，会删除所有数据）
docker compose -p bytedesk -f docker-compose-ollama.yaml down -v

# 重启特定服务
docker compose -p bytedesk -f docker-compose-ollama.yaml restart bytedesk

# 重启所有服务
docker compose -p bytedesk -f docker-compose-ollama.yaml restart
```

## 升级bytedesk镜像

```bash
# 1. 停止当前服务
docker compose -p bytedesk -f docker-compose-ollama.yaml down

# 2. 拉取最新镜像
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest

# 3. 重新启动服务（会自动使用最新镜像）
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d

# 或者使用以下命令强制重新构建并启动
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d --force-recreate bytedesk
```

## 删除MySQL数据挂载

如果需要删除MySQL数据挂载并重新初始化数据库，请按以下步骤操作：

```bash
# 1. 停止所有服务
docker compose -p bytedesk -f docker-compose-ollama.yaml down

# 2. 删除MySQL数据卷（谨慎操作，会删除所有数据库数据）
docker volume rm bytedesk_mysql_data

# 3. 重新启动服务（会自动创建新的数据卷和数据库）
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d

# 注意：删除数据卷后，所有数据都会丢失，需要重新初始化管理员账户
```

## 数据卷管理

```bash
# 查看所有数据卷
docker volume ls | grep bytedesk

# 查看数据卷详细信息
docker volume inspect bytedesk_mysql_data
docker volume inspect bytedesk_redis_data
docker volume inspect bytedesk_elasticsearch_data
docker volume inspect bytedesk_upload_data
docker volume inspect bytedesk_ollama_models
docker volume inspect bytedesk_artemis_data

# 备份数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz -C /data .

# 恢复数据卷（可选）
docker run --rm -v bytedesk_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /data
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

# 清理未使用的资源
docker system prune -f
```

## 常用命令

```bash
# 查看服务状态
docker compose -p bytedesk -f docker-compose-ollama.yaml ps

# 查看服务日志
docker compose -p bytedesk -f docker-compose-ollama.yaml logs

# 查看特定服务日志
docker compose -p bytedesk -f docker-compose-ollama.yaml logs bytedesk

# 进入容器内部
docker exec -it bytedesk /bin/bash
docker exec -it mysql-bytedesk mysql -u root -p

# 查看容器资源使用情况
docker stats
```
