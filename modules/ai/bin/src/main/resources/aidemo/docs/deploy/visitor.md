---
sidebar_label: 访客端
sidebar_position: 7
---

# 访客端

## [Nginx](./depend/nginx)

本地部署可不需要，仅在生产环境推荐安装nginx，nginx做反向代理，

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
{
    "enabled": true,
    "apiUrl": "https://api.weiyuai.cn",
    "websocketUrl": "wss://api.weiyuai.cn/websocket",
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
{
    "enabled": true,
    "apiUrl": "http://127.0.0.1:9003",
    "websocketUrl": "ws://127.0.0.1:9885/websocket",
    "htmlUrl": "http://127.0.0.1:9006"
}
```
