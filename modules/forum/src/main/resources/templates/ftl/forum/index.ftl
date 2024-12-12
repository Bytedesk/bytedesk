<#import "layout/base.ftl" as base>

<@base.base title="ByteDesk Forum - 首页">
    <div class="row">
        <!-- 左侧分类列表 -->
        <div class="col-md-3">
            <div class="card">
                <div class="card-header">
                    分类
                </div>
                <div class="list-group list-group-flush">
                    <#list categories as category>
                        <a href="/forum/category/${category.id}" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            ${category.name}
                            <span class="badge bg-primary rounded-pill">${category.postCount}</span>
                        </a>
                    </#list>
                </div>
            </div>
        </div>

        <!-- 右侧帖子列表 -->
        <div class="col-md-9">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <span>最新帖子</span>
                    <a href="/forum/posts/create" class="btn btn-primary btn-sm">发布帖子</a>
                </div>
                <div class="list-group list-group-flush">
                    <#list posts.content as post>
                        <div class="list-group-item">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1">
                                    <a href="/forum/posts/${post.id}" class="text-decoration-none">${post.title}</a>
                                </h5>
                                <small>${post.createdAt?string('yyyy-MM-dd HH:mm')}</small>
                            </div>
                            <p class="mb-1 text-truncate">${post.content}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <small>
                                    <span class="me-2">
                                        <i class="bi bi-eye"></i> ${post.viewCount}
                                    </span>
                                    <span class="me-2">
                                        <i class="bi bi-chat"></i> ${post.commentCount}
                                    </span>
                                    <span>
                                        <i class="bi bi-heart"></i> ${post.likeCount}
                                    </span>
                                </small>
                                <small>作者: ${post.userId}</small>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>

            <!-- 分页 -->
            <nav class="mt-4">
                <ul class="pagination justify-content-center">
                    <#if posts.hasPrevious()>
                        <li class="page-item">
                            <a class="page-link" href="?page=${posts.number - 1}">上一页</a>
                        </li>
                    </#if>
                    
                    <#list 0..posts.totalPages-1 as i>
                        <li class="page-item ${(i == posts.number)?string('active', '')}">
                            <a class="page-link" href="?page=${i}">${i + 1}</a>
                        </li>
                    </#list>
                    
                    <#if posts.hasNext()>
                        <li class="page-item">
                            <a class="page-link" href="?page=${posts.number + 1}">下一页</a>
                        </li>
                    </#if>
                </ul>
            </nav>
        </div>
    </div>
</@base.base>