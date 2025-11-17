<#include "../layout/base.ftl">

<div class="row">
    <div class="col-md-8">
        <!-- 工单详情 -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">${ticket.summary}</h5>
            </div>
            <div class="card-body">
                <div class="ticket-content mb-4">
                    ${ticket.content}
                </div>
                
                <#if ticket.attachments?size gt 0>
                    <div class="attachments mb-4">
                        <h6>附件：</h6>
                        <div class="list-group">
                            <#list ticket.attachments as attachment>
                                <a href="/attachments/${attachment.id}" class="list-group-item list-group-item-action">
                                    <i class="fas fa-file"></i> ${attachment.filename}
                                </a>
                            </#list>
                        </div>
                    </div>
                </#if>
                
                <div class="ticket-meta text-muted">
                    <small>
                        创建于：${ticket.createdAt?string('yyyy-MM-dd HH:mm:ss')} |
                        提交人：${getUserName(ticket.userId)} |
                        状态：<span class="badge badge-${getStatusBadgeClass(ticket.status)}">
                            ${getStatusText(ticket.status)}
                        </span> |
                        优先级：<span class="badge badge-${getPriorityBadgeClass(ticket.priority)}">
                            ${getPriorityText(ticket.priority)}
                        </span>
                    </small>
                </div>
            </div>
        </div>

        <!-- 评论列表 -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">评论</h5>
            </div>
            <div class="card-body">
                <#list comments as comment>
                    <div class="comment mb-4">
                        <div class="d-flex">
                            <div class="flex-shrink-0">
                                <img src="${getAvatarUrl(comment.userId)}" class="rounded-circle" width="40">
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <div class="comment-header">
                                    <strong>${getUserName(comment.userId)}</strong>
                                    <small class="text-muted">
                                        ${comment.createdAt?string('yyyy-MM-dd HH:mm:ss')}
                                    </small>
                                    <#if comment.internal>
                                        <span class="badge badge-info">内部评论</span>
                                    </#if>
                                </div>
                                <div class="comment-content mt-2">
                                    ${comment.content}
                                </div>
                                <#if comment.attachments?size gt 0>
                                    <div class="comment-attachments mt-2">
                                        <div class="list-group">
                                            <#list comment.attachments as attachment>
                                                <a href="/attachments/${attachment.id}" 
                                                   class="list-group-item list-group-item-action">
                                                    <i class="fas fa-file"></i> ${attachment.filename}
                                                </a>
                                            </#list>
                                        </div>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </div>
                </#list>

                <!-- 添加评论表单 -->
                <form method="post" action="/tickets/${ticket.id}/comments" enctype="multipart/form-data">
                    <div class="form-group">
                        <textarea class="form-control" name="content" rows="3" required></textarea>
                    </div>
                    <div class="form-group">
                        <div class="custom-file">
                            <input type="file" class="custom-file-input" name="attachments" multiple>
                            <label class="custom-file-label">选择附件</label>
                        </div>
                    </div>
                    <#if isAgent()>
                        <div class="form-check mb-3">
                            <input type="checkbox" class="form-check-input" name="internal" id="internal">
                            <label class="form-check-label" for="internal">内部评论</label>
                        </div>
                    </#if>
                    <button type="submit" class="btn btn-primary">提交评论</button>
                </form>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <!-- 工单操作 -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">工单操作</h5>
            </div>
            <div class="card-body">
                <#if canEditTicket(ticket)>
                    <a href="/tickets/${ticket.id}/edit" class="btn btn-warning btn-block mb-2">编辑工单</a>
                </#if>
                
                <#if isAgent()>
                    <form method="post" action="/tickets/${ticket.id}/status" class="mb-2">
                        <select class="form-control mb-2" name="status">
                            <option value="open" <#if ticket.status == 'open'>selected</#if>>待处理</option>
                            <option value="in_progress" <#if ticket.status == 'in_progress'>selected</#if>>处理中</option>
                            <option value="resolved" <#if ticket.status == 'resolved'>selected</#if>>已解决</option>
                            <option value="closed" <#if ticket.status == 'closed'>selected</#if>>已关闭</option>
                        </select>
                        <button type="submit" class="btn btn-primary btn-block">更新状态</button>
                    </form>

                    <form method="post" action="/tickets/${ticket.id}/assign" class="mb-2">
                        <select class="form-control mb-2" name="assigneeId">
                            <option value="">选择处理人</option>
                            <#list agents as agent>
                                <option value="${agent.id}" <#if ticket.assignedTo?? && ticket.assignedTo == agent.id>selected</#if>>
                                    ${agent.name}
                                </option>
                            </#list>
                        </select>
                        <button type="submit" class="btn btn-info btn-block">分配工单</button>
                    </form>
                </#if>
            </div>
        </div>

        <!-- 工单信息 -->
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0">工单信息</h5>
            </div>
            <div class="card-body">
                <dl class="row">
                    <dt class="col-sm-4">工单编号</dt>
                    <dd class="col-sm-8">${ticket.id}</dd>

                    <dt class="col-sm-4">创建时间</dt>
                    <dd class="col-sm-8">${ticket.createdAt?string('yyyy-MM-dd HH:mm:ss')}</dd>

                    <dt class="col-sm-4">更新时间</dt>
                    <dd class="col-sm-8">${ticket.updatedAt?string('yyyy-MM-dd HH:mm:ss')}</dd>

                    <dt class="col-sm-4">处理人</dt>
                    <dd class="col-sm-8">${ticket.assignedTo???then(getUserName(ticket.assignedTo), '-')}</dd>

                    <dt class="col-sm-4">分类</dt>
                    <dd class="col-sm-8">${getCategoryName(ticket.categoryId)}</dd>

                    <#if ticket.resolvedAt??>
                        <dt class="col-sm-4">解决时间</dt>
                        <dd class="col-sm-8">${ticket.resolvedAt?string('yyyy-MM-dd HH:mm:ss')}</dd>
                    </#if>

                    <#if ticket.closedAt??>
                        <dt class="col-sm-4">关闭时间</dt>
                        <dd class="col-sm-8">${ticket.closedAt?string('yyyy-MM-dd HH:mm:ss')}</dd>
                    </#if>
                </dl>
            </div>
        </div>
    </div>
</div> 