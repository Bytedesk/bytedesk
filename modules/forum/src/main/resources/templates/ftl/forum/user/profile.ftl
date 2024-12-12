<#import "../layout/base.ftl" as base>

<@base.base title="个人中心 - ByteDesk Forum">
    <div class="row">
        <!-- 左侧导航 -->
        <div class="col-md-3">
            <div class="card">
                <div class="card-header">
                    个人中心
                </div>
                <div class="list-group list-group-flush">
                    <a href="/forum/user/profile" class="list-group-item list-group-item-action active">
                        基本信息
                    </a>
                    <a href="/forum/user/posts" class="list-group-item list-group-item-action">
                        我的帖子
                    </a>
                    <a href="/forum/user/comments" class="list-group-item list-group-item-action">
                        我的评论
                    </a>
                    <a href="/forum/user/likes" class="list-group-item list-group-item-action">
                        我的点赞
                    </a>
                    <a href="/forum/user/settings" class="list-group-item list-group-item-action">
                        账号设置
                    </a>
                </div>
            </div>
        </div>

        <!-- 右侧内容 -->
        <div class="col-md-9">
            <div class="card">
                <div class="card-header">
                    基本信息
                </div>
                <div class="card-body">
                    <form id="profileForm" class="needs-validation" novalidate>
                        <div class="row mb-3">
                            <label class="col-sm-2 col-form-label">用户名</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" value="${currentUser.username}" readonly>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label for="nickname" class="col-sm-2 col-form-label">昵称</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" id="nickname" name="nickname" 
                                    value="${currentUser.nickname!''}" required>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label for="email" class="col-sm-2 col-form-label">邮箱</label>
                            <div class="col-sm-10">
                                <input type="email" class="form-control" id="email" name="email" 
                                    value="${currentUser.email!''}" required>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label for="bio" class="col-sm-2 col-form-label">个人简介</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" id="bio" name="bio" 
                                    rows="3">${currentUser.bio!''}</textarea>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <label class="col-sm-2 col-form-label">注册时间</label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" 
                                    value="${currentUser.createdAt?string('yyyy-MM-dd HH:mm:ss')}" readonly>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-sm-10 offset-sm-2">
                                <button type="submit" class="btn btn-primary">保存修改</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- 统计信息 -->
            <div class="card mt-4">
                <div class="card-header">
                    统计信息
                </div>
                <div class="card-body">
                    <div class="row text-center">
                        <div class="col-md-4">
                            <h4>${userStats.postCount}</h4>
                            <p class="text-muted">发帖数</p>
                        </div>
                        <div class="col-md-4">
                            <h4>${userStats.commentCount}</h4>
                            <p class="text-muted">评论数</p>
                        </div>
                        <div class="col-md-4">
                            <h4>${userStats.likeCount}</h4>
                            <p class="text-muted">获赞数</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('profileForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            fetch('/api/v1/users/profile', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(formData)
            }).then(response => {
                if (response.ok) {
                    alert('保存成功');
                    location.reload();
                }
            });
        });
    </script>
</@base.base> 