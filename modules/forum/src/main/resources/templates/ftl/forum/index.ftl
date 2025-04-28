<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>微语用户社区/论坛</title>
    <meta name="description" content="微语用户社区是一个面向开发者和用户的交流平台，提供优质的技术讨论、问答和知识分享环境">
    <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
    <#--  <link href="/css/ticket/index.css" rel="stylesheet">  -->
    <style>
        .feature-icon {
            font-size: 2.5rem;
            color: #0d6efd;
            margin-bottom: 1rem;
        }
        .feature-box {
            padding: 2rem;
            border-radius: 10px;
            background: #f8f9fa;
            transition: all 0.3s ease;
        }
        .feature-box:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="#">微语社区</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" href="#技术交流">技术交流</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#问答社区">问答社区</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#最佳实践">最佳实践</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#官方公告">官方公告</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">微语开发者社区</h1>
                <p class="lead">面向开发者和用户的优质技术交流平台，提供技术讨论、问答和知识分享</p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">💬</div>
                    <h3>技术交流</h3>
                    <ul class="list-unstyled">
                        <li>技术讨论版块</li>
                        <li>产品反馈通道</li>
                        <li>使用教程分享</li>
                        <li>最佳实践案例</li>
                        <li>问答社区互助</li>
                        <li>官方公告发布</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">✨</div>
                    <h3>内容特性</h3>
                    <ul class="list-unstyled">
                        <li>Markdown支持</li>
                        <li>富文本编辑器</li>
                        <li>代码高亮显示</li>
                        <li>图片智能上传</li>
                        <li>附件管理系统</li>
                        <li>多媒体内容支持</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🌟</div>
                    <h3>社区互动</h3>
                    <ul class="list-unstyled">
                        <li>点赞与收藏</li>
                        <li>评论与回复</li>
                        <li>内容分享</li>
                        <li>用户关注</li>
                        <li>话题订阅</li>
                        <li>积分奖励</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🎯</div>
                    <h3>用户中心</h3>
                    <ul class="list-unstyled">
                        <li>个人资料管理</li>
                        <li>发帖历史记录</li>
                        <li>收藏内容管理</li>
                        <li>等级成长体系</li>
                        <li>专家认证</li>
                        <li>贡献榜单</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🔍</div>
                    <h3>智能搜索</h3>
                    <ul class="list-unstyled">
                        <li>全文内容检索</li>
                        <li>高级筛选功能</li>
                        <li>标签智能匹配</li>
                        <li>相关推荐</li>
                        <li>热门排序</li>
                        <li>精准导航</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🛡️</div>
                    <h3>安全保障</h3>
                    <ul class="list-unstyled">
                        <li>内容审核</li>
                        <li>垃圾信息过滤</li>
                        <li>敏感词屏蔽</li>
                        <li>XSS/CSRF防护</li>
                        <li>访问频率控制</li>
                        <li>数据安全加密</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-light py-4 mt-5">
        <div class="container text-center">
            <p class="mb-0">© 2024 微语. All rights reserved.</p>
            <p class="text-muted">微语开发者社区</p>
        </div>
    </footer>

    <script src="/assets/vendor/jquery-3.7.1.min.js"></script>
    <script src="/assets/vendor/bootstrap5/js/bootstrap.bundle.min.js"></script>
    <#--  <script src="/js/ticket/index.js"></script>  -->
    <!-- bytedesk.com -->
    <script src="https://www.weiyuai.cn/embed/bytedesk-web.js"></script>
    <script>
        const config = {
            baseUrl: 'https://www.weiyuai.cn/chat',
            placement: 'bottom-right',
            autoPopup: false,
            inviteConfig: {
            show: false,
            text: '需要帮助么',
            delay: 1000, // 首次弹出延迟时间, 单位: 毫秒
            loop: true, // 是否启用循环
            loopDelay: 10000, // 循环间隔, 单位: 毫秒
            loopCount: 3, // 循环次数, 设置为0表示无限循环
            },
            bubbleConfig: {
            show: true,
            icon: '👋',
            title: '需要帮助么',
            subtitle: '点击我，与我对话'
            },
            theme: {
            mode: 'system',
            backgroundColor: '#0066FF',
            textColor: '#ffffff'
            },
            window: {
            width: '380'
            },
            chatConfig: {
            org: 'df_org_uid',
            t: '1',
            sid: 'df_wg_uid'
            }
        };
        const bytedesk = new BytedeskWeb(config);
        bytedesk.init();
    </script>
    <#--  end of bytedesk.com  -->
</body>
</html>