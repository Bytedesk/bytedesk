<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>工作流Flow - 微语</title>
    <meta name="description" content="微语工作流，基于AI全新打造">
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
            <a class="navbar-brand" href="#">微语AI助手</a>
        </div>
    </nav>

    <div class="container my-5">
        <div class="row text-center mb-5">
            <div class="col">
                <h1 class="display-4 mb-3">智能AI助手系统</h1>
                <p class="lead">整合多种大语言模型，提供智能对话、自动化工作流、知识库管理等全方位AI解决方案</p>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🤖</div>
                    <h3>多模型支持</h3>
                    <ul class="list-unstyled">
                        <li>OpenAI集成</li>
                        <li>智谱AI对接</li>
                        <li>文心一言集成</li>
                        <li>Ollama本地部署</li>
                        <li>自定义模型接入</li>
                        <li>多模型协同</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">💬</div>
                    <h3>智能客服</h3>
                    <ul class="list-unstyled">
                        <li>24/7自动回复</li>
                        <li>多轮对话支持</li>
                        <li>情感分析</li>
                        <li>意图识别</li>
                        <li>智能问答推荐</li>
                        <li>人机协作服务</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">🧠</div>
                    <h3>智能体系统</h3>
                    <ul class="list-unstyled">
                        <li>角色定制</li>
                        <li>知识库对接</li>
                        <li>上下文管理</li>
                        <li>多智能体协作</li>
                        <li>任务规划执行</li>
                        <li>持续学习优化</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">⚡</div>
                    <h3>自动化工作流</h3>
                    <ul class="list-unstyled">
                        <li>流程自动化</li>
                        <li>智能任务分发</li>
                        <li>条件触发执行</li>
                        <li>多步骤编排</li>
                        <li>异常处理机制</li>
                        <li>执行监控分析</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📚</div>
                    <h3>知识库管理</h3>
                    <ul class="list-unstyled">
                        <li>知识库构建</li>
                        <li>文档智能导入</li>
                        <li>知识图谱</li>
                        <li>语义检索</li>
                        <li>实时更新</li>
                        <li>知识推理</li>
                    </ul>
                </div>
            </div>

            <div class="col-md-4">
                <div class="feature-box h-100">
                    <div class="feature-icon">📊</div>
                    <h3>数据分析</h3>
                    <ul class="list-unstyled">
                        <li>对话质量分析</li>
                        <li>用户意图洞察</li>
                        <li>性能监控</li>
                        <li>效果评估</li>
                        <li>优化建议</li>
                        <li>趋势预测</li>
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