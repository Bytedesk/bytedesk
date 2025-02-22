<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-19 22:51:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-24 09:48:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Nginx

## 安装

``` bash
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
# 测试 Nginx 配置文件，确保所做的修改没有错误
# sudo nginx -t
# 如果没有错误，运行以下命令让 Nginx 重新载入修改后的配置文件
# sudo nginx -s reload
```

```bash
# 查看是否安装stream模块
nginx -V | grep stream # 注意是大写V
# 有输出内容证明已经安装
# 缓存路径，创建文件夹，在nginx.conf文件中用到
mkdir -p /var/www/html/nginx/cache/webserver
# 对外开放端口号
http：80
https：443
# 另外
mysql：3306
redis：6379
# spring boot 端口：
9003
# 9883
9885
```

## 配置

``` bash
# 如果下载文件报错：
 open() "/root/weiyuai/uploader/20240821080529_weiyuai.exe" failed (13: Permission denied),
# 修改 nginx.conf 的 
# user www-data;
# 为
# user root;
# 然后重启
# 查看nginx进程
ps -aux | grep nginx
# 将 nginx.conf和sites-available文件夹，直接上传到/etc/nginx/目录下覆盖原有文件
# 重新加载配置文件
nginx -s reload
# 或者 重启nginx
service nginx restart
sudo systemctl restart nginx
```

```bash
# 创建软连接
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_80.conf /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_443.conf /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_api_80.conf /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/weiyuai_cn_api_443.conf /etc/nginx/sites-enabled/
# 删除软连接
sudo unlink /etc/nginx/sites-enabled/weiyuai_cn_80.conf
sudo unlink /etc/nginx/sites-enabled/weiyuai_cn_443.conf
sudo unlink /etc/nginx/sites-enabled/weiyuai_cn_api_80.conf
sudo unlink /etc/nginx/sites-enabled/weiyuai_cn_api_443.conf
# 
sudo unlink /etc/nginx/sites-enabled/default
# 查看软连接
ls -l /etc/nginx/sites-enabled/
# 重新加载nginx配置
sudo nginx -s reload
# 或
sudo systemctl reload nginx
```
