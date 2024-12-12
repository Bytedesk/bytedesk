<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${title!'工单系统'} - ByteDesk</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="/webjars/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="/webjars/toastr/2.1.4/toastr.min.css">
    <link rel="stylesheet" href="/css/style.css">
    
    <!-- 自定义样式 -->
    <style>
        .sidebar {
            position: fixed;
            top: 0;
            bottom: 0;
            left: 0;
            z-index: 100;
            padding: 48px 0 0;
            box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
            background-color: #f8f9fa;
        }
        
        .sidebar-sticky {
            position: relative;
            top: 0;
            height: calc(100vh - 48px);
            padding-top: .5rem;
            overflow-x: hidden;
            overflow-y: auto;
        }
        
        .navbar-brand {
            padding-top: .75rem;
            padding-bottom: .75rem;
            font-size: 1rem;
            background-color: rgba(0, 0, 0, .25);
            box-shadow: inset -1px 0 0 rgba(0, 0, 0, .25);
        }
        
        .content {
            margin-left: 240px;
            padding: 20px;
        }
        
        @media (max-width: 768px) {
            .sidebar {
                display: none;
            }
            .content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <!-- 顶部导航栏 -->
    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0 shadow">
        <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="/">
            <i class="fas fa-headset"></i> ByteDesk
        </a>
        <button class="navbar-toggler position-absolute d-md-none collapsed" type="button" 
                data-bs-toggle="collapse" data-bs-target="#sidebarMenu">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <!-- 搜索框 -->
        <form class="w-100" action="/tickets/search" method="get">
            <input class="form-control form-control-dark w-100" type="text" name="keyword" 
                   placeholder="搜索工单..." aria-label="Search">
        </form>
        
        <!-- 用户菜单 -->
        <div class="navbar-nav">
            <div class="nav-item text-nowrap">
                <#if user??>
                    <div class="dropdown">
                        <a class="nav-link px-3 dropdown-toggle" href="#" role="button" 
                           data-bs-toggle="dropdown">
                            <i class="fas fa-user"></i> ${user.name}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="/profile">个人信息</a></li>
                            <li><a class="dropdown-item" href="/settings">系统设置</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="/logout">退出登录</a></li>
                        </ul>
                    </div>
                <#else>
                    <a class="nav-link px-3" href="/login">登录</a>
                </#if>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- 侧边栏 -->
            <nav id="sidebarMenu" class="col-md-3 col-lg-2 d-md-block sidebar collapse">
                <div class="sidebar-sticky pt-3">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link ${(active!'') == 'dashboard'?then('active', '')}" href="/dashboard">
                                <i class="fas fa-home"></i> 控制台
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${(active!'') == 'tickets'?then('active', '')}" href="/tickets">
                                <i class="fas fa-ticket-alt"></i> 工单列表
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/tickets/create">
                                <i class="fas fa-plus"></i> 创建工单
                            </a>
                        </li>
                        
                        <#if user?? && (user.role == 'ADMIN' || user.role == 'SUPERVISOR')>
                            <h6 class="sidebar-heading d-flex justify-content-between align-items-center px-3 mt-4 mb-1 text-muted">
                                <span>管理功能</span>
                            </h6>
                            <li class="nav-item">
                                <a class="nav-link ${(active!'') == 'performance'?then('active', '')}" href="/performance">
                                    <i class="fas fa-chart-line"></i> 绩效统计
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${(active!'') == 'reports'?then('active', '')}" href="/reports">
                                    <i class="fas fa-file-alt"></i> 报表
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${(active!'') == 'settings'?then('active', '')}" href="/settings">
                                    <i class="fas fa-cog"></i> 系统设置
                                </a>
                            </li>
                        </#if>
                    </ul>
                </div>
            </nav>

            <!-- 主要内容 -->
            <main class="content">
                <#if message??>
                    <div class="alert alert-${messageType!'info'} alert-dismissible fade show">
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </#if>
                
                <#nested>
            </main>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="/webjars/jquery/3.7.0/jquery.min.js"></script>
    <script src="/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script src="/webjars/toastr/2.1.4/toastr.min.js"></script>
    <script src="/js/app.js"></script>
    
    <!-- 页面特定的JavaScript -->
    <#if scripts??>
        <#list scripts as script>
            <script src="${script}"></script>
        </#list>
    </#if>
    
    <!-- 初始化Toastr -->
    <script>
        toastr.options = {
            "closeButton": true,
            "progressBar": true,
            "positionClass": "toast-top-right",
            "timeOut": "3000"
        };
        
        <#if success??>
            toastr.success("${success}");
        </#if>
        
        <#if error??>
            toastr.error("${error}");
        </#if>
    </script>
</body>
</html> 