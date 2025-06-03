<#import "layout/base.ftl" as base>

<@base.base title="ByteDesk 呼叫中心 - 企业级通信解决方案">
    <!-- 顶部介绍区域 -->
    <div class="row text-center mb-5">
        <div class="col">
            <h1 class="display-4 mb-3">ByteDesk FreeSWITCH 呼叫中心</h1>
            <p class="lead">企业级开源通信平台，打造无缝客户沟通体验</p>
        </div>
    </div>

    <!-- 顶部统计信息 -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">📞</div>
                    <h5 class="card-title">今日呼叫</h5>
                    <p class="card-text display-6">${statistics.todayCallCount!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">⏱️</div>
                    <h5 class="card-title">平均通话时长</h5>
                    <p class="card-text display-6">${statistics.averageCallDuration!'0'}分钟</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">👥</div>
                    <h5 class="card-title">在线座席</h5>
                    <p class="card-text display-6">${statistics.onlineAgentsCount!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">🔄</div>
                    <h5 class="card-title">实时呼叫</h5>
                    <p class="card-text display-6">${statistics.activeCallsCount!'0'}</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 主要功能介绍 -->
    <div class="row g-4 mb-5">
        <div class="col-md-4">
            <div class="feature-box h-100">
                <div class="feature-icon">🔌</div>
                <h3>基于FreeSWITCH</h3>
                <p>FreeSWITCH是一个开源通信平台，支持VoIP、WebRTC和传统电话网络，提供强大的通信能力。</p>
                <ul class="list-unstyled">
                    <li><i class="bi bi-check-circle"></i> 高性能架构</li>
                    <li><i class="bi bi-check-circle"></i> 多协议支持</li>
                    <li><i class="bi bi-check-circle"></i> 开源灵活</li>
                </ul>
            </div>
        </div>
        <div class="col-md-4">
            <div class="feature-box h-100">
                <div class="feature-icon">🔀</div>
                <h3>智能呼叫路由</h3>
                <p>根据客户需求和座席技能自动分配呼叫，提高服务质量和效率。</p>
                <ul class="list-unstyled">
                    <li><i class="bi bi-check-circle"></i> 基于技能的路由</li>
                    <li><i class="bi bi-check-circle"></i> 优先级队列</li>
                    <li><i class="bi bi-check-circle"></i> 智能座席调度</li>
                </ul>
            </div>
        </div>
        <div class="col-md-4">
            <div class="feature-box h-100">
                <div class="feature-icon">📊</div>
                <h3>全面数据分析</h3>
                <p>通过详细的呼叫统计和报表，帮助企业优化客户服务流程。</p>
                <ul class="list-unstyled">
                    <li><i class="bi bi-check-circle"></i> 实时监控面板</li>
                    <li><i class="bi bi-check-circle"></i> 自定义报表</li>
                    <li><i class="bi bi-check-circle"></i> 性能指标追踪</li>
                </ul>
            </div>
        </div>
    </div>

    <!-- 更多功能展示 -->
    <div class="row mb-5">
        <div class="col-12">
            <h2 class="text-center mb-4">强大的呼叫中心功能</h2>
        </div>
        <div class="col-md-6">
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-telephone"></i> 多渠道通信</h5>
                    <p class="card-text">支持SIP、WebRTC、PSTN等多种通信协议，实现全渠道客户沟通。</p>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-record-circle"></i> 通话录音</h5>
                    <p class="card-text">自动录制所有通话，支持质量控制和培训需求。</p>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-volume-up"></i> IVR语音菜单</h5>
                    <p class="card-text">通过可视化界面轻松构建交互式语音应答系统。</p>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card mb-3">
                <div class="card-body">
                    <h5 class="card-title"><i class="bi bi-person-lines-fill"></i> CRM集成</h5>
                    <p class="card-text">与客户关系管理系统无缝集成，提供完整的客户服务视图。</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 技术架构介绍 -->
    <div class="row mb-5">
        <div class="col-12">
            <h2 class="text-center mb-4">技术架构</h2>
            <div class="text-center mb-4">
                <img src="/images/callcenter/architecture.png" alt="ByteDesk呼叫中心架构" class="img-fluid rounded shadow" style="max-width: 80%;">
            </div>
            <p class="text-center">ByteDesk呼叫中心基于FreeSWITCH构建，采用微服务架构，具有高可用性和可扩展性。</p>
        </div>
    </div>

    <!-- 呼叫统计图表 -->
    <div class="row mb-5">
        <div class="col-12">
            <h2 class="text-center mb-4">呼叫统计</h2>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    本周呼叫量统计
                </div>
                <div class="card-body">
                    <canvas id="weeklyCallsChart"></canvas>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    呼叫类型分布
                </div>
                <div class="card-body">
                    <canvas id="callTypeChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- 快速操作区 -->
    <div class="row text-center mb-5">
        <div class="col-12">
            <h2 class="mb-4">快速入门</h2>
            <div class="d-flex justify-content-center gap-3">
                <a href="/callcenter/docs" class="btn btn-primary btn-lg">查看文档</a>
                <a href="/callcenter/demo" class="btn btn-outline-primary btn-lg">在线演示</a>
            </div>
        </div>
    </div>

    <!-- Chart.js引入 -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 每周呼叫量统计图表
            const weeklyCallsCtx = document.getElementById('weeklyCallsChart').getContext('2d');
            new Chart(weeklyCallsCtx, {
                type: 'line',
                data: {
                    labels: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'],
                    datasets: [{
                        label: '呼叫量',
                        data: [65, 78, 80, 85, 90, 45, 30],
                        borderColor: '#0d6efd',
                        tension: 0.1,
                        fill: false
                    }]
                },
                options: {
                    responsive: true
                }
            });
            
            // 呼叫类型分布图表
            const callTypeCtx = document.getElementById('callTypeChart').getContext('2d');
            new Chart(callTypeCtx, {
                type: 'pie',
                data: {
                    labels: ['入站呼叫', '出站呼叫', '内部呼叫', '会议呼叫'],
                    datasets: [{
                        data: [55, 30, 10, 5],
                        backgroundColor: ['#0d6efd', '#6c757d', '#198754', '#ffc107']
                    }]
                },
                options: {
                    responsive: true
                }
            });
        });
    </script>

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
</@base.base>