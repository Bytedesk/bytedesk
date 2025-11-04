# ActiveMQ Artemis

## 下载

- [artemis](https://activemq.apache.org/components/artemis/download/)

## 安装

```shell
# 解压到apache-artemis-2.37.0
# tar -zxvf apache-artemis-2.37.0-bin.tar.gz
# artemis需要创建一个文件夹，cd到目标文件夹，执行以下命令，创建mybroker文件夹
./apache-artemis-2.37.0/bin/artemis create mybroker
# 输入用户名和密码: admin/admin, y
# 前台启动
./mybroker/bin/artemis run
# 后台启动
./mybroker/bin/artemis-service start
# 停止
./mybroker/bin/artemis stop
# 查看状态
./mybroker/bin/artemis-service status
# 查看日志
./mybroker/bin/artemis-service log
# 查看帮助
# 配置
./apache-artemis-2.37.0/bin/artemis help create
# 修改端口号，本地冲突，将默认端口号+1
vim mybroker/etc/broker.xml
# web管理界面
http://localhost:8161
```

## 参考

1. [ActiveMQ Artemis](https://activemq.apache.org/components/artemis/documentation/latest/)
