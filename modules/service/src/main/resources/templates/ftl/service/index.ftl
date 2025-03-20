<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>在线客服系统 - 微语</title>
    <meta name="description" content="微语在线客服系统是一个全渠道智能客服解决方案">
    <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
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
            <a class="navbar-brand" href="#">微语在线客服系统</a>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">智能在线客服系统</h1>
                <p class="lead">多渠道整合的智能客服平台，为企业打造专业、高效的客户服务体系</p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">💬</div>
                    <h3>多渠道接入</h3>
                    <ul class="list-unstyled">
                        <li>网页即时通讯</li>
                        <li>微信公众号</li>
                        <li>企业微信</li>
                        <li>钉钉集成</li>
                        <li>手机APP</li>
                        <li>邮件系统</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">👨‍💼</div>
                    <h3>人工客服</h3>
                    <ul class="list-unstyled">
                        <li>客服分组管理</li>
                        <li>会话分配策略</li>
                        <li>客服状态监控</li>
                        <li>会话转接管理</li>
                        <li>服务质量评估</li>
                        <li>客服绩效统计</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🤖</div>
                    <h3>智能机器人</h3>
                    <ul class="list-unstyled">
                        <li>智能问答系统</li>
                        <li>知识库对接</li>
                        <li>自动回复</li>
                        <li>意图识别</li>
                        <li>对话流程管理</li>
                        <li>机器人训练</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📊</div>
                    <h3>统计报表</h3>
                    <ul class="list-unstyled">
                        <li>会话量统计</li>
                        <li>响应时间分析</li>
                        <li>客服工作量</li>
                        <li>满意度评价</li>
                        <li>渠道分布分析</li>
                        <li>问题类型统计</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🔄</div>
                    <h3>会话管理</h3>
                    <ul class="list-unstyled">
                        <li>消息撤回</li>
                        <li>快捷回复</li>
                        <li>消息记录</li>
                        <li>敏感词过滤</li>
                        <li>多媒体消息</li>
                        <li>历史会话查询</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🔒</div>
                    <h3>安全管理</h3>
                    <ul class="list-unstyled">
                        <li>访问权限控制</li>
                        <li>数据加密传输</li>
                        <li>隐私信息保护</li>
                        <li>操作日志记录</li>
                        <li>安全审计</li>
                        <li>备份恢复</li>
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
        <!-- bytedesk.com -->
    <script src="https://www.weiyuai.cn/embed/bytedesk-web.js"></script>
    <script>
        const config = {
            baseUrl: 'https://www.weiyuai.cn/chat',
            placement: 'bottom-right',
            autoPopup: false,
            inviteConfig: {
            show: true,
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