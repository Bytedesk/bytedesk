---
sidebar_label: Mysql
sidebar_position: 1
---

# Mysql 8.0

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置2核4G内存，推荐配置4核8G内存

:::

## 安装配置

- [下载mysql-apt-config_0.8.29-1_all.deb](https://dev.mysql.com/downloads/repo/apt/)

``` bash
- sudo dpkg -i mysql-apt-config_0.8.29-1_all.deb
- 弹出配置界面，选择mysql-8.0, 选择ok
- sudo apt-get update
- sudo apt-get install mysql-server
# 注：安装过程中需要设置密码，选择最新密码加密方式

# 下载Sequel Ace客户端远程连接MySQL
# 开启root远程访问，登录服务器
- mysql -u root -p # 按提示输入密码
- mysql> use mysql; # 进入mysql库
- mysql> update user set host='%' where user ='root'; # 更新域属性，'%'表示允许外部访问
- mysql> FLUSH PRIVILEGES;
- mysql> ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '密码'; # 修改密码
- mysql> FLUSH PRIVILEGES;
- mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; # 执行授权语句。执行完此句，外部已经可以通过账户密码访问了
- mysql> FLUSH PRIVILEGES;
- mysql> exit;
# 其他：
# FLUSH PRIVILEGES; 命令本质上的作用是：
# 将当前user和privilige表中的用户信息/权限设置从mysql库(MySQL数据库的内置库)中提取到内存里。
# MySQL用户数据和权限有修改后，希望在"不重启MySQL服务"的情况下直接生效，那么就需要执行这个命令。
# 
- sudo vi /etc/mysql/mysql.conf.d/mysqld.cnf # 将 bind-address = 127.0.0.1 注释 然后保存退出
# - service mysql restart
# 如果远程连接报错：Authentication plugin 'caching_sha2_password' cannot be loaded，则修改如下
# mysql> ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'password';
- 到阿里云 或 腾讯云 开启防火墙端口3306，允许外界可访问
```

创建数据库

``` bash
# 或者 使用SequalAce客户端创建数据库
# 命令创建数据库
mysql>CREATE DATABASE bytedesk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# 使用数据库
mysql>use bytedesk;
```
