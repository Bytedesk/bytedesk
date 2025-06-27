# Nginx

- [文档](https://www.weiyuai.cn/docs/zh-CN/docs/deploy/source)

:::tip

- 操作系统：Ubuntu 20.04 LTS
- 服务器最低配置 2 核 4G 内存，推荐配置 4 核 8G 内存。

:::

## 安装

```bash
sudo apt update
sudo apt install nginx
# 查看是否安装成功
netstat -ntlp
# 如果80端口正常启动，则证明安装成功
# 停止nginx
# service nginx stop
# 启动nginx
# service nginx start
# 重启nginx:
# service nginx restart
# systemctl restart nginx
# 重新加载：
# service nginx force-reload
```

```bash
# 查看是否安装stream模块
nginx -V | grep stream # 注意是大写V
# 有输出内容证明已经安装
nginx version: nginx/1.18.0 (Ubuntu)
# ...
# 可以看到参数：--with-stream=dynamic，说明已经安装stream模块
# 对应报错：unknown directive "stream" in /etc/nginx/nginx.conf，需要在nginx.conf的第一行插入
# load_module /usr/lib/nginx/modules/ngx_stream_module.so;
# 缓存路径，创建文件夹，在nginx.conf文件中用到
mkdir -p /var/www/html/nginx/cache/webserver
# 重新加载配置文件
nginx -s reload
# 或者 重启nginx
service nginx restart
```

## 准备

- 将下载的 [server](https://www.weiyuai.cn/download/weiyu-server.zip) 文件解压，解压后的文件结构如下

```bash
(base) server % tree -L 1
.
├── admin
├── agent
├── bytedesk-starter-0.4.0.jar
├── chat
├── config
├── logs
├── readme.md
├── readme.zh.md
├── start.bat
├── start.sh
├── stop.bat
├── stop.sh
└── uploader

7 directories, 7 files
```

- 将其中的 admin，agent，chat 三个文件夹复制到 /var/www/html/weiyuai/ 文件夹下。
- 其中：admin 为管理后台，agent 为客户端，chat 为访客端
- 三者默认访问的服务器地址为: http://127.0.0.1:9003, 发布到线上时需要修改才能够正常使用，具体修改方法如下：
- 找到 admin/config.json 、 agent/config.json 和 chat/config.json 三个文件
- config.json 文件内容如下：

```json
// admin、agent config.json
{
    "enabled": false,
    "apiUrl": "https://api.weiyuai.cn",
    "websocketUrl": "wss://api.weiyuai.cn/websocket",
    "htmlUrl": "https://www.weiyuai.cn"
}
// visitor config.json
{
    "enabled": false,
    "apiUrl": "https://api.weiyuai.cn",
    "websocketUrl": "wss://api.weiyuai.cn/stomp",
    "htmlUrl": "https://www.weiyuai.cn"
}
```

- enabled 字段为是否启用自定义服务器地址，默认为 false。这里需要将 false 改为 true。只有修改为 true，下面的 apiHost 和 htmlHost 才能生效
- apiUrl 字段为 api 地址，默认为：api.weiyuai.cn，请替换为自己的域名
- websocketUrl 字段为 websocket 地址，默认为：ws://api.weiyuai.cn/websocket，请替换为自己的域名
- htmlHost 字段为静态网页地址，默认为：www.weiyuai.cn，请替换为自己的域名 

## 替换为ip实例

- 将域名替换为ip
- 将https替换为http

```json
// admin、agent config.json
{
    "enabled": false,
    "apiUrl": "http://127.0.0.1:9003",
    "websocketUrl": "ws://127.0.0.1:9885/websocket",
    "htmlUrl": "http://127.0.0.1:9003"
}

// visitor config.json
{
    "enabled": false,
    "apiUrl": "http://127.0.0.1:9003",
    "websocketUrl": "ws://127.0.0.1:9003/stomp",
    "htmlUrl": "http://127.0.0.1:9003"
}

```

## nginx.conf

在nginx.conf文件中http模块添加如下内容：

```bash
#...
http {
    ##...
    
    ## restapi-负载均衡
    upstream weiyuai {
        # round_robin; # 默认，轮流分配
        ip_hash; # 同一个ip访问同一台服务器, 这样来自同一个IP的访客固定访问一个后端服务器
        # least_conn; # 公平分配
        # server 172.16.81.2:9003     weight=2 max_fails=10 fail_timeout=60s;
        server 127.0.0.1:9003 weight=2 max_fails=10 fail_timeout=60s;
    }

    # websocket-负载均衡
    upstream weiyuaiwss {
        # round_robin; # 默认，轮流分配
        ip_hash; # 同一个ip访问同一台服务器, 这样来自同一个IP的访客固定访问一个后端服务器
        # least_conn; # 公平分配
        # server 172.16.81.2:9885     weight=2 max_fails=10 fail_timeout=60s;
        server 127.0.0.1:9885 weight=2 max_fails=10 fail_timeout=60s;
    }

    include /etc/nginx/conf.d/*.conf;
    include /etc/nginx/sites-enabled/*;
}
```

## sites-available

在sites-available文件夹下创建4个文件，如下：

### weiyuai_cn_80.conf

- 需要修将 server_name weiyuai.cn *.weiyuai.cn; 改为自己的域名或者IP地址

```bash
# weiyuai_cn_80.conf内容
server {
    listen 80;
    listen [::]:80;

    root /var/www/html/weiyuai/;
    index index.html index.htm index.nginx-debian.html index.php;

    server_name weiyuai.cn *.weiyuai.cn;

    location / {
        # 匹配所有路径，并尝试首先提供文件，然后目录，最后回退到index.html
        try_files $uri $uri/ /index.html; # 这里应该指向根目录的index.html，而不是特定路径下的index.html
    }

    # 如果需要为每个子路径提供特定的index.html，您可以添加额外的location块
    location /admin/ {
        try_files $uri $uri/ /admin/index.html;
    }

    location /agent/ {
        try_files $uri $uri/ /agent/index.html;
    }

    location /chat/ {
        try_files $uri $uri/ /chat/index.html;
    }

    location /frame/ {
        try_files $uri $uri/ /chat/index.html;
    }
}
```

### weiyuai_cn_443.conf

- 可选，仅有启用ssl的情况下需要
- 需要修将 server_name weiyuai.cn *.weiyuai.cn; 改为自己的域名或者IP地址
- 443端口配置，需要ssl证书，这里使用的是Let's Encrypt的免费SSL证书
- 需要修改ssl证书的路径

```bash
# weiyuai_cn_443.conf内容
server {
	listen 443 ssl;
	listen [::]:443 ssl;

	ssl_certificate /etc/letsencrypt/live/weiyuai.cn/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/weiyuai.cn/privkey.pem; # managed by Certbot

	server_name weiyuai.cn *.weiyuai.cn;

	root /var/www/html/weiyuai;
	index index.html index.htm index.nginx-debian.html index.php;

	location / {
        # 匹配所有路径，并尝试首先提供文件，然后目录，最后回退到index.html
        try_files $uri $uri/ /index.html; # 这里应该指向根目录的index.html，而不是特定路径下的index.html
    }

    # 如果需要为每个子路径提供特定的index.html，您可以添加额外的location块
    location /admin/ {
        try_files $uri $uri/ /admin/index.html;
    }

    location /agent/ {
        try_files $uri $uri/ /agent/index.html;
    }

    location /chat/ {
        try_files $uri $uri/ /chat/index.html;
    }

    location /frame/ {
        try_files $uri $uri/ /chat/index.html;
    }

    location /docs/ {
        try_files $uri $uri/ /docs/index.html;
    }
}
```

### weiyuai_cn_api_80.conf

- 需要修将 server_name api.weiyuai.cn; 改为自己的域名或者IP地址

```bash
# weiyuai_cn_api_80.conf内容
#
# http://api.weiyuai.cn
server {
	listen 80;
	listen [::]:80;

    server_name api.weiyuai.cn;

    # 直接将所有请求代理到Spring Boot
    location / {
        proxy_pass http://weiyuai;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        include fastcgi_params;
    }

    ## 反向代理
    # https代理stomp连接
    location /stomp {
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass http://weiyuai/stomp;

        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;
    }

    ## 反向代理
    # https代理websocket连接
    location /websocket {
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass http://weiyuaiwss/websocket;

        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;
    }

    #增加两头部
    add_header X-Via $server_addr;
    add_header X-Cache $upstream_cache_status;

    ## 反向代理
    location @springboot {
		# 将nginx所有请求均跳转到9003端口
        proxy_pass http://weiyuai;
        
        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        #  X-Real-IP 让日志的IP显示真实的客户端的IP
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;

        # 设置缓存
        # 为应答代码为200和302的设置缓存时间为10分钟，404代码缓存10分钟。
        #proxy_cache webserver;
        # proxy_cache_valid  200 302  10m;
        proxy_cache_valid  404      10m;
	}
}
```

### weiyuai_cn_api_443.conf

- 可选，仅有启用ssl的情况下需要
- 需要修将 server_name api.weiyuai.cn; 改为自己的域名或者IP地址
- 443端口配置，需要ssl证书，这里使用的是Let's Encrypt的免费SSL证书
- 需要修改ssl证书的路径

```bash
# weiyuai_cn_api_443.conf内容
server {
	listen 443 ssl;
	listen [::]:443 ssl;

	ssl_certificate /etc/letsencrypt/live/weiyuai.cn/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/weiyuai.cn/privkey.pem; # managed by Certbot

	server_name api.weiyuai.cn;

    # 直接将所有请求代理到Spring Boot
    location / {
        proxy_pass http://weiyuai;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        include fastcgi_params;
    }

    ## 反向代理
    # https代理stomp连接
    location /stomp {
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass http://weiyuai/stomp;

        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;
    }

    ## 反向代理
    # https代理websocket连接
    location /websocket {
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_pass http://weiyuaiwss/websocket;

        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;
    }

    #增加两头部
    add_header X-Via $server_addr;
    add_header X-Cache $upstream_cache_status;

    ## 反向代理
    location @springboot {
		# 将nginx所有请求均跳转到9003端口
        proxy_pass http://weiyuai;

        # add_header Access-Control-Allow-Origin *; # 报错，不能添加，需要在spring boot中去掉相应的origin
        # 为记录真实ip地址，而不是反向代理服务器地址
        proxy_set_header  Host            $host;
        #  X-Real-IP 让日志的IP显示真实的客户端的IP
        proxy_set_header  X-Real-IP       $remote_addr;
        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
        include           fastcgi_params;

        # 设置缓存
        # 为应答代码为200和302的设置缓存时间为10分钟，404代码缓存10分钟。
        #proxy_cache webserver;
        #proxy_cache_valid  200 302  10m;
        proxy_cache_valid  404      10m;
	}
}
```

## 创建软链接

```bash
# 删除默认的软连接（可选）
sudo unlink /etc/nginx/sites-enabled/default

# 方法1：使用 && 连接多个命令
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_80.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_443.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_api_80.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_api_443.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_demo_80.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_demo_443.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_sip_80.conf /etc/nginx/sites-enabled/ && \
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_sip_443.conf /etc/nginx/sites-enabled/

# 方法2：使用 for 循环（推荐）
for conf in weiyuai_cn_80 weiyuai_cn_443 weiyuai_cn_api_80 weiyuai_cn_api_443 weiyuai_cn_demo_80 weiyuai_cn_demo_443 weiyuai_cn_sip_80 weiyuai_cn_sip_443; do
  sudo ln -s /etc/nginx/sites-available/${conf}.conf /etc/nginx/sites-enabled/
done

# 方法3：使用通配符（如果文件名有规律）
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_*.conf /etc/nginx/sites-enabled/
```

## 使配置生效

```bash
# 检查nginx配置文件是否有语法错误
sudo nginx -t
# 重新加载nginx配置
sudo nginx -s reload
# 或
sudo systemctl reload nginx
# 查看访问日志
tail -f /var/log/nginx/access.log
# 查看报错日志
tail -f /var/log/nginx/error.log
```

## 对外开放端口

```bash
# 对外开放端口号
http：80
https：443
# 可选，可不对外开放
mysql：3306
redis：6379
# rest api：9003
# websocket：9885
```

<!-- 

-->

## TCP 连接数修改（可选）

```bash
# 查看Linux系统用户最大打开的文件限制
ulimit -n
# 65535
# 修改打开文件限制
vi /etc/security/limits.conf
root soft nofile 655350
root hard nofile 655350
nginx soft nofile 6553500
nginx hard nofile 6553500
* soft nofile 655350
* hard nofile 655350
# 其中root指定了要修改哪个用户的打开文件数限制。
# 可用'*'号表示修改所有用户的限制；soft或hard指定要修改软限制还是硬限制；
# 102400则指定了想要修改的新的限制值，即最大打开文件数(请注意软限制值要小于或等于硬限制)
# 注意：修改了/etc/security/limits.conf，关闭Terminal重新登录或重启服务器生效
# 查看 open files数
ulimit -a
```

## 常见问题

```shell
# 查看nginx log
cd /var/log/nginx
```

## 参考

- [letsencrypt](https://letsencrypt.org/)
- [LetsEncrypt 通配符证书](https://www.jianshu.com/p/c5c9d071e395)
- [Ubuntu /etc/security/limits.conf 不生效问题](https://www.cnblogs.com/xiao987334176/p/11008812.html)
