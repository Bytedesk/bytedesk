<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>微语社交IM - 连接你的社区</title>
    <meta name="description" content="微语社交IM - 为社区打造专业的即时通讯解决方案">
    <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --discord-primary: #5865F2;
            --discord-dark: #36393f;
            --discord-darker: #2f3136;
            --discord-light: #dcddde;
        }
        
        body {
            background-color: var(--discord-dark);
            color: var(--discord-light);
        }

        .navbar {
            background-color: var(--discord-darker) !important;
        }

        .feature-box {
            background: var(--discord-darker);
            border-radius: 8px;
            padding: 2rem;
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .feature-box:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            border-color: var(--discord-primary);
        }

        .feature-icon {
            font-size: 2.5rem;
            color: var(--discord-primary);
            margin-bottom: 1rem;
        }

        .btn-discord {
            background-color: var(--discord-primary);
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 5px;
            transition: all 0.2s;
        }

        .btn-discord:hover {
            background-color: #4752c4;
            color: white;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <img src="/logo.png" alt="微语社交" height="30">
                <span class="text-white">微语社交</span>
            </a>
            <div class="d-flex">
                <#--  <a href="/login" class="btn btn-discord me-2">登录</a>  -->
                <#--  <a href="/register" class="btn btn-outline-light">注册</a>  -->
            </div>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">想象一个地方...</h1>
                <p class="lead">在这里，你可以属于学习小组、游戏社区或者全球艺术社区。这里有一个属于你的位置，让你找到归属。</p>
                <a href="/social/" class="btn btn-discord btn-lg mt-3">开始你的社区之旅</a>
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
                    <p>文字聊天、语音通话、视频会议，满足各种交流需求。</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🤖</div>
                    <h3>机器人助手</h3>
                    <p>智能机器人帮助管理社区、组织活动、提供娱乐功能。</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🎮</div>
                    <h3>游戏集成</h3>
                    <p>与好友一起玩游戏，分享游戏状态，组织比赛活动。</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🔒</div>
                    <h3>安全可靠</h3>
                    <p>端到端加密，角色权限管理，确保社区安全。</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📱</div>
                    <h3>随时随地</h3>
                    <p>支持多平台，Web、PC、移动端随时保持联系。</p>
                </div>
            </div>
        </div>
    </div>

    <footer class="py-4 mt-5" style="background-color: var(--discord-darker)">
        <div class="container text-center">
            <p class="mb-0">© 2025 微语社交. All rights reserved.</p>
            <p class="text-muted">打造你的专属社区</p>
        </div>
    </footer>

    <script src="/assets/vendor/jquery-3.7.1.min.js"></script>
    <script src="/assets/vendor/bootstrap5/js/bootstrap.bundle.min.js"></script>
</body>
</html>