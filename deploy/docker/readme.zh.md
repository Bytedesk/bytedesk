<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-17 10:41:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

## docker compose

```bash
# https://www.weiyuai.cn/docs/zh-CN/docs/deploy/docker
# 克隆项目
git clone https://github.com/Bytedesk/bytedesk.git
# 进入docker目录
cd bytedesk/deploy/docker
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
docker compose -p bytedesk -f docker-compose.yaml up -d
# 内含ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
# 拉取ollama模型
docker exec ollama-bytedesk ollama pull deepseek-r1:1.5b
# 停止
docker compose -p bytedesk -f docker-compose.yaml stop
# 停止，内含ollama
docker compose -p bytedesk -f docker-compose-ollama.yaml stop
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
