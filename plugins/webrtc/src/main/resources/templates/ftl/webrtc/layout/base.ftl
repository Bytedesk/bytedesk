<#macro base title="微语 智能视频客服">
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    
    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="/css/webrtc/style.css" rel="stylesheet">
    
    <!-- 自定义样式 -->
    <style>
        .content {
            padding: 20px;
        }
    </style>
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="/webrtc">
                <i class="bi bi-camera-video"></i> 微语 智能视频客服
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <#--  <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link <#if (active!'') == 'dashboard'>active</#if>" href="/webrtc/dashboard">控制台</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <#if (active!'') == 'sessions'>active</#if>" href="/webrtc/sessions">视频会话</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <#if (active!'') == 'create'>active</#if>" href="/webrtc/create">发起会话</a>
                    </li>
                </ul>
            </div>  -->
        </div>
    </nav>

    <!-- 主要内容 -->
    <main class="container my-4">
        <#if message??>
            <div class="alert alert-${messageType!'info'} alert-dismissible fade show">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </#if>
        
        <#nested>
    </main>

    <!-- 页脚 -->
    <footer class="bg-light py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>微语 智能视频客服</h5>
                    <p>AI驱动的远程视频服务解决方案</p>
                </div>
                <div class="col-md-6 text-end">
                    <p>&copy; 2025 微语. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/webrtc/main.js"></script>
    
    <!-- 页面特定的JavaScript -->
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}"></script>
        </#list>
    </#if>
</body>
</html>
</#macro> 