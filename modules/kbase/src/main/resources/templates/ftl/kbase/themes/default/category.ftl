
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>${category.name!''} - 文章分类 - ${knowledgebase.headline!'帮助中心'} - ${knowledgebase.name!'微语'}</title>

    <#include "./template/header.ftl"/>

    <style>
        .uk-background-primary {
            background-color: ${knowledgebase.primaryColor!''};
        }
        .accordion {
            background-color: white;
            cursor: pointer;
            width: 100%;
            text-align: left;
            border: none;
            outline: none;
            transition: 0.4s;
            height: 35px;
            font-size: 17px;
        }
    </style>

</head>

<body>

    <#include "./template/toplink.ftl"/>

    <div class="uk-section section-sub-nav uk-padding-remove">
        <div class="uk-container">
            <div uk-grid>
                <div class="uk-width-2-3@m">
                    <ul class="uk-breadcrumb uk-visible@m">
                        <li><a href="/helpcenter/${knowledgebase.uid!''}">首页</a></li>
                        <li><span href="" id="supportCategoryName">${category.name!''}</span></li>
                    </ul>
                </div>
                <div class="uk-width-1-3@m">
                    <div class="uk-margin">
                        <form class="uk-search uk-search-default">
                            <a href="javascript:void(0)" class="uk-search-icon-flip" uk-search-icon onclick="searchContent()"></a>
                            <input id="supportAdminUid" name="uid" type="text" style="display:none"/>
                            <input id="supportSearch" class="uk-search-input" type="search" 
                                autocomplete="off" placeholder="搜索" onkeyup="onKeyUp(arguments[0] || window.event)">
                        </form>
                    </div>
                </div>
            </div>
            <div class="border-top"></div>
        </div>
    </div>

    <div class="uk-section uk-section-small uk-padding-remove-bottom section-content">
        <div class="uk-container">
            <div class="uk-grid-medium" uk-grid>

                <!-- 左侧列表 -->
                <div class="uk-width-1-4@m text-dark sidebar">
                    <h3>类别</h3>
                    <ul id="supportCategory" class="uk-list uk-list-large uk-margin-medium-bottom">
                        <#list categories as category>
                            <button class="accordion">
                                <a href="/helpcenter/${knowledgebase.uid!''}/category/${category.uid}.html" target="_blank">${category.name!''}</a>
                            </button>
                        </#list>
                        <!-- <li><a href="#">Getting Started</a></li> -->
                        <!-- <li><a class="uk-text-bold" href="#">Account Management</a> <span uk-icon="icon: chevron-right"></span></li> -->
                    </ul>
                    <#--  <h3>相关文章</h3>  -->
                    <ul class="uk-list uk-list-large">
                        <#--  <li>暂无</li>  -->
                        <!-- <li><a href="article">Setting up attributes</a></li>  -->
                        <!-- <li><a href="article">Shipping Options Page</a></li> -->
                    </ul>
                </div>

                <!-- 右侧详情 -->
                <div class="uk-width-3-4@m uk-flex-last@m">
                    <h1 id="supportCategoryName2">${category.name!''}</h1>
                    <!-- <p class="uk-text-lead uk-margin-medium-bottom">Managing your account, creating new users, security and exporting data</p> -->
                    <ul id="supportCategoryArticle" class="uk-list list-category link-icon-right">
                        <!-- <li>
                            <h3><a href="article">Manage payment settings and invoices</span></a></h3>
                        </li> -->
                        <#list articles as article>
                            <li>
                                <a href="/helpcenter/${knowledgebase.uid!''}/article/${article.uid}.html" target="_blank">${article.title!''}<span style="float: right;">${article.updatedAt?substring(5, 16)}</span></a>
                            </li>
                        </#list>
                        <#if (articles?size == 0)>
                            <div>
                                暂无文章
                            </div>
                        </#if>
                    </ul>
                    <!-- 翻页，TODO: 增加支持翻页 -->
                    <ul class="uk-pagination uk-margin-medium-top" uk-margin>
                        <#--  <li><a href="#"><span class="uk-margin-small-right" uk-pagination-previous></span> 上一篇</a></li>  -->
                        <#--  <li class="uk-margin-auto-left"><a href="#">下一篇 <span class="uk-margin-small-left" uk-pagination-next></span></a></li>  -->
                    </ul>
                </div>

            </div>
        </div>
    </div>

    <#include "./template/footer.ftl"/>

    <#include "./template/offcanvas.ftl"/>

    <#include "./template/bytedesk.ftl"/>

</body>

</html>
