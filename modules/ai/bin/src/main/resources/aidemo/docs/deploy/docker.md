---
sidebar_label: Docker部署
sidebar_position: 3
---

# Docker部署

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

## 宝塔面板部署

- [宝塔面板部署](./baota)

## 一行命令启动

```bash
git clone https://gitee.com/270580156/weiyu.git && cd weiyu/deploy/docker && docker compose -p weiyu -f docker-compose.yaml up -d
```

### 因项目默认使用ollama qwen3:0.6b模型，所以需要提前拉取模型

```bash
ollama pull deepseek-r1:1.5b
ollama pull qwen3:0.6b
# 或docker拉取
# docker exec ollama pull qwen3:0.6b
```

### 停止容器

```bash
docker compose -p weiyu -f docker-compose.yaml stop
```

### 修改配置，否则上传图片、文件和知识库无法正常显示

- 修改 `docker-compose.yaml` 文件

```bash
# 请将服务器127.0.0.1替换为你的服务器ip
BYTEDESK_UPLOAD_URL: http://127.0.0.1:9003
BYTEDESK_KBASE_API_URL: http://127.0.0.1:9003
```

## 演示

本地预览

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

- [线上预览](https://www.weiyuai.cn/admin/)
