<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!'ByteDesk 呼叫中心'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="/css/callcenter/style.css" rel="stylesheet">
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="/callcenter">ByteDesk 呼叫中心</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/callcenter">首页</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/callcenter/calls">呼叫记录</a>
                    </li>
                    <#if currentUser??>
                        <li class="nav-item">
                            <a class="nav-link" href="/callcenter/user/calls">我的呼叫</a>
                        </li>
                        <#if currentUser.hasRole('ADMIN')>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                                    管理
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="/callcenter/admin/monitor">呼叫监控</a></li>
                                    <li><a class="dropdown-item" href="/callcenter/admin/statistics">统计报表</a></li>
                                </ul>
                            </li>
                        </#if>
                    </#if>
                </ul>
                <div class="navbar-nav">
                    <#if currentUser??>
                        <div class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                                ${currentUser.username}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="/profile">个人资料</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="/logout">退出</a></li>
                            </ul>
                        </div>
                    <#else>
                        <a class="nav-link" href="/login">登录</a>
                        <a class="nav-link" href="/register">注册</a>
                    </#if>
                </div>
            </div>
        </div>
    </nav>

    <main class="container my-4">
        <#nested>
    </main>

    <!-- 页脚 -->
    <footer class="bg-light py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>ByteDesk 呼叫中心</h5>
                    <p>专业的呼叫中心解决方案</p>
                </div>
                <div class="col-md-6 text-end">
                    <p>&copy; 2025 ByteDesk. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/callcenter/main.js"></script>
</body>
</html>