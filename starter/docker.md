<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:11:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-15 23:03:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# docker

```bash
# 启动docker容器
# 修改application.properties中的配置，默认为true
spring.docker.compose.enabled=true
# 运行
mvn spring-boot:run
# 可以运行，但无法连接mysql/redis：
# docker run --name test -p 9003:9003 jackning/test
```

## docker compose

本地打包本地运行

```bash
# 启动docker容器
# 进入项目目录
cd bytedesk/starter
# 编译
mvn clean package -Dmaven.test.skip=true
docker build -t bytedesk/bytedesk .
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
docker compose -f docker-compose.yaml up -d
# 启动后查看容器
docker ps
# 停止容器
docker compose -f docker-compose.yaml stop
# 停止并删除 docker-compose.yaml 文件中定义的所有服务（容器
# docker compose down 
# 删除镜像
# docker rmi bytedesk/bytedesk:latest
# docker rmi -f bytedesk/bytedesk:latest
# 推送镜像到docker hub
# https://hub.docker.com/repository/docker/bytedesk/bytedesk/general
docker push bytedesk/bytedesk:latest
```

```bash
docker pull registry.cn-hangzhou.aliyuncs.com/weiyuai/bytedesk:latest
# 启动docker compose容器, -f标志来指定文件路径, -d标志表示在后台模式下启动容器
docker compose -f docker-compose.yaml up -d
# 停止容器
docker compose -f docker-compose.yaml stop
```
