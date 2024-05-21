<#include "module/macro.ftl">
<@layout title="分类：${category.name} - ${blog_title!}">
    <h1>分类：${category.name}</h1>
    <ul>
        <#list posts.content as post>
            <li>
                <a href="${post.fullPath}">${post.title}</a>
            </li>
        </#list>
    </ul>

    <h1>分页</h1>

    <#if posts.totalPages gt 1>
        <ul>
            <@paginationTag method="categoryPosts" page="${posts.number}" total="${posts.totalPages}" display="3" slug="${category.slug!}">
                <#if pagination.hasPrev>
                    <li>
                        <a href="${pagination.prevPageFullPath!}">
                            上一页
                        </a>
                    </li>
                </#if>
                <#list pagination.rainbowPages as number>
                    <li>
                        <#if number.isCurrent>
                            <span class="current">第 ${number.page!} 页</span>
                        <#else>
                            <a href="${number.fullPath!}">第 ${number.page!} 页</a>
                        </#if>
                    </li>
                </#list>
                <#if pagination.hasNext>
                    <li>
                        <a href="${pagination.nextPageFullPath!}">
                            下一页
                        </a>
                    </li>
                </#if>
            </@paginationTag>
        </ul>
    <#else>
        <span>当前只有一页</span>
    </#if>
</@layout>
