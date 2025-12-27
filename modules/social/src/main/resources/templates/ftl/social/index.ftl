<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]>      <html class="no-js"> <!--<![endif]-->
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>微语社交IM - 连接你的社区</title>
    <meta name="description" content="微语社交IM - 为社区打造专业的即时通讯解决方案">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-blue: #1a73e8;
            --primary-text: #333333;
            --light-bg: #ffffff;
            --hover-shadow: rgba(0, 0, 0, 0.1);
        }
        
        body {
            background-color: var(--light-bg);
            color: var(--primary-text);
        }

        .navbar {
            background-color: var(--primary-blue) !important;
        }

        .feature-icon {
            font-size: 2.5rem;
            color: var(--primary-blue);
            margin-bottom: 1rem;
        }

        .feature-box {
            padding: 2rem;
            border-radius: 10px;
            background: var(--light-bg);
            transition: all 0.3s ease;
            border: 1px solid #eee;
        }

        .feature-box:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px var(--hover-shadow);
            border-color: var(--primary-blue);
        }

        .btn-primary {
            background-color: var(--primary-blue);
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 5px;
            transition: all 0.2s;
        }

        .btn-primary:hover {
            background-color: #1557b0;
            color: white;
        }

        .browsehappy {
            margin: 0.2em 0;
            background: #ccc;
            color: #000;
            padding: 0.2em 0;
        }

        .lead {
            color: #666;
        }

        footer {
            background-color: #f8f9fa;
            color: #666;
        }
    </style>
</head>
<body>
    <!--[if lt IE 7]>
        <p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="#">upgrade your browser</a> to improve your experience.</p>
    <![endif]-->
    
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <img src="/logo.png" alt="微语社交" height="30">
                <span class="text-white">微语社交</span>
            </a>
            <div class="d-flex">
                <#--  <a href="/login" class="btn btn-light me-2">登录</a>  -->
                <#--  <a href="/register" class="btn btn-outline-light">注册</a>  -->
            </div>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">想象一个地方...</h1>
                <p class="lead">在这里，你可以属于学习小组、游戏社区或者全球艺术社区。这里有一个属于你的位置，让你找到归属。</p>
                <a href="/social/" class="btn btn-primary btn-lg mt-3">开始你的社区之旅</a>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🌍</div>
                    <h3>创建你的社区</h3>
                    <p>轻松创建和管理你的服务器，邀请成员加入，建立属于你的社区文化。</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">💬</div>
                    <h3>多样化频道</h3>
                    <ul class="list-unstyled">
                        <li>文字频道</li>
                        <li>语音频道</li>
                        <li>视频频道</li>
                        <li>资源分享</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🤖</div>
                    <h3>智能助手</h3>
                    <ul class="list-unstyled">
                        <li>欢迎机器人</li>
                        <li>管理机器人</li>
                        <li>音乐机器人</li>
                        <li>自定义功能</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🎮</div>
                    <h3>社交功能</h3>
                    <ul class="list-unstyled">
                        <li>表情反应系统</li>
                        <li>@提及功能</li>
                        <li>回复threading</li>
                        <li>投票系统</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🛡️</div>
                    <h3>安全管理</h3>
                    <ul class="list-unstyled">
                        <li>角色权限控制</li>
                        <li>内容审核</li>
                        <li>敏感词过滤</li>
                        <li>用户举报系统</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📱</div>
                    <h3>多平台支持</h3>
                    <ul class="list-unstyled">
                        <li>Web端</li>
                        <li>PC客户端</li>
                        <li>移动端APP</li>
                        <li>数据同步</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <footer class="py-4 mt-5">
        <div class="container text-center">
            <p class="mb-0">© 2025 微语. All rights reserved.</p>
            <p class="text-muted">北京微语天下科技有限公司</p>
        </div>
    </footer>

    <script src="/assets/vendor/jquery-3.7.1.min.js"></script>
    <script src="/assets/vendor/bootstrap5/js/bootstrap.bundle.min.js"></script>
</body>
</html>