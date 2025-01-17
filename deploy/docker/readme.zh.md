<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-16 13:16:46
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
# 拉取阿里云镜像
# 社区免费版
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk-ce:latest
# 企业付费版-完善中，暂未正式商业化
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest
# 或从 docker hub拉取镜像：
# 社区免费版
docker pull bytedesk/bytedesk-ce:latest
# 企业付费版-完善中，暂未正式商业化
docker pull bytedesk/bytedesk:latest
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
docker compose -f docker-compose.yaml up -d
# stop container
docker compose -f docker-compose.yaml stop
```
