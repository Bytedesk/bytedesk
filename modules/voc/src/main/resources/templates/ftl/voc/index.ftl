<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>客户之声 - 微语</title>
    <meta name="description" content="微语客户之声-意见反馈平台">
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
            <a class="navbar-brand" href="#">微语客户之声</a>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">客户之声意见反馈平台</h1>
                <p class="lead">多渠道收集客户反馈，智能分析处理，持续改进产品与服务质量</p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📝</div>
                    <h3>多渠道反馈收集</h3>
                    <ul class="list-unstyled">
                        <li>内部表单收集</li>
                        <li>社交媒体评论抓取</li>
                        <li>邮件反馈通道</li>
                        <li>第三方平台同步</li>
                        <li>小程序/APP内嵌入口</li>
                        <li>全方位覆盖反馈来源</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🎯</div>
                    <h3>反馈类型管理</h3>
                    <ul class="list-unstyled">
                        <li>产品建议收集</li>
                        <li>服务投诉处理</li>
                        <li>功能需求管理</li>
                        <li>技术支持对接</li>
                        <li>分类智能推荐</li>
                        <li>灵活扩展类型</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">⚡</div>
                    <h3>智能处理流程</h3>
                    <ul class="list-unstyled">
                        <li>自动分类分发</li>
                        <li>产品经理评审</li>
                        <li>客服快速跟进</li>
                        <li>技术团队评估</li>
                        <li>全程状态跟踪</li>
                        <li>闭环处理反馈</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📢</div>
                    <h3>智能通知机制</h3>
                    <ul class="list-unstyled">
                        <li>邮件实时通知</li>
                        <li>站内消息提醒</li>
                        <li>企业微信通知</li>
                        <li>钉钉机器人集成</li>
                        <li>自定义通知规则</li>
                        <li>多渠道触达</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🤖</div>
                    <h3>高级智能特性</h3>
                    <ul class="list-unstyled">
                        <li>敏感词智能过滤</li>
                        <li>AI自动分类</li>
                        <li>智能路由分配</li>
                        <li>满意度跟踪</li>
                        <li>数据分析报表</li>
                        <li>持续优化改进</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🛡️</div>
                    <h3>安全性能保障</h3>
                    <ul class="list-unstyled">
                        <li>异步处理机制</li>
                        <li>智能限流控制</li>
                        <li>数据安全脱敏</li>
                        <li>访问权限管理</li>
                        <li>系统性能监控</li>
                        <li>安全防护体系</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-light py-4 mt-5">
        <div class="container text-center">
            <p class="mb-0">© 2025 微语. All rights reserved.</p>
            <p class="text-muted">北京微语天下科技有限公司</p>
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