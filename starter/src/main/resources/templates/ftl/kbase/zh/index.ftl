<!doctype html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    
    <title>${knowledgebase.name!'Bytedesk'} - ${knowledgebase.headline!'Help Center'}</title>

    <#include "../common/header.ftl"/>

    <style>
        .section-hero {
            background-color: ${knowledgebase.primaryColor!'#02b3e4'};
        }
        .box {
            border: 1px solid ${knowledgebase.primaryColor!'#02b3e4'};;
        }
        .box h3 {
            color: ${knowledgebase.primaryColor!'#02b3e4'};
        }
    </style>

</head>

<body>

    <#include "../common/toplink_home.ftl"/>

    <div class="uk-section">
        <div class="uk-container">
            <div id="supportCategory" class="uk-child-width-1-3@s uk-grid-match uk-grid-medium uk-text-center" uk-grid>
                <#--  <#list categories as category>
                    <div>
                        <a href="category_${category.cid}.html" target="_blank" class="box uk-border-rounded">
                            <h3>${category.name!''}</h3>
                        </a>
                    </div>
                </#list>  -->
            </div>
        </div>
    </div>

    <div class="uk-section uk-padding-remove-top uk-padding-remove-bottom">
        <div class="uk-container">
            <hr>
        </div>
    </div>

    <div class="uk-section">
        <div class="uk-container">
            <div class="uk-child-width-1-3@s text-dark" uk-grid>
                <div>
                    <h3>置顶问题</h3>
                    <#--  <ul id="supportHotArticle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <#list articlesTop.content as article>
                            <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a><span style="float: right;">${article.updatedAt?substring(5, 16)}</span></li>
                        </#list>
                        <#if (articlesTop.content?size == 0)>
                            <div>
                                暂无置顶
                            </div>
                        </#if>
                    </ul>  -->
                </div>
                <div>
                    <h3>热门问题</h3>
                    <#--  <ul id="supportHotArticle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <#list articlesHot.content as article>
                            <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a><span style="float: right;">${article.updatedAt?substring(5, 16)}</span></li>
                        </#list>
                        <#if (articlesHot.content?size == 0)>
                            <div>
                                暂无文章
                            </div>
                        </#if>
                    </ul>  -->
                </div>
                <div>
                    <h3>最近更新</h3>
                    <#--  <ul id="supportLatestArtcle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <#list articlesRecent.content as article>
                            <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a><span style="float: right;">${article.updatedAt?substring(5, 16)}</span></li>
                        </#list>
                        <#if (articlesRecent.content?size == 0)>
                            <div>
                                暂无文章
                            </div>
                        </#if>
                    </ul>  -->
                </div>
            </div>
        </div>
    </div>

    <#--  <div class="uk-section uk-padding-remove section-cta uk-background-blend-lighten uk-background-center-center uk-background-cover uk-text-center" style="background-image: url(/img/cafe.jpg)" >
        <div class="uk-background-muted1 uk-border-rounded1 uk-padding-large">
            <h2>找不到您要的答案?</h2>
            <p class="uk-margin-medium-top">
                <a id="replaceAgent" href="contact" class="uk-button uk-button-primary uk-button-large" target="_blank">在线客服</a>
            </p>
        </div>
    </div>  -->

    <#include "../common/footer_home.ftl"/>

    <#include "../common/offcanvas.ftl"/>

    <#--  <#include "../common/bytedesk.ftl"/>  -->

    <#include "../common/img_enlarge.ftl"/>

</body>

</html>
