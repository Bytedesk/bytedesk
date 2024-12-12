<#include "../layout/base.ftl">

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">工单列表</h5>
                <div>
                    <a href="/tickets/create" class="btn btn-primary">创建工单</a>
                </div>
            </div>
            <div class="card-body">
                <!-- 搜索表单 -->
                <form class="mb-4" method="get" action="/tickets">
                    <div class="row">
                        <div class="col-md-3">
                            <input type="text" class="form-control" name="keyword" 
                                value="${keyword!''}" placeholder="搜索关键词">
                        </div>
                        <div class="col-md-2">
                            <select class="form-control" name="status">
                                <option value="">所有状态</option>
                                <option value="open" <#if status?? && status == 'open'>selected</#if>>待处理</option>
                                <option value="in_progress" <#if status?? && status == 'in_progress'>selected</#if>>处理中</option>
                                <option value="resolved" <#if status?? && status == 'resolved'>selected</#if>>已解决</option>
                                <option value="closed" <#if status?? && status == 'closed'>selected</#if>>已关闭</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <select class="form-control" name="priority">
                                <option value="">所有优先级</option>
                                <option value="urgent" <#if priority?? && priority == 'urgent'>selected</#if>>紧急</option>
                                <option value="high" <#if priority?? && priority == 'high'>selected</#if>>高</option>
                                <option value="normal" <#if priority?? && priority == 'normal'>selected</#if>>中</option>
                                <option value="low" <#if priority?? && priority == 'low'>selected</#if>>低</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select class="form-control" name="categoryId">
                                <option value="">所有分类</option>
                                <#list categories as category>
                                    <option value="${category.id}" <#if categoryId?? && categoryId == category.id>selected</#if>>
                                        ${category.name}
                                    </option>
                                </#list>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary btn-block">搜索</button>
                        </div>
                    </div>
                </form>

                <!-- 工单列表 -->
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>标题</th>
                                <th>状态</th>
                                <th>优先级</th>
                                <th>分类</th>
                                <th>提交人</th>
                                <th>处理人</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list tickets.content as ticket>
                                <tr>
                                    <td>${ticket.id}</td>
                                    <td>
                                        <a href="/tickets/${ticket.id}">${ticket.title}</a>
                                        <#if ticket.attachments?size gt 0>
                                            <i class="fas fa-paperclip text-muted"></i>
                                        </#if>
                                    </td>
                                    <td>
                                        <span class="badge badge-${getStatusBadgeClass(ticket.status)}">
                                            ${getStatusText(ticket.status)}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge badge-${getPriorityBadgeClass(ticket.priority)}">
                                            ${getPriorityText(ticket.priority)}
                                        </span>
                                    </td>
                                    <td>${getCategoryName(ticket.categoryId)}</td>
                                    <td>${getUserName(ticket.userId)}</td>
                                    <td>${ticket.assignedTo???then(getUserName(ticket.assignedTo), '-')}</td>
                                    <td>${ticket.createdAt?string('yyyy-MM-dd HH:mm:ss')}</td>
                                    <td>
                                        <a href="/tickets/${ticket.id}" class="btn btn-sm btn-info">查看</a>
                                        <#if canEditTicket(ticket)>
                                            <a href="/tickets/${ticket.id}/edit" class="btn btn-sm btn-warning">编辑</a>
                                        </#if>
                                    </td>
                                </tr>
                            <#else>
                                <tr>
                                    <td colspan="9" class="text-center">暂无工单</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>

                <!-- 分页 -->
                <nav>
                    <ul class="pagination justify-content-center">
                        <#if tickets.hasPrevious()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${tickets.number - 1}${getQueryString()}">&laquo;</a>
                            </li>
                        <#else>
                            <li class="page-item disabled">
                                <span class="page-link">&laquo;</span>
                            </li>
                        </#if>

                        <#list 0..tickets.totalPages-1 as i>
                            <li class="page-item <#if i == tickets.number>active</#if>">
                                <a class="page-link" href="?page=${i}${getQueryString()}">${i + 1}</a>
                            </li>
                        </#list>

                        <#if tickets.hasNext()>
                            <li class="page-item">
                                <a class="page-link" href="?page=${tickets.number + 1}${getQueryString()}">&raquo;</a>
                            </li>
                        <#else>
                            <li class="page-item disabled">
                                <span class="page-link">&raquo;</span>
                            </li>
                        </#if>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div> 