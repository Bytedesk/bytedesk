---
title: 宝塔面板部署
sidebar_label: 宝塔面板
sidebar_position: 9
description: 使用宝塔面板部署 ByteDesk
---

## 步骤一

- ![agent](/img/deploy/baota/baota_1.png)

## 步骤二：添加容器编排

- ![agent](/img/deploy/baota/baota_2.png)

### 复制如下编排内容

- [最新docker-compose.yaml](https://gitee.com/270580156/weiyu/blob/main/deploy/docker/docker-compose.yaml)

```bash
services:
  bytedesk-mysql:
    image: mysql:latest
    container_name: mysql-bytedesk
    environment:
      MYSQL_DATABASE: bytedesk
      MYSQL_ROOT_PASSWORD: r8FqfdbWUaN3
    ports:
      - "13306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - bytedesk-network

  bytedesk-redis:
    image: redis/redis-stack-server:latest
    container_name: redis-bytedesk
    ports:
      - "16379:6379"
    environment:
      - REDIS_ARGS=--requirepass qfRxz3tVT8Nh
    volumes:
      - redis_data:/data
    networks:
      - bytedesk-network

  bytedesk:
    # image: bytedesk/bytedesk:latest # hub.docker.com enterprise
    # image: bytedesk/bytedesk-ce:latest # hub.docker.com community
    image: registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk:latest # aliyun enterprise
    # mage: registry.cn-hangzhou.aliyuncs.com/bytedesk/bytedesk-ce:latest # aliyun community
    container_name: bytedesk
    depends_on:
      - bytedesk-mysql
      - bytedesk-redis
    environment:
      # db config
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-bytedesk:3306/bytedesk?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: r8FqfdbWUaN3
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      # redis config
      SPRING_DATA_REDIS_HOST: redis-bytedesk
      SPRING_DATA_REDIS_PORT: 6379
      SPRING_DATA_REDIS_PASSWORD: qfRxz3tVT8Nh
      SPRING_DATA_REDIS_DATABASE: 0
      # bytedesk config
      BYTEDESK_DEBUG: true
      BYTEDESK_EDITION: enterprise
      BYTEDESK_NAME: 
      BYTEDESK_LOGO: 
      BYTEDESK_DESCRIPTION: bytedesk description
      BYTEDESK_VERSION: 0.6.2
      # Admin config
      BYTEDESK_ADMIN_EMAIL: admin@email.com
      BYTEDESK_ADMIN_PASSWORD: admin
      BYTEDESK_ADMIN_PASSWORD_DEFAULT: 123456
      BYTEDESK_ADMIN_NICKNAME: SuperAdmin
      BYTEDESK_ADMIN_MOBILE: 13345678000
      BYTEDESK_ADMIN_MOBILE_WHITELIST: 18888888000,18888888001,18888888002,18888888003,18888888004,18888888005
      BYTEDESK_ADMIN_EMAIL_WHITELIST: 100@email.com,101@email.com,102@email.com,103@email.com,104@email.com,105@email.com
      BYTEDESK_ADMIN_VALIDATE_CODE: 123456
      BYTEDESK_ADMIN_FORCE_VALIDATE_MOBILE: true
      BYTEDESK_ADMIN_FORCE_VALIDATE_EMAIL: true
      # Organization config
      BYTEDESK_ORGANIZATION_NAME: MyCompany
      BYTEDESK_ORGANIZATION_CODE: bytedesk
      # Features config
      BYTEDESK_FEATURES_JAVA_AI: false
      BYTEDESK_FEATURES_PYTHON_AI: true
      BYTEDESK_FEATURES_EMAIL_TYPE: javamail
      BYTEDESK_FEATURES_ENABLE_REGISTRATION: false
      BYTEDESK_FEATURES_AVATAR_BASE_URL: 
      # CORS config
      # BYTEDESK_CORS_ALLOWED_ORIGINS: *
      # JWT config
      BYTEDESK_JWT_SECRET_KEY: 1dfaf8d004207b628a9a6b859c429f49a9a7ead9fd8161c1e60847aeef06dbd2
      BYTEDESK_JWT_EXPIRATION: 2592000000
      BYTEDESK_JWT_REFRESH_TOKEN_EXPIRATION: 5184000000
      # Cache config
      BYTEDESK_CACHE_LEVEL: 0
      BYTEDESK_CACHE_PREFIX: bytedeskim
      BYTEDESK_CACHE_REDIS_STREAM_KEY: bytedeskim:stream
      # Upload config
      BYTEDESK_UPLOAD_TYPE: local
      BYTEDESK_UPLOAD_DIR: /app/uploads
      # 上传文件的访问地址，请修改为服务器实际的地址
      BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
      # Knowledge base config
      BYTEDESK_KBASE_THEME: default
      BYTEDESK_KBASE_HTML_PATH: helpcenter
      # 知识库的访问地址，请修改为服务器实际的地址
      BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
      # Socket config
      BYTEDESK_SOCKET_HOST: 0.0.0.0
      BYTEDESK_SOCKET_WEBSOCKET_PORT: 9885
      BYTEDESK_SOCKET_LEAK_DETECTOR_LEVEL: SIMPLE
      BYTEDESK_SOCKET_PARENT_EVENT_LOOP_GROUP_THREAD_COUNT: 1
      BYTEDESK_SOCKET_CHILD_EVENT_LOOP_GROUP_THREAD_COUNT: 8
      BYTEDESK_SOCKET_MAX_PAYLOAD_SIZE: 10240
      # Cluster config
      BYTEDESK_CLUSTER_ENABLED: false
      # Push config
      # BYTEDESK_PUSH_APNS_BUNDLE_ID: com.kefux.im
      # BYTEDESK_PUSH_APNS_P12_URL: 123.p12
      # BYTEDESK_PUSH_APNS_P12_PASSWORD: 123456
      # Actuator security configuration
      MANAGEMENT_ENDPOINTS_ENABLED_BY_DEFAULT: false
      MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: ''
      MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_EXCLUDE: '*'
      MANAGEMENT_ENDPOINT_HEALTH_ENABLED: false
      MANAGEMENT_ENDPOINT_INFO_ENABLED: false
      MANAGEMENT_SERVER_PORT: -1
      MANAGEMENT_ENDPOINTS_WEB_BASE_PATH: '/management'
      SPRING_SECURITY_BASIC_ENABLED: true
      # ai config
      # zhipuai
      SPRING_AI_ZHIPUAI_CHAT_ENABLED: false
      SPRING_AI_ZHIPUAI_API_KEY: ''
      SPRING_AI_ZHIPUAI_CHAT_OPTIONS_MODEL: glm-4.5-flash
      SPRING_AI_ZHIPUAI_CHAT_OPTIONS_TEMPERATURE: 0.7
      # ollama
      SPRING_AI_OLLAMA_CHAT_ENABLED: true
      SPRING_AI_OLLAMA_BASE_URL: http://host.docker.internal:11434
      # SPRING_AI_OLLAMA_CHAT_OPTIONS_MODEL: qwen:7b
      # SPRING_AI_OLLAMA_CHAT_OPTIONS_TEMPERATURE: 0.7
      # SPRING_AI_OLLAMA_EMBEDDING_ENABLED: false
      # openai/deepseek
      # https://docs.spring.io/spring-ai/reference/api/chat/deepseek-chat.html
      SPRING_AI_OPENAI_CHAT_ENABLED: false
      SPRING_AI_OPENAI_API_KEY: ''
      SPRING_AI_OPENAI_BASE_URL: https://api.deepseek.com
      # SPRING_AI_OPENAI_CHAT_MODEL: deepseek-chat
      SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL: deepseek-chat
      SPRING_AI_OPENAI_CHAT_OPTIONS_TEMPERATURE: 0.7
      SPRING_AI_OPENAI_EMBEDDING_ENABLED: false
      # moonshot
      SPRING_AI_MOONSHOT_CHAT_ENABLED: false
      SPRING_AI_MINIMAX_CHAT_ENABLED: false
      # vector store
      SPRING_AI_VECTORSTORE_REDIS_INITIALIZE_SCHEMA: true
      SPRING_AI_VECTORSTORE_REDIS_URI: redis://:qfRxz3tVT8Nh@redis-bytedesk:6379
      # wechat config
      WECHAT_PAY_ENABLED: false
    ports:
      - 9003:9003
      - 9885:9885
    volumes:
      - upload_data:/app/uploads
    networks:
      - bytedesk-network

volumes:
  mysql_data:
  redis_data:
  upload_data:

networks:
  bytedesk-network:
    driver: bridge
```

### 因项目默认使用ollama qwen3:0.6b模型，所以需要提前拉取模型

```bash
ollama pull deepseek-r1:1.5b
ollama pull qwen3:0.6b
# 或docker拉取
# docker exec ollama pull qwen3:0.6b
```

### 修改配置，否则上传图片、文件和知识库无法正常显示

- 修改上述编排文件

```bash
# 请将服务器127.0.0.1替换为你的服务器ip
BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
```

## 步骤三：等待中

- ![agent](/img/deploy/baota/baota_3.png)

## 步骤四：添加完成，关闭窗口

- ![agent](/img/deploy/baota/baota_4.png)

## 步骤五：安装成功

- ![agent](/img/deploy/baota/baota_5.png)

## 步骤六：开放端口

请开放内网入方向端口

- 9003
- 9885

## 步骤七：预览

```bash
# 请将127.0.0.1替换为你的服务器ip
http://127.0.0.1:9003/
```

## 修改默认密码

- 修改 `docker-compose.yaml` 文件

```bash
BYTEDESK_ADMIN_EMAIL: admin@email.com
BYTEDESK_ADMIN_PASSWORD: admin
```

- 或登录之后在个人资料修改密码
