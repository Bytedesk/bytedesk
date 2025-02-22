---
sidebar_label: Mysql-Cluster
sidebar_position: 1
---

# Mysql-Cluster


## 主从复制

首先在主、从服务器上分别安装MySQL

首先，在主服务器执行

``` bash
# mysql-bin log存储目录
/var/lib/mysql
# master
vi /etc/mysql/mysql.conf.d/mysqld.cnf
# 注释掉bind-address = 127.0.0.1
#需要备份的数据库
# binlog-do-db=bytedesk
#不需要备份的数据库
# binlog-ignore-db=mysql
# 开启二进制日志功能，可以随便取，最好有含义（关键就是这里了）
log-bin=mysql-bin
# 设置server_id，一般设置为IP,注意要唯一
server_id=1
# 设置bin文件保存时间为3天
expire_logs_days=3
# 进入命令console
mysql -u root -p
# 创建数据同步用户，并赋予权限
mysql> CREATE USER 'bytedesk'@'%' IDENTIFIED BY 'password';
mysql> GRANT REPLICATION SLAVE ON *.* TO 'bytedesk'@'%';
mysql> flush privileges;
mysql> exit;
# 重启Mysqld服务
service mysql restart
```

其次，在从服务器执行

``` bash
# slave
vi /etc/mysql/mysql.conf.d/mysqld.cnf
# log-bin=mysql-bin   #[可选]启用二进制日志
# 设置server_id，一般设置为IP,注意要唯一
server_id=2
# 设置bin文件保存时间为3天
expire_logs_days=3
max_connect_errors=10000
max_connections=1000
# 重启
service mysql restart
```

第三步，在主服务器执行

``` bash
# 登录 master console
mysql -u root -p
# 查看状态
mysql> show master status;
```

第四步，在从服务器执行

``` bash
# 主服务器将RSA公钥发送给从服务器，后者使用它来加密密码并将结果返回给服务器
mysql --ssl-mode=DISABLED -h 121.199.165.116 -ubytedesk -ppassword --get-server-public-key
mysql> exit;
# 报错：Host '114.55.33.206' is blocked because of many connection errors; unblock with 'mysqladmin flush-hosts'
# 在mysql master上面执行：
# show variables like "%max_connect%";
# set global max_connections=1000;
# set global mysqlx_max_connections=1000;
# set global max_connect_errors=10000;
# which mysqladmin
# /usr/bin/mysqladmin flush-hosts -h 127.0.0.1 -u root -p密码
# mysql -u root -p
# flush hosts;
# 注意：必须首先执行上面语句，否则会报错：
# Last_IO_Error: error connecting to master 'bytedesk@121.199.165.116:3306' - retry-time: 30 retries: 1 message: Authentication plugin 'caching_sha2_password' reported error: Authentication requires secure connection.
# https://blog.csdn.net/wawa8899/article/details/86689618
# https://www.modb.pro/db/29919
# https://www.cnblogs.com/summertime-wu/p/11637520.html
# 登录 slave console
# mysqladmin flush-host -h 127.0.0.1 -u root -p密码
# 
# select * from performance_schema.replication_applier_status_by_worker \G;
mysql -u root -p
# flush hosts;
# 根据第三步中的信息，修改下面命令
mysql> change master to master_host='121.199.165.116', master_user='bytedesk', master_password='password', master_port=3306, master_log_file='mysql-bin.001566', master_log_pos=43471610, master_connect_retry=30;
# 启动 slave 从库
mysql> start slave;
# 查看 slave 从库
mysql> show slave status\G; # 注意：末尾的\G用来格式化显示，增加易读性
#停止主从复制
#清空之前的主从复制配置信息
# mysql> stop slave;
# mysql> reset slave;
mysql> exit;
# 若Slave_IO_Running和Slave_SQL_Running均为Yes，则表示连接正常。
# 如果遇到bug，利用配置参数 来躲避这个bug
# vi /etc/mysql/my.cnf
# 添加相应错误码
# [mysqld]
# slave-skip-errors = 1032,1062
# 然后重启
# service mysql restart
# 或直接跳过，下面数字1可以修改为任意数字
# stop slave;set global sql_slave_skip_counter=1;start slave;
# 
```

第五步

``` bash
# 测试
在master执行插入、更新、删除等操作，在slave查看是否同步
```

常用命令

``` bash
# 查看主服务器的运行状态
mysql> show master status;
+------------------+----------+--------------+------------------+-------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB | Executed_Gtid_Set |
+------------------+----------+--------------+------------------+-------------------+
| mysql-bin.000253 | 46568304 |              |                  |                   |
+------------------+----------+--------------+------------------+-------------------+
1 row in set (0.00 sec)
# 查看从服务器主机列表
mysql> show slave hosts;
# 获取binlog文件列表
mysql> show binary logs;
# 只查看第一个binlog文件的内容
mysql> show binlog events;
# 查看指定binlog文件的内容
mysql> show binlog events in 'mysql-bin.000001';
# 启动从库复制线程
mysql> START SLAVE;
# 停止从库复制线程
mysql> STOP SLAVE;
```

## 读写分离

- TODO: sharding-jdbc

## 支持JNDI数据源

如果想使用Tomcat或者WebLogic的数据源，则不需要配置`spring.datasource.url`等参数，只需要配置`spring.datasource.jndi-name`为JNDI的name即可，如：

``` bash
spring.datasource.jndi-name=java:jdbc/mysql
```

<!-- TODO: 多数据源 -->
<!-- TODO: 分表、分库 -->
<!-- <img :src="$withBase('/image/qrcode_xiaperio_430.jpg')" style="width:250px;"/> -->

## 参考

* [Docker](https://hub.docker.com/_/mysql)
* [官方指南](https://dev.mysql.com/doc/mysql-apt-repo-quick-guide/en/)
* [下载](https://dev.mysql.com/downloads/repo/apt/)
* [Connection to a JNDI DataSource](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-connecting-to-a-jndi-datasource)
* [参考](https://www.cnblogs.com/zhangxuel1ang/p/13456116.html)
