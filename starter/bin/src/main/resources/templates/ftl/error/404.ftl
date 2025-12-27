<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 15:14:06
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-13 11:51:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
<!DOCTYPE html>
<html lang="en">

<head>

    <title>404 -微语 - 开源智能客服系统 - 企业即时通讯、在线客服系统、大模型AI助手</title>

    <meta charset="UTF-8">
    <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="description" content="微语 - 开源智能客服系统 - 企业即时通讯、在线客服系统、大模型AI助手">
    <meta name="keywords"
        content="ByteDesk.com 微客服 在线客服系统 微信客服系统 智能客服系统 即时通讯云 企业协作系统 工单系统 App android ios 苹果 安卓 微信 微信公众平台 微服务 android iOS 微客服 App客服  App在线客服 App 在线咨询 移动开发者服务平台 微客服  在线客服  微博 新浪微博 微信 微语 聊天 移动聊天 免费 发信息 发图片 语音 weixin 离线消息 微博 私信 流量" />
    <link rel="icon" href="https://cdn.bytedesk.com/favicon.ico" type="image/x-icon">
</head>

<body>

    <div id="back">
        <a href="/">loading</a>
    </div>

    <script>
        if (window.location.href.indexOf('/admin') != -1) {
            // react打包发布之后，刷新页面会报404，nginx配置"try_files $uri /index.html"无效，暂时重定向到/admin
            window.location.href = '/admin';
        } else if (window.location.href.indexOf('/agent') != -1 ) {
            // web客服端
            window.location.href = '/agent';
        } else if (window.location.href.indexOf('/chat') != -1 ) {
            // 访客对话窗口
            window.location.href = '/chat';
        } else {
            window.location.href = '/dev';
        }
    </script>
</body>

</html>