---
sidebar_label: Postgresql
sidebar_position: 1
---

# PostgreSQL 16

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

## 安装

```bash
# ubuntu
# https://www.postgresql.org/download/linux/ubuntu/
sudo apt install -y postgresql-common
sudo /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh
# Import the repository signing key:
sudo apt install curl ca-certificates
sudo install -d /usr/share/postgresql-common/pgdg
sudo curl -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc --fail https://www.postgresql.org/media/keys/ACCC4CF8.asc
# Create the repository configuration file:
sudo sh -c 'echo "deb [signed-by=/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc] https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
# Update the package lists:
sudo apt update
# Install the latest version of PostgreSQL:
# If you want a specific version, use 'postgresql-16' or similar instead of 'postgresql'
sudo apt -y install postgresql
# 查看版本号
psql --version
# 查看是否已经启动
lsof -i:5432
# 如需要本地客户端连接云服务，到腾讯云或阿里云防火墙开放端口号：5432
# 查找配置文件路径
locate postgresql.conf
# /etc/postgresql/16/main/postgresql.conf
# 为方便修改将/etc/postgresql/16/main/路径下所有配置文件下载到本地修改
# 开启外网访问，修改 postgresql.conf 文件
listen_addresses = '*'
# 修改 pg_hba.conf 文件，文件末尾添加如下内容：
host    all             all             0.0.0.0/0               scram-sha-256
# 修改密码
sudo -u postgres psql
ALTER USER postgres WITH PASSWORD 'password'; # 修改密码 https://suijimimashengcheng.bmcx.com/
# 按 \q 退出
# 安装 pgvector
apt install postgresql-16-pgvector
# 将修改后的配置文件上传到服务器，然后重启
service postgresql restart
# service postgresql stop
# 查看端口 5432
lsof -i:5432
# 或者
netstat -tunlp | grep 5432
# 使用pgadmin客户端, 桌面客户端远程连接
# 创建数据库 bytedesk
# 给刚创建的数据库bytedesk添加扩展vector（右键扩展，创建->General->名称：vector）
```
