<#import "layout/base.ftl" as base>

<@base.base title="ByteDesk VOC - 首页">
    <!-- 顶部统计信息 -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h5 class="card-title">待处理</h5>
                    <p class="card-text display-6">${pendingCount!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h5 class="card-title">处理中</h5>
                    <p class="card-text display-6">${processingCount!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h5 class="card-title">已解决</h5>
                    <p class="card-text display-6">${resolvedCount!'0'}</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card text-center">
                <div class="card-body">
                    <h5 class="card-title">已关闭</h5>
                    <p class="card-text display-6">${closedCount!'0'}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 反馈列表 -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <div class="d-flex align-items-center">
                        <form class="d-flex me-3" method="GET">
                            <input type="text" class="form-control form-control-sm me-2" 
                                name="keyword" value="${keyword!''}" placeholder="搜索反馈...">
                            <input type="hidden" name="type" value="${type!''}">
                            <input type="hidden" name="status" value="${status!''}">
                            <input type="hidden" name="sort" value="${sort!'createdAt'}">
                            <input type="hidden" name="direction" value="${direction!'desc'}">
                            <button type="submit" class="btn btn-outline-primary btn-sm">搜索</button>
                        </form>
                        
                        <div class="btn-group me-2">
                            <button type="button" class="btn btn-outline-secondary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                ${type!'全部类型'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=&status=${status!''}&sort=${sort!''}&direction=${direction!''}">全部类型</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=suggestion&status=${status!''}&sort=${sort!''}&direction=${direction!''}">建议</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=bug&status=${status!''}&sort=${sort!''}&direction=${direction!''}">Bug</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=complaint&status=${status!''}&sort=${sort!''}&direction=${direction!''}">投诉</a></li>
                                <li><a class="dropdown-item" href="?keyword=${keyword!''}&type=other&status=${status!''}&sort=${sort!''}&direction=${direction!''}">其他</a></li>
                            </ul>
                        </div>
                        
                        <div class="btn-group me-2">
                            <button type="button" class="btn btn-outline-secondary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                ${status!'全部状态'}
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
                            <button type="button" class="btn btn-outline-secondary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
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
                    <a href="/voc/feedback/create" class="btn btn-primary btn-sm">提交反馈</a>
                </div>
                <div class="list-group list-group-flush">
                    <#if feedbacks.totalElements == 0>
                        <div class="list-group-item text-center text-muted">
                            暂无反馈
                        </div>
                    <#else>
                        <#list feedbacks.content as feedback>
                            <div class="list-group-item">
                                <div class="d-flex w-100 justify-content-between">
                                    <h5 class="mb-1">
                                        <a href="/voc/feedback/${feedback.id}" class="text-decoration-none">
                                            <span class="badge bg-${feedback.type=='bug'?'danger':
                                                (feedback.type=='suggestion'?'primary':'secondary')}">${feedback.type}</span>
                                            ${feedback.content?truncate(50)}
                                        </a>
                                    </h5>
                                    <small>${feedback.createdAt?string('yyyy-MM-dd HH:mm')}</small>
                                </div>
                                <div class="d-flex justify-content-between align-items-center mt-2">
                                    <small>
                                        <span class="me-2">
                                            <i class="bi bi-chat"></i> ${feedback.replyCount}
                                        </span>
                                        <span>
                                            <i class="bi bi-heart"></i> ${feedback.likeCount}
                                        </span>
                                    </small>
                                    <small>
                                        <span class="badge bg-${feedback.status=='pending'?'warning':
                                            (feedback.status=='processing'?'info':
                                            (feedback.status=='resolved'?'success':'secondary'))}">${feedback.status}</span>
                                    </small>
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
                                <a class="page-link" href="?page=${feedbacks.number - 1}">上一页</a>
                            </li>
                        </#if>
                        
                        <#list 0..feedbacks.totalPages-1 as i>
                            <li class="page-item ${(i == feedbacks.number)?string('active', '')}">
                                <a class="page-link" href="?page=${i}">${i + 1}</a>
                            </li>
                        </#list>
                        
                        <#if feedbacks.hasNext()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${feedbacks.number + 1}">下一页</a>
                            </li>
                        </#if>
                    </ul>
                </nav>
            </#if>
        </div>
    </div>
</@base.base>