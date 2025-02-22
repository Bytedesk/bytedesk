---
sidebar_label: 登录
sidebar_position: 1
---

# 登录

## 方法一：自定义服务器

- 登录管理后台
- 点击左侧菜单栏的`设置` -》`服务器设置` -》复制 服务器地址
- 打开客户端，登录界面，点击`自定义服务器`，粘贴服务器地址，点击`保存`
- 返回登录界面，输入用户名和密码即可

## 方法二：Web 版客户端

使用方法一获取到服务器地址

- 找到 agent/config.json 文件，默认格式如下：

```json
{
    "enabled": false, // false 改为 true。只有修改为 true，下面的 apiHost 和 htmlHost 才能生效
    "apiHost": "api.weiyuai.cn", // 重要：改为线上 api 地址，如: api.example.com，不能够以 http 开头
    "htmlHost": "www.weiyuai.cn" // 修改为访问静态网页地址，如: www.example.com，不能够以 http 开头
}
```

将 apiHost 和 htmlHost 替换为服务器地址即可
