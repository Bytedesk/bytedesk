<#import "layout/base.ftl" as base>

<@base.base title="ByteDesk 呼叫中心">
    <!-- 顶部介绍区域 -->
    <div class="row text-center mb-5">
        <div class="col">
            <h1 class="display-4 mb-3">FreeSWITCH 呼叫中心系统</h1>
            <p class="lead">专业智能的企业级通信解决方案，提供高质量的语音呼叫服务</p>
        </div>
    </div>

    <!-- 顶部统计信息 -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">📞</div>
                    <h5 class="card-title">总通话量</h5>
                    <p class="card-text display-6">${totalCalls!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">🔄</div>
                    <h5 class="card-title">当前活动通话</h5>
                    <p class="card-text display-6">${activeCalls!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">❌</div>
                    <h5 class="card-title">未接来电</h5>
                    <p class="card-text display-6">${missedCalls!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">⏱️</div>
                    <h5 class="card-title">平均通话时长</h5>
                    <p class="card-text display-6">${avgDuration!'0'}分钟</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 功能介绍 -->
    <div class="row mb-5">
        <div class="col-12">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h2 class="card-title mb-4">ByteDesk 呼叫中心解决方案</h2>
                    <p class="lead">我们基于FreeSWITCH打造的呼叫中心系统，为企业提供高效、稳定、灵活的通信服务</p>
                    
                    <div class="row mt-5">
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">☎️</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>语音通话</h5>
                                    <p>高质量、低延迟的语音通话服务，支持多方通话和会议</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">🔄</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>智能路由</h5>
                                    <p>基于技能、状态的智能呼叫分配，提高客服效率</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">📊</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>实时监控</h5>
                                    <p>实时监控座席状态和通话质量，确保服务水平</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">📱</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>多通道接入</h5>
                                    <p>支持SIP、WebRTC、手机、固话等多种接入方式</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">🔌</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>系统集成</h5>
                                    <p>与CRM、客服系统等无缝集成，提供统一服务体验</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="d-flex">
                                <div class="flex-shrink-0">
                                    <div class="feature-icon">🤖</div>
                                </div>
                                <div class="flex-grow-1 ms-3">
                                    <h5>智能语音助手</h5>
                                    <p>基于AI的语音识别和交互，提供智能服务体验</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 技术架构介绍 -->
    <div class="row mb-5">
        <div class="col-12">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h3 class="card-title mb-4">系统架构</h3>
                    
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h5 class="mb-3">FreeSWITCH 核心优势</h5>
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item bg-transparent">高性能处理引擎，支持上千并发通话</li>
                                <li class="list-group-item bg-transparent">多协议支持：SIP、WebRTC、PSTN等</li>
                                <li class="list-group-item bg-transparent">丰富的媒体处理能力，支持转码、录音、回声消除等</li>
                                <li class="list-group-item bg-transparent">灵活的路由策略，支持复杂呼叫场景</li>
                                <li class="list-group-item bg-transparent">开源稳定，易于扩展和定制</li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            <h5 class="mb-3">系统层级结构</h5>
                            <div class="bg-light p-3 rounded">
                                <pre class="mb-0"><code>
└── 呼叫中心系统
    ├── 接入层
    │   ├── SIP接入
    │   ├── WebRTC接入
    │   └── PSTN接入
    ├── 核心层
    │   ├── FreeSWITCH引擎
    │   ├── 呼叫控制
    │   └── 媒体处理
    └── 应用层
        ├── 座席管理
        ├── 队列管理
        ├── 统计报表
        └── 系统监控
                                </code></pre>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 客户案例或使用场景 -->
    <div class="row mb-5">
        <div class="col-12">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h3 class="card-title mb-4">应用场景</h3>
                    
                    <div class="row">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h5 class="card-title">客服中心</h5>
                                    <p class="card-text">为企业提供专业的呼入式客服中心解决方案，高效处理客户咨询与服务请求。</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h5 class="card-title">销售外呼</h5>
                                    <p class="card-text">支持团队高效率销售外呼，提高接通率和转化率，实现销售目标。</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <div class="card-body">
                                    <h5 class="card-title">远程办公</h5>
                                    <p class="card-text">为远程办公团队提供统一通信平台，保持团队协作和客户服务无缝衔接。</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@base.base>
