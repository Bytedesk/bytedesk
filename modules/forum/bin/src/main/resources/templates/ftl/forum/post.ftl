<#import "layout/base.ftl" as base>

<@base.base title="${post.title} - ByteDesk Forum">
    <div class="row">
        <div class="col-12">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/forum">首页</a></li>
                    <li class="breadcrumb-item"><a href="/forum/category/${post.categoryId}">分类</a></li>
                    <li class="breadcrumb-item active">${post.title}</li>
                </ol>
            </nav>

            <!-- 帖子内容 -->
            <div class="card mb-4">
                <div class="card-header">
                    <h4 class="mb-0">${post.title}</h4>
                    <small class="text-muted">
                        作者: ${post.userId} | 
                        发布时间: ${post.createdAt?string('yyyy-MM-dd HH:mm:ss')} |
                        浏览: ${post.viewCount} |
                        评论: ${post.commentCount} |
                        点赞: ${post.likeCount}
                    </small>
                </div>
                <div class="card-body">
                    <div class="post-content">
                        ${post.content}
                    </div>
                    <div class="text-end mt-3">
                        <button class="btn btn-outline-primary btn-sm" onclick="likePost(${post.id})">
                            <i class="bi bi-heart"></i> 点赞
                        </button>
                    </div>
                </div>
            </div>

            <!-- 评论区 -->
            <div class="card">
                <div class="card-header">
                    评论区
                </div>
                <div class="card-body">
                    <!-- 发表评论 -->
                    <form id="commentForm" class="mb-4">
                        <div class="mb-3">
                            <textarea class="form-control" id="commentContent" rows="3" placeholder="写下你的评论..."></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">发表评论</button>
                    </form>

                    <!-- 评论列表 -->
                    <div class="comments-list">
                        <#list comments.content as comment>
                            <div class="comment mb-3">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <strong>${comment.userId}</strong>
                                        <small class="text-muted ms-2">${comment.createdAt?string('yyyy-MM-dd HH:mm')}</small>
                                    </div>
                                    <button class="btn btn-link btn-sm" onclick="likeComment(${comment.id})">
                                        <i class="bi bi-heart"></i> ${comment.likeCount}
                                    </button>
                                </div>
                                <div class="mt-2">
                                    ${comment.content}
                                </div>
                            </div>
                        </#list>
                    </div>

                    <!-- 评论分页 -->
                    <nav class="mt-4">
                        <ul class="pagination justify-content-center">
                            <#if comments.hasPrevious()>
                                <li class="page-item">
                                    <a class="page-link" href="?page=${comments.number - 1}#comments">上一页</a>
                                </li>
                            </#if>
                            
                            <#list 0..comments.totalPages-1 as i>
                                <li class="page-item ${(i == comments.number)?string('active', '')}">
                                    <a class="page-link" href="?page=${i}#comments">${i + 1}</a>
                                </li>
                            </#list>
                            
                            <#if comments.hasNext()>
                                <li class="page-item">
                                    <a class="page-link" href="?page=${comments.number + 1}#comments">下一页</a>
                                </li>
                            </#if>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <script>
        function likePost(postId) {
            fetch(`/api/v1/posts/${postId}/like`, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        }

        function likeComment(commentId) {
            fetch(`/api/v1/comments/${commentId}/like`, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        }

        document.getElementById('commentForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const content = document.getElementById('commentContent').value;
            
            fetch('/api/v1/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(content)}&postId=${post.id}&userId=${currentUserId}`
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        });
    </script>
</@base.base> 