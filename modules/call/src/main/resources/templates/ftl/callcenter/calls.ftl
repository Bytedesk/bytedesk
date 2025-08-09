<#import "layout/base.ftl" as base>

<@base.base title="呼叫记录 - ByteDesk 呼叫中心">
    <div class="row mb-4">
        <div class="col">
            <h2>呼叫记录</h2>
        </div>
    </div>

    <!-- 过滤器 -->
    <div class="row mb-4">
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <form method="get" action="/callcenter/calls" class="row g-3">
                        <div class="col-md-4">
                            <label for="status" class="form-label">状态</label>
                            <select class="form-select" id="status" name="status">
                                <option value="">全部</option>
                                <option value="answered" <#if status?? && status == 'answered'>selected</#if>>已接通</option>
                                <option value="missed" <#if status?? && status == 'missed'>selected</#if>>未接通</option>
                                <option value="busy" <#if status?? && status == 'busy'>selected</#if>>忙线</option>
                                <option value="canceled" <#if status?? && status == 'canceled'>selected</#if>>已取消</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label for="dateRange" class="form-label">日期范围</label>
                            <input type="text" class="form-control" id="dateRange" name="dateRange" placeholder="选择日期范围">
                        </div>
                        <div class="col-md-4 align-self-end">
                            <button type="submit" class="btn btn-primary">筛选</button>
                            <a href="/callcenter/calls" class="btn btn-outline-secondary ms-2">重置</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- 呼叫记录列表 -->
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>主叫</th>
                                    <th>被叫</th>
                                    <th>开始时间</th>
                                    <th>结束时间</th>
                                    <th>持续时间</th>
                                    <th>状态</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#if calls?? && calls.content?size gt 0>
                                    <#list calls.content as call>
                                        <tr>
                                            <td>${call.id}</td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar me-2">
                                                        <#if call.callerAvatar??>
                                                            <img src="${call.callerAvatar}" alt="${call.callerName}" class="rounded-circle" width="32" height="32">
                                                        <#else>
                                                            <div class="avatar-placeholder">${call.callerName?substring(0,1)}</div>
                                                        </#if>
                                                    </div>
                                                    ${call.callerName}
                                                </div>
                                            </td>
                                            <td>
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar me-2">
                                                        <#if call.calleeAvatar??>
                                                            <img src="${call.calleeAvatar}" alt="${call.calleeName}" class="rounded-circle" width="32" height="32">
                                                        <#else>
                                                            <div class="avatar-placeholder">${call.calleeName?substring(0,1)}</div>
                                                        </#if>
                                                    </div>
                                                    ${call.calleeName}
                                                </div>
                                            </td>
                                            <td>${call.startTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                            <td>
                                                <#if call.endTime??>
                                                    ${call.endTime?string('yyyy-MM-dd HH:mm:ss')}
                                                <#else>
                                                    -
                                                </#if>
                                            </td>
                                            <td>
                                                <#if call.duration??>
                                                    ${call.duration} 秒
                                                <#else>
                                                    -
                                                </#if>
                                            </td>
                                            <td>
                                                <#if call.status == 'answered'>
                                                    <span class="badge bg-success">已接通</span>
                                                <#elseif call.status == 'missed'>
                                                    <span class="badge bg-danger">未接通</span>
                                                <#elseif call.status == 'busy'>
                                                    <span class="badge bg-warning">忙线</span>
                                                <#elseif call.status == 'canceled'>
                                                    <span class="badge bg-secondary">已取消</span>
                                                <#elseif call.status == 'active'>
                                                    <span class="badge bg-primary">通话中</span>
                                                <#else>
                                                    <span class="badge bg-info">${call.status}</span>
                                                </#if>
                                            </td>
                                            <td>
                                                <a href="/callcenter/call/${call.id}" class="btn btn-sm btn-outline-primary" title="查看详情">
                                                    <i class="bi bi-info-circle"></i>
                                                </a>
                                                <#if call.recordingUrl??>
                                                    <button class="btn btn-sm btn-outline-secondary" title="播放录音" onclick="playRecording('${call.recordingUrl}')">
                                                        <i class="bi bi-play-circle"></i>
                                                    </button>
                                                </#if>
                                            </td>
                                        </tr>
                                    </#list>
                                <#else>
                                    <tr>
                                        <td colspan="8" class="text-center py-4">
                                            <div class="text-muted">
                                                <i class="bi bi-telephone-x h1"></i>
                                                <p class="mt-3">暂无呼叫记录</p>
                                            </div>
                                        </td>
                                    </tr>
                                </#if>
                            </tbody>
                        </table>
                    </div>

                    <!-- 分页 -->
                    <#if calls?? && calls.totalPages gt 1>
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item <#if calls.number == 0>disabled</#if>">
                                    <a class="page-link" href="?page=${calls.number - 1}&status=${status!''}" aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                
                                <#list 0..calls.totalPages-1 as i>
                                    <#if i == calls.number>
                                        <li class="page-item active"><span class="page-link">${i+1}</span></li>
                                    <#elseif i <= calls.number + 2 && i >= calls.number - 2>
                                        <li class="page-item"><a class="page-link" href="?page=${i}&status=${status!''}">${i+1}</a></li>
                                    </#if>
                                </#list>
                                
                                <li class="page-item <#if calls.number + 1 == calls.totalPages>disabled</#if>">
                                    <a class="page-link" href="?page=${calls.number + 1}&status=${status!''}" aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </#if>
                </div>
            </div>
        </div>
    </div>

    <!-- 录音播放器对话框 -->
    <div class="modal fade" id="recordingModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">通话录音</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body text-center">
                    <audio id="recordingPlayer" controls class="w-100"></audio>
                </div>
            </div>
        </div>
    </div>

    <!-- 日期选择器和录音播放功能 -->
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <link href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css" rel="stylesheet">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 初始化日期选择器
            flatpickr("#dateRange", {
                mode: "range",
                dateFormat: "Y-m-d",
                locale: {
                    rangeSeparator: ' 至 '
                }
            });
        });
        
        // 播放录音功能
        function playRecording(url) {
            const player = document.getElementById('recordingPlayer');
            player.src = url;
            const modal = new bootstrap.Modal(document.getElementById('recordingModal'));
            modal.show();
            player.play();
        }
    </script>

    <style>
        .avatar-placeholder {
            width: 32px;
            height: 32px;
            background-color: #6c757d;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
        }
    </style>
</@base.base>
