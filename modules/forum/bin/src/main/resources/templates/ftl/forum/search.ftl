<#import "layout/base.ftl" as base>

<@base.base title="搜索结果 - ByteDesk Forum">
    <div class="row">
        <div class="col-12">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="/forum">首页</a></li>
                    <li class="breadcrumb-item active">搜索结果</li>
                </ol>
            </nav>

            <div class="card">
                <div class="card-header">
                    搜索结果: "${keyword}"
                </div>
                <div class="list-group list-group-flush">
                    <#if posts.totalElements == 0>
                        <div class="list-group-item text-center text-muted">
                            没有找到相关内容
                        </div>
                    <#else>
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
                    </#if>
                </div>
            </div>

            <!-- 分页 -->
            <#if posts.totalElements gt 0>
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <#if posts.hasPrevious()>
                            <li class="page-item">
                                <a class="page-link" href="?keyword=${keyword}&page=${posts.number - 1}">上一页</a>
                            </li>
                        </#if>
                        
                        <#list 0..posts.totalPages-1 as i>
                            <li class="page-item ${(i == posts.number)?string('active', '')}">
                                <a class="page-link" href="?keyword=${keyword}&page=${i}">${i + 1}</a>
                            </li>
                        </#list>
                        
                        <#if posts.hasNext()>
                            <li class="page-item">
                                <a class="page-link" href="?keyword=${keyword}&page=${posts.number + 1}">下一页</a>
                            </li>
                        </#if>
                    </ul>
                </nav>
            </#if>
        </div>
    </div>
</@base.base> 