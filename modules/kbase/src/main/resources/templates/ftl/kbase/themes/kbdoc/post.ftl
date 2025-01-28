<#include "module/macro.ftl">
<@layout title="${post.title!} - ${blog_title!}">
    <h1>${post.title!}</h1>
    <article>
        ${post.formatContent!}
    </article>
    <#include "module/comment.ftl">
    <@comment post=post type="post" />
</@layout>
