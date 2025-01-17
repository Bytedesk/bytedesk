<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-12 10:21:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 12:37:10
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
# https://www.weiyuai.cn/docs/docs/deploy/docker
# clone project
git clone https://github.com/Bytedesk/bytedesk.git
# enter docker directory
cd bytedesk/deploy/docker
# pull aliyun image
# community free version
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk-ce:latest
# enterprise paid version, under development, not yet officially commercialized
docker pull registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest
# or pull from docker hub:
# community free version
docker pull bytedesk/bytedesk-ce:latest
# enterprise paid version, under development, not yet officially commercialized
docker pull bytedesk/bytedesk:latest
# start docker compose container, -f flag to specify file path, -d flag to start container in background mode
docker compose -f docker-compose.yaml up -d
# stop container
docker compose -f docker-compose.yaml stop
```
