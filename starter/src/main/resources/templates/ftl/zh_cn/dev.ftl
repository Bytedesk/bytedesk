<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="/favicon.ico">
    <title>微语</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
            background-color: #f8f9fa;
            color: #333;
            line-height: 1.6;
            padding: 2rem;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
        }

        h3 {
            color: #2c3e50;
            margin: 1.5rem 0 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #eee;
            font-weight: 600;
        }

        a {
            color: #3498db;
            text-decoration: none;
            padding: 0.5rem 0;
            display: inline-block;
            transition: color 0.2s;
        }

        a:hover {
            color: #2980b9;
            text-decoration: underline;
        }

        .section {
            margin-bottom: 2rem;
        }

        .section:last-child {
            margin-bottom: 0;
        }

        .section-title {
            font-size: 1.25rem;
            color: #2c3e50;
            margin-bottom: 1rem;
        }

        .link-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .link-list li {
            margin-bottom: 0.5rem;
        }

        .link-list a {
            display: inline-flex;
            align-items: center;
        }

        .link-list a:before {
            content: "→";
            margin-right: 0.5rem;
            color: #95a5a6;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="section">
            <a href="/" target="_blank">微语官网</a>
            <p>
                用户名: admin@email.com, 密码: admin
            </p>
        </div>

        <div class="section">
            <h3>快速入口</h3>
            <ul class="link-list">
                <li><a href="/admin/" target="_blank">管理后台</a></li>
                <li><a href="/agent/chat" target="_blank">客服工作台</a></li>
                <li><a href="/chat/demo" target="_blank">访客对话</a></li>
                <li><a href="/agenticflow/" target="_blank">工单系统</a></li>
                <li><a href="/notebase/spaces" target="_blank">知识库</a></li>
            </ul>
        </div>

        <div class="section">
            <h3>开发工具</h3>
            <ul class="link-list">
                <li><a href="/swagger-ui/index.html" target="_blank">API 文档</a></li>
                <#--  <li><a href="/actuator" target="_blank">系统监控</a></li>  -->
                <li><a href="/druid" target="_blank">数据库监控</a></li>
            </ul>
        </div>
    </div>
</body>
</html>