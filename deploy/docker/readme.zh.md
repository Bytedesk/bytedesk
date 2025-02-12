<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-12 11:09:29
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
