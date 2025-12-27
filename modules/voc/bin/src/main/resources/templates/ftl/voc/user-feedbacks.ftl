<#import "layout/base.ftl" as base>

<@base.base title="我的反馈 - ByteDesk VOC">
    <div class="row">
        <div class="col-12">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/voc">首页</a></li>
                    <li class="breadcrumb-item active">我的反馈</li>
                </ol>
            </nav>

            <!-- 统计信息 -->
            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="card text-center">
                        <div class="card-body">
                            <h5 class="card-title">待处理</h5>
                            <p class="card-text display-6">${userPendingCount!'0'}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-center">
                        <div class="card-body">
                            <h5 class="card-title">处理中</h5>
                            <p class="card-text display-6">${userProcessingCount!'0'}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-center">
                        <div class="card-body">
                            <h5 class="card-title">已解决</h5>
                            <p class="card-text display-6">${userResolvedCount!'0'}</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-center">
                        <div class="card-body">
                            <h5 class="card-title">已关闭</h5>
                            <p class="card-text display-6">${userClosedCount!'0'}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 反馈列表 -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <div>
                        <span>我的反馈</span>
                        <div class="btn-group ms-3">
                            <button type="button" class="btn btn-outline-secondary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                ${type!'全部类型'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?type=">全部类型</a></li>
                                <li><a class="dropdown-item" href="?type=suggestion">建议</a></li>
                                <li><a class="dropdown-item" href="?type=bug">Bug</a></li>
                                <li><a class="dropdown-item" href="?type=complaint">投诉</a></li>
                                <li><a class="dropdown-item" href="?type=other">其他</a></li>
                            </ul>
                        </div>
                        <div class="btn-group ms-2">
                            <button type="button" class="btn btn-outline-secondary btn-sm dropdown-toggle" 
                                data-bs-toggle="dropdown">
                                ${status!'全部状态'}
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="?status=">全部状态</a></li>
                                <li><a class="dropdown-item" href="?status=pending">待处理</a></li>
                                <li><a class="dropdown-item" href="?status=processing">处理中</a></li>
                                <li><a class="dropdown-item" href="?status=resolved">已解决</a></li>
                                <li><a class="dropdown-item" href="?status=closed">已关闭</a></li>
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
                                    <div>
                                        <#if feedback.assignedTo??>
                                            <small class="text-muted me-2">已分配给: ${feedback.assignedTo}</small>
                                        </#if>
                                        <span class="badge bg-${feedback.status=='pending'?'warning':
                                            (feedback.status=='processing'?'info':
                                            (feedback.status=='resolved'?'success':'secondary'))}">${feedback.status}</span>
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
                                <a class="page-link" href="?page=${feedbacks.number - 1}${type??'&type='+type}${status??'&status='+status}">上一页</a>
                            </li>
                        </#if>
                        
                        <#list 0..feedbacks.totalPages-1 as i>
                            <li class="page-item ${(i == feedbacks.number)?string('active', '')}">
                                <a class="page-link" href="?page=${i}${type??'&type='+type}${status??'&status='+status}">${i + 1}</a>
                            </li>
                        </#list>
                        
                        <#if feedbacks.hasNext()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${feedbacks.number + 1}${type??'&type='+type}${status??'&status='+status}">下一页</a>
                            </li>
                        </#if>
                    </ul>
                </nav>
            </#if>
        </div>
    </div>
</@base.base> 