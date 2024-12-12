$(document).ready(function() {
    // 初始化文件上传
    $('.custom-file-input').on('change', function() {
        let fileName = $(this).val().split('\\').pop();
        $(this).next('.custom-file-label').html(fileName);
    });

    // 状态更新确认
    $('form[action$="/status"]').on('submit', function(e) {
        e.preventDefault();
        let status = $(this).find('select[name="status"]').val();
        let message = '确定要将工单状态更新为"' + getStatusText(status) + '"吗？';
        
        if (confirm(message)) {
            this.submit();
        }
    });

    // 工单分配确认
    $('form[action$="/assign"]').on('submit', function(e) {
        e.preventDefault();
        let assigneeName = $(this).find('select[name="assigneeId"] option:selected').text();
        
        if (confirm('确定要将工单分配给"' + assigneeName + '"吗？')) {
            this.submit();
        }
    });

    // 评论预览
    let previewTimeout;
    $('textarea[name="content"]').on('input', function() {
        clearTimeout(previewTimeout);
        let content = $(this).val();
        let $preview = $('#comment-preview');
        
        previewTimeout = setTimeout(function() {
            $.post('/api/v1/markdown/preview', {content: content}, function(html) {
                $preview.html(html);
            });
        }, 500);
    });

    // 内部评论切换
    $('#internal').on('change', function() {
        let $form = $(this).closest('form');
        if (this.checked) {
            $form.addClass('bg-light');
        } else {
            $form.removeClass('bg-light');
        }
    });

    // 附件预览
    $('.attachment-preview').on('click', function(e) {
        e.preventDefault();
        let url = $(this).attr('href');
        let filename = $(this).data('filename');
        let type = $(this).data('type');
        
        if (type.startsWith('image/')) {
            showImagePreview(url, filename);
        } else if (type.startsWith('text/')) {
            showTextPreview(url, filename);
        } else {
            window.open(url);
        }
    });
});

// 状态文本转换
function getStatusText(status) {
    const statusMap = {
        'open': '待处理',
        'in_progress': '处理中',
        'resolved': '已解决',
        'closed': '已关闭'
    };
    return statusMap[status] || status;
}

// 图片预览
function showImagePreview(url, filename) {
    let modal = `
        <div class="modal fade" id="imagePreviewModal">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${filename}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-center">
                        <img src="${url}" class="img-fluid" alt="${filename}">
                    </div>
                </div>
            </div>
        </div>
    `;
    
    $(modal).modal('show').on('hidden.bs.modal', function() {
        $(this).remove();
    });
}

// 文本预览
function showTextPreview(url, filename) {
    $.get(url, function(content) {
        let modal = `
            <div class="modal fade" id="textPreviewModal">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${filename}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <pre><code>${content}</code></pre>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        $(modal).modal('show').on('hidden.bs.modal', function() {
            $(this).remove();
        });
    });
} 