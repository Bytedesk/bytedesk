<#import "layout/base.ftl" as base>

<@base.base title="ByteDesk VOC - 客户之声">
    <!-- 顶部介绍区域 -->
    <div class="row text-center mb-5">
        <div class="col">
            <h1 class="display-4 mb-3">客户之声管理系统</h1>
            <p class="lead">收集、分析和响应客户反馈，持续改进产品和服务质量</p>
        </div>
    </div>

    <!-- 顶部统计信息 -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">📋</div>
                    <h5 class="card-title">待处理</h5>
                    <p class="card-text display-6">${pendingCount!'0'}</p>
                    <p class="text-muted small">需要立即处理的反馈</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">🔄</div>
                    <h5 class="card-title">处理中</h5>
                    <p class="card-text display-6">${processingCount!'0'}</p>
                    <p class="text-muted small">正在跟进的反馈</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">✅</div>
                    <h5 class="card-title">已解决</h5>
                    <p class="card-text display-6">${resolvedCount!'0'}</p>
                    <p class="text-muted small">成功解决的反馈</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center feature-box h-100">
                <div class="card-body">
                    <div class="feature-icon">🔒</div>
                    <h5 class="card-title">已关闭</h5>
                    <p class="card-text display-6">${closedCount!'0'}</p>
                    <p class="text-muted small">已完成的反馈</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 反馈列表 -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <div class="d-flex align-items-center flex-wrap gap-2">
                        <form class="d-flex me-3" method="GET">
                            <div class="input-group">
                                <input type="text" class="form-control form-control-sm" 
                                    name="keyword" value="${keyword!''}" placeholder="搜索反馈内容...">
                                <button type="submit" class="btn btn-primary btn-sm">
                                    <i class="bi bi-search"></i> 搜索
                                </button>
                            </div>
                            <input type="hidden" name="type" value="${type!''}">
                            <input type="hidden" name="status" value="${status!''}">
                            <input type="hidden" name="sort" value="${sort!'createdAt'}">
                            <input type="hidden" name="direction" value="${direction!'desc'}">
                        </form>
                        
                        <div class="btn-group">
                            <button type="button" class="btn btn-outline-primary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                <i class="bi bi-funnel"></i> ${type!'全部类型'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=&status=${status!''}&sort=${sort!''}&direction=${direction!''}">全部类型</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=suggestion&status=${status!''}&sort=${sort!''}&direction=${direction!''}">产品建议</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=bug&status=${status!''}&sort=${sort!''}&direction=${direction!''}">功能缺陷</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=complaint&status=${status!''}&sort=${sort!''}&direction=${direction!''}">服务投诉</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=requirement&status=${status!''}&sort=${sort!''}&direction=${direction!''}">功能需求</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=other&status=${status!''}&sort=${sort!''}&direction=${direction!''}">其他</a></li>
                            </ul>
                        </div>
                        
                        <div class="btn-group">
                            <button type="button" class="btn btn-outline-primary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                <i class="bi bi-flag"></i> ${status!'全部状态'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=&sort=${sort!''}&direction=${direction!''}">全部状态</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=pending&sort=${sort!''}&direction=${direction!''}">待处理</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=processing&sort=${sort!''}&direction=${direction!''}">处理中</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=resolved&sort=${sort!''}&direction=${direction!''}">已解决</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=closed&sort=${sort!''}&direction=${direction!''}">已关闭</a></li>
                            </ul>
                        </div>
                        
                        <div class="btn-group">
                            <button type="button" class="btn btn-outline-primary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                <i class="bi bi-sort-down"></i> 
                                排序: ${sort=='createdAt'?'创建时间':
                                    (sort=='likeCount'?'点赞数':
                                    (sort=='replyCount'?'回复数':'未知'))}
                                ${direction=='asc'?'↑':'↓'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=createdAt&direction=desc">创建时间 ↓</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=createdAt&direction=asc">创建时间 ↑</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=likeCount&direction=desc">点赞数 ↓</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=likeCount&direction=asc">点赞数 ↑</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=replyCount&direction=desc">回复数 ↓</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=replyCount&direction=asc">回复数 ↑</a></li>
                            </ul>
                        </div>
                    </div>
                    <a href="/voc/feedback/create" class="btn btn-primary">
                        <i class="bi bi-plus-lg"></i> 提交反馈
                    </a>
                </div>
                <div class="list-group list-group-flush">
                    <#if feedbacks.totalElements == 0>
                        <div class="list-group-item text-center p-5">
                            <div class="text-muted">
                                <i class="bi bi-inbox h1"></i>
                                <p class="mt-3">暂无反馈内容</p>
                            </div>
                        </div>
                    <#else>
                        <#list feedbacks.content as feedback>
                            <div class="list-group-item">
                                <div class="d-flex w-100 justify-content-between align-items-start">
                                    <div>
                                        <h5 class="mb-2">
                                            <a href="/voc/feedback/${feedback.id}" class="text-decoration-none">
                                                <span class="badge bg-${feedback.type=='bug'?'danger':
                                                    (feedback.type=='suggestion'?'primary':
                                                    (feedback.type=='complaint'?'warning':
                                                    (feedback.type=='requirement'?'info':'secondary')))}">${feedback.type}</span>
                                                ${feedback.content?truncate(50)}
                                            </a>
                                        </h5>
                                        <p class="mb-1 text-muted small">
                                            <i class="bi bi-person-circle"></i> ${feedback.creator!'匿名用户'} 
                                            <span class="mx-2">•</span>
                                            <i class="bi bi-clock"></i> ${feedback.createdAt?string('yyyy-MM-dd HH:mm')}
                                        </p>
                                    </div>
                                    <span class="badge bg-${feedback.status=='pending'?'warning':
                                        (feedback.status=='processing'?'info':
                                        (feedback.status=='resolved'?'success':'secondary'))}">${feedback.status}</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mt-2">
                                    <div>
                                        <span class="me-3 text-muted">
                                            <i class="bi bi-chat"></i> ${feedback.replyCount} 回复
                                        </span>
                                        <span class="text-muted">
                                            <i class="bi bi-heart"></i> ${feedback.likeCount} 点赞
                                        </span>
                                    </div>
                                    <div class="btn-group btn-group-sm">
                                        <a href="/voc/feedback/${feedback.id}" class="btn btn-outline-primary">
                                            <i class="bi bi-eye"></i> 查看详情
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </#if>
                </div>
            </div>

            <!-- 分页 -->
            <#if feedbacks.totalElements gt 0>
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <#if feedbacks.hasPrevious()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${feedbacks.number - 1}&keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=${sort!''}&direction=${direction!''}">
                                    <i class="bi bi-chevron-left"></i> 上一页
                                </a>
                            </li>
                        </#if>
                        
                        <#list 0..feedbacks.totalPages-1 as i>
                            <li class="page-item ${(i == feedbacks.number)?string('active', '')}">
                                <a class="page-link" href="?page=${i}&keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=${sort!''}&direction=${direction!''}">${i + 1}</a>
                            </li>
                        </#list>
                        
                        <#if feedbacks.hasNext()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${feedbacks.number + 1}&keyword=${keyword!''}&type=${type!''}&status=${status!''}&sort=${sort!''}&direction=${direction!''}">
                                    下一页 <i class="bi bi-chevron-right"></i>
                                </a>
                            </li>
                        </#if>
                    </ul>
                </nav>
            </#if>
        </div>
    </div>

    <style>
        .feature-icon {
            font-size: 2rem;
            color: #0d6efd;
            margin-bottom: 1rem;
        }
        .feature-box {
            transition: all 0.3s ease;
        }
        .feature-box:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .list-group-item:hover {
            background-color: #f8f9fa;
        }
    </style>
</@base.base>