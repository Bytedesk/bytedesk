<#import "layout/base.ftl" as base>

<@base.base title="反馈详情 - ByteDesk VOC">
    <div class="row">
        <div class="col-12">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/voc">首页</a></li>
                    <li class="breadcrumb-item active">反馈详情</li>
                </ol>
            </nav>

            <div class="card mb-4">
                <div class="card-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="badge bg-${feedback.type=='bug'?'danger':
                                (feedback.type=='suggestion'?'primary':'secondary')}">${feedback.type}</span>
                            <span class="badge bg-${feedback.status=='pending'?'warning':
                                (feedback.status=='processing'?'info':
                                (feedback.status=='resolved'?'success':'secondary'))}">${feedback.status}</span>
                        </div>
                        <small class="text-muted">
                            提交时间: ${feedback.createdAt?string('yyyy-MM-dd HH:mm:ss')}
                        </small>
                    </div>
                </div>
                <div class="card-body">
                    <p class="card-text">${feedback.content}</p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">回复列表</h5>
                </div>
                <div class="card-body">
                    <form id="replyForm" class="mb-4">
                        <div class="mb-3">
                            <textarea class="form-control" name="content" rows="3" required></textarea>
                        </div>
                        <div class="text-end">
                            <button type="submit" class="btn btn-primary">提交回复</button>
                        </div>
                    </form>

                    <div id="replyList">
                        <#list replies.content as reply>
                            <div class="border-bottom mb-3 pb-3">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <small class="text-muted">
                                        ${reply.createdAt?string('yyyy-MM-dd HH:mm:ss')}
                                    </small>
                                </div>
                                <p class="mb-0">${reply.content}</p>
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@base.base> 