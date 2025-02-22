---
sidebar_label: Docker
sidebar_position: 6
---

# Docker

- [参考链接](https://cloud.tencent.com/document/product/213/46000)

## 添加 Docker 软件源

```bash
sudo apt-get update
sudo apt-get install ca-certificates curl -y
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://mirrors.cloud.tencent.com/docker-ce/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo   "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://mirrors.cloud.tencent.com/docker-ce/linux/ubuntu/ \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" |   sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```

## 安装 Docker

```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

## 运行配置 Docker

```bash
systemctl start docker
systemctl stop docker
systemctl restart docker
systemctl status docker
sudo service docker restart
# 检查安装结果
docker info
# 搜索镜像
docker search redis
# 本地镜像
docker images
# 正在运行镜像
docker ps
# 安装镜像
# https://github.com/redis-stack/redis-stack
docker pull redis/redis-stack-server
# 删除镜像
docker ps -a
# docker rm 容器id
docker rmi redis/redis-stack-server
# 安装镜像失败, 修改或创建：
vi /etc/docker/daemon.json
# 添加内容：腾讯云
{
   "registry-mirrors": [
      "https://docker.1panel.live/",
      "https://docker.m.daocloud.io",
      "https://noohub.ru",
      "https://huecker.io",
      "https://dockerhub.timeweb.cloud"
  ]
}
#
sudo systemctl daemon-reload
sudo systemctl restart docker
# 重启docker
service docker restart
# 查看信息
docker info
```
