<!doctype html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    
    <title>${knowledgebase.name!'萝卜丝'} - ${knowledgebase.headline!'帮助中心'}</title>

    <#include "../../../common/template/header.ftl"/>

</head>

<body>

    <#include "../../../common/template/toplink_home.ftl"/>

    <div class="uk-section">
        <div class="uk-container">
            <div id="supportCategory" class="uk-child-width-1-3@s uk-grid-match uk-grid-medium uk-text-center" uk-grid>
                <#list categories as category>
                    <div>
                        <a href="category_${category.cid}.html" target="_blank" class="box uk-border-rounded">
                            <h3>${category.name!''}</h3>
                        </a>
                    </div>
                </#list>
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
            <div class="uk-child-width-1-2@s text-dark" uk-grid>
                <div>
                    <h3>热门问题</h3>
                    <ul id="supportHotArticle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <#list articles.content as article>
                            <#if article.recommend>
                                <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a></li>
                            </#if>
                        </#list>
                    </ul>
                </div>
                <div>
                    <h3>最近更新</h3>
                    <ul id="supportLatestArtcle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <#list articles.content as article>
                            <#if !article.recommend>
                                <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a></li>
                            </#if>
                        </#list>
                    </ul>
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

    <#include "../../../common/template/footer_home.ftl"/>

    <#include "../../../common/template/offcanvas.ftl"/>


</body>

</html>
