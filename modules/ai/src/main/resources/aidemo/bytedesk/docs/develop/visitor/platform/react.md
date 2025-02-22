---
sidebar_label: React
sidebar_position: 2
---

# React

## 部分功能

- 微语官方技术支持
- 100%全部开源，支持自定义界面
- 支持人工客服
- 支持机器人
- 支持文字、图片、语音、表情
- 支持消息预知：对方正在输入
- 支持消息状态：送达、已读
- 支持消息撤回
- 对接第三方账号系统/多用户切换

## SDK源码及Demo下载

- [Gitee Demo](https://gitee.com/270580156/bytedesk-react)
- [Github Demo](https://github.com/Bytedesk/bytedesk-react)

## 安装

```bash
npm install bytedesk-react --save
# or
yarn add bytedesk-react
```

## 使用

```jsx
import { ChatFloat } from "bytedesk-react";
// 
<ChatFloat
    chatUrl="http://localhost:9006/chat?t=1&sid=default_wg_uid&"
    //buttonPosition: 'right', // botton position：left or right
    //buttonBackgroundColor: 'blue', // button background color
    //iframeWidth: 400,
    //iframeHeight: 600,
    //iframeMargins: { right: 20, bottom: 20, left: 20 }, // iframe margins
    //buttonMargins: { right: 20, bottom: 20, left: 20 }, // button margins
    //showButton: true, // show button or not
    //showIframe: true // show iframe or not
/>
// params:
chatUrl: 客服链接;
```

## 获取客服链接

- [登录管理后台](https://www.weiyuai.cn/admin/cs/wgroup)
