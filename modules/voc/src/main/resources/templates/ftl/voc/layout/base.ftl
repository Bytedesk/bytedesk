<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!'ByteDesk VOC'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css" rel="stylesheet">
    <link href="/css/voc/style.css" rel="stylesheet">
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="/voc">ByteDesk VOC</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/voc">首页</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/voc/feedback/create">提交反馈</a>
                    </li>
                    <#if currentUser??>
                        <li class="nav-item">
                            <a class="nav-link" href="/voc/user/feedbacks">我的反馈</a>
                        </li>
                        <#if currentUser.hasRole('ADMIN')>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                                    管理
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item" href="/voc/admin/pending">待处理反馈</a></li>
                                    <li><a class="dropdown-item" href="/voc/admin/assigned">分配给我的</a></li>
                                </ul>
                            </li>
                        </#if>
                    </#if>
                </ul>
                <div class="d-flex">
                    <#if currentUser??>
                        <div class="dropdown">
                            <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                ${currentUser.username}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="/voc/user/profile">个人中心</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="/logout" method="POST" class="dropdown-item">
                                        <button type="submit" class="btn btn-link p-0">退出登录</button>
                                    </form>
                                </li>
                            </ul>
                        </div>
                    <#else>
                        <a href="/login" class="btn btn-outline-light">登录</a>
                        <a href="/register" class="btn btn-light ms-2">注册</a>
                    </#if>
                </div>
            </div>
        </div>
    </nav>

    <!-- 主要内容 -->
    <main class="container my-4">
        <#nested>
    </main>

    <!-- 页脚 -->
    <footer class="bg-light py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>ByteDesk VOC</h5>
                    <p>您的声音，我们的进步</p>
                </div>
                <div class="col-md-6 text-end">
                    <p>&copy; 2024 ByteDesk. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/voc/main.js"></script>
</body>
</html> 