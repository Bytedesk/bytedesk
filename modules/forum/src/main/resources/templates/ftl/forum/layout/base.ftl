<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!'ByteDesk Forum'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/forum/style.css" rel="stylesheet">
</head>
<body>
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="/forum">ByteDesk Forum</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/forum">首页</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/forum/categories">分类</a>
                    </li>
                </ul>
                <div class="d-flex align-items-center">
                    <form class="d-flex me-3" action="/forum/search" method="GET">
                        <input class="form-control me-2" type="search" name="keyword" placeholder="搜索帖子...">
                        <button class="btn btn-outline-light" type="submit">搜索</button>
                    </form>
                    <#if currentUser??>
                        <div class="dropdown">
                            <button class="btn btn-outline-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                ${currentUser.username}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="/forum/user/posts">我的帖子</a></li>
                                <li><a class="dropdown-item" href="/forum/user/comments">我的评论</a></li>
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
    <footer class="bg-dark text-light py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>ByteDesk Forum</h5>
                    <p>一个开源的社区论坛系统</p>
                </div>
                <div class="col-md-6 text-end">
                    <p>&copy; 2024 ByteDesk. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/forum/main.js"></script>
</body>
</html> 