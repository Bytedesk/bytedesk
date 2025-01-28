<#include "module/macro.ftl">
<@layout title="分类列表 - ${blog_title!}">
    <h1>分类列表</h1>
    <ul>
        <@categoryTag method="list">
            <#if categories?? && categories?size gt 0>
                <#list categories as category>
                    <li><a href="${category.fullPath!}">${category.name}</a></li>
                </#list>
            </#if>
        </@categoryTag>
    </ul>
</@layout>
