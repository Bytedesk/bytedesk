<#import "layout/base.ftl" as base>

<@base.base title="提交反馈 - ByteDesk VOC">
    <div class="row">
        <div class="col-md-8 mx-auto">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">提交反馈</h5>
                </div>
                <div class="card-body">
                    <form id="feedbackForm" class="needs-validation" novalidate>
                        <div class="mb-3">
                            <label for="type" class="form-label">反馈类型</label>
                            <select class="form-select" id="type" name="type" required>
                                <option value="">请选择反馈类型</option>
                                <option value="suggestion">建议</option>
                                <option value="bug">Bug</option>
                                <option value="complaint">投诉</option>
                                <option value="other">其他</option>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label for="content" class="form-label">反馈内容</label>
                            <textarea class="form-control" id="content" name="content" rows="5" required></textarea>
                        </div>

                        <button type="submit" class="btn btn-primary">提交反馈</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</@base.base> 