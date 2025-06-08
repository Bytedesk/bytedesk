<#import "layout/base.ftl" as base>

<@base.base title="${post??'编辑帖子':'发布新帖子'} - ByteDesk Forum">
    <div class="row">
        <div class="col-12">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/forum">首页</a></li>
                    <li class="breadcrumb-item active">${post??'发布新帖子':'编辑帖子'}</li>
                </ol>
            </nav>

            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">${post??'发布新帖子':'编辑帖子'}</h5>
                </div>
                <div class="card-body">
                    <form id="postForm" method="POST" action="/api/v1/posts${post??'/'+post.id:''}">
                        <div class="mb-3">
                            <label for="title" class="form-label">标题</label>
                            <input type="text" class="form-control" id="title" name="title" 
                                value="${(post.title)!''}" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="categoryId" class="form-label">分类</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">选择分类</option>
                                <#list categories as category>
                                    <option value="${category.id}" 
                                        ${((post.categoryId?? && post.categoryId == category.id) || 
                                        (selectedCategory?? && selectedCategory.id == category.id))?string('selected', '')}>
                                        ${category.name}
                                    </option>
                                </#list>
                            </select>
                        </div>
                        
                        <div class="mb-3">
                            <label for="content" class="form-label">内容</label>
                            <textarea class="form-control" id="content" name="content" 
                                rows="10" required>${(post.content)!''}</textarea>
                        </div>

                        <input type="hidden" name="_method" value="${post??'PUT':'POST'}">
                        
                        <div class="text-end">
                            <a href="javascript:history.back()" class="btn btn-secondary">取消</a>
                            <button type="submit" class="btn btn-primary">提交</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('postForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            const method = formData.get('_method');
            formData.delete('_method');
            
            fetch(this.action, {
                method: method,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(formData)
            }).then(response => {
                if (response.ok) {
                    window.location.href = '/forum';
                }
            });
        });
    </script>
</@base.base> 