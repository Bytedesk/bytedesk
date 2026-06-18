# odbc

## ubuntu 安装 odbc 驱动

```bash
sudo apt-get install unixodbc-dev mysql-connector-odbc
```

## 查看查看驱动信息

```bash
cat /etc/odbcinst.ini
# 内容如下
[MySQL ODBC 9.3 Unicode Driver]
DRIVER=/usr/lib/x86_64-linux-gnu/odbc/libmyodbc9w.so
UsageCount=1

[MySQL ODBC 9.3 ANSI Driver]
DRIVER=/usr/lib/x86_64-linux-gnu/odbc/libmyodbc9a.so
UsageCount=1
```

## 创建odbc配置文件

```bash
# 复制驱动：MySQL ODBC 9.3 Unicode Driver，填写下面DRIVER字段
cat >> /etc/odbc.ini <<EOF
[freeswitch]
DRIVER   = MySQL ODBC 9.3 Unicode Driver
SERVER   = 124.220.58.234
PORT     = 3306
DATABASE = db_name
USER     = root
PASSWORD = password
OPTION   = 3
```

## 测试连接

```bash
isql freeswitch -v
# 
+---------------------------------------+
| Connected!                            |
|                                       |
| sql-statement                         |
| help [tablename]                      |
| quit                                  |
|                                       |
+---------------------------------------+
```

## 参考链接

- [Using ODBC in the core](https://developer.signalwire.com/freeswitch/FreeSWITCH-Explained/Databases/ODBC-DSN/Using-ODBC-in-the-core_6586653)
