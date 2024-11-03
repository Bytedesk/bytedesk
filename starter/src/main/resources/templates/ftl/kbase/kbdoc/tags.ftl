<#include "module/macro.ftl">
<@layout title="标签列表 - ${blog_title!}">
    <h1>标签列表</h1>
    <ul>
        <@tagTag method="list">
            <#if tags?? && tags?size gt 0>
                <#list tags as tag>
                    <li><a href="${tag.fullPath!}">${tag.name}</a></li>
                </#list>
            </#if>
        </@tagTag>
    </ul>
</@layout>
