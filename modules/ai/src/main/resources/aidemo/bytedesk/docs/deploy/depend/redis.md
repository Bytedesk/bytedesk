---

sidebar_label: Redis
sidebar_position: 2
---

# Redis-stack-server

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

:::warning
因AI知识库问答用到向量搜索，需要安装redis-stack-server，而不是普通redis，否则无法正常使用AI知识库问答
:::

## 参考[redis官方安装说明](https://redis.io/docs/install/install-stack/docker/)

## ubuntu 安装非docker版 [redis-stack-server](https://redis.io/docs/install/install-stack/linux/)

```bash
curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg
sudo chmod 644 /usr/share/keyrings/redis-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list
sudo apt-get update
sudo apt-get install redis-stack-server
# 启动
sudo systemctl enable redis-stack-server
sudo systemctl start redis-stack-server
# 停止
sudo systemctl stop redis-stack-server
# 重启
sudo systemctl restart redis-stack-server
# 查看状态
sudo systemctl status redis-stack-server
# 配置文件
cat /etc/systemd/system/redis-stack-server.service
cd /opt/redis-stack/
cat /etc/redis-stack.conf
# 修改密码
# 运行命令：
redis-cli
# 查看现有的redis密码：
config get requirepass
# 随机密码 https://suijimimashengcheng.bmcx.com/
# 设置 redis 密码：
config set requirepass 密码
```

## [Docker方式安装](https://redis.io/docs/latest/operate/oss_and_stack/install/install-stack/docker/)

```bash
# 首先本地启动docker, 如果没有安装，会自动安装redis/redis-stack-server
# 线上环境：安装redis/redis-stack-server
# 密码参数：-e REDIS_ARGS="--requirepass 密码"
docker run -d --name redis-stack-server -p 6379:6379 -e REDIS_ARGS="--requirepass 密码" redis/redis-stack-server:latest
# 使用redis-cli
docker exec -it redis-stack-server redis-cli
# 本地测试：安装redis/redis-stack
# redisinsight: http://localhost:8001
# docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 -e REDIS_ARGS="--requirepass 密码" redis/redis-stack:latest
#
# 将内容存储到/local-data/文件夹
# docker run -v /local-data/:/data redis/redis-stack:latest
# 使用local-redis-stack.conf替代默认redis-stack.conf配置文件
# docker run -v `pwd`/local-redis-stack.conf:/redis-stack.conf -p 6379:6379 -p 8001:8001 redis/redis-stack-server:latest
# 使用redis-cli
# docker exec -it redis-stack redis-cli
#
# 1.进入redis的容器：docker exec -it redis-stack bash
# 2.运行命令：redis-cli
# 3.查看现有的redis密码：config get requirepass
# 随机密码 https://suijimimashengcheng.bmcx.com/
# 4.设置 redis 密码：config set requirepass 密码
# docker container stop redis-stack # 停止 Redis 服务
# docker container start redis-stack # 开启 Redis 服务
# docker container restart redis-stack # 重启 Redis 服务
#
```

## Mac 安装 非docker版 [redis-stack](https://redis.io/docs/install/install-stack/mac-os/)

```bash
arch -arm64 brew tap redis-stack/redis-stack
arch -arm64 brew install redis-stack
# echo $PATH，输出结果：/opt/homebrew/bin
# 修改 ~/.zshrc
# export PATH=/opt/homebrew/Caskroom/redis-stack-server/<VERSION>/bin:$PATH
# 找到安装目录，修改conf文件，密码
# 启动
redis-stack-server
redisinsight
# 卸载
brew uninstall redis-stack-redisinsight redis-stack-server redis-stack
brew untap redis-stack/redis-stack
```
