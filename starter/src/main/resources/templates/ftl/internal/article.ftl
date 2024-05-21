
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>${article.title!''} - 文章详情 - ${knowledgebase.headline!'帮助中心'} - ${knowledgebase.name!'萝卜丝'}</title>

    <#include "../../../common/template/header.ftl"/>

</head>

<body>

    <#include "../../../common/template/toplink.ftl"/>

    <div class="uk-section section-sub-nav uk-padding-remove">
        <div class="uk-container">
            <div uk-grid>
                <div class="uk-width-2-3@m">
                    <ul id="supportArticleBreadcrumb" class="uk-breadcrumb uk-visible@m">
                        <li><a href="/">首页</a></li>
                        <li><a href="category_${article.categories[0].cid}.html">${article.categories[0].name}</a></li>
                        <li><span href="">${article.title!''}</span></li>
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
        <div class="uk-container container-xs">
            <article class="uk-article">

                <!-- 文章作者 -->
                <header>
                    <h1 id="supportArticleTitle" class="uk-article-title uk-margin-bottom">${article.title!''}</h1>
                    <div class="author-box uk-card">
                        <div class="uk-card-header uk-padding-remove">
                            <div class="uk-grid-small uk-flex-middle  uk-position-relative" uk-grid>
                                <div class="uk-width-auto">
                                    <img id="supportArticleUserAvatar" class="uk-border-circle" width="40" height="40" src="${article.user.avatar}">
                                </div>
                                <div class="uk-width-expand">
                                    <h5 id="supportArticleUserNickname" class="uk-card-title">${article.user.nickname!''}</h5>
                                    <#--  <p id="supportArticleUpdatedAt" class="uk-article-meta uk-margin-remove-top">${(article.createdAt)?string("yyyy-MM-dd HH:mm:ss")}</p>  -->
                                    <p id="supportArticleUpdatedAt" class="uk-article-meta uk-margin-remove-top">${article.createdAt}</p>
                                </div>
                                <!-- <a href="#" class="uk-button uk-button-outline-primary uk-button-small uk-visible@m">关注</a> -->
                                <a id="supportArticleReadCount" href="#" class="uk-button uk-button-outline-primary uk-button-small uk-visible@m">阅读次数:${article.readCount}</a>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- 文章内容 -->
                <div class="entry-content uk-margin-medium-top">
                    <p id="supportArticleSummary" class="uk-text-lead">${article.summary!''}</p>
                    <div id="supportArticleContent">${article.content!''}</div>
                </div>

                <!-- 评价 -->
                <div class="article-votes uk-text-center uk-margin-medium-top uk-padding uk-padding-remove-horizontal border-bottom border-top">
                    <h3>是否对您有帮助?</h3>
                    <a id="supportArticleRateHelpfull" href="#" class="vote uk-button uk-button-outline-primary" onclick="utils.rateHelpfull()"><span class="uk-margin-small-right" uk-icon="icon: check; ratio: 0.8"></span>是的</a>
                    <a id="supportArticleRateHelpless" href="#" class="vote uk-button uk-button-outline-primary uk-margin-small-left" onclick="utils.rateHelpless()"><span class="uk-margin-small-right uk-inline" uk-icon="icon: close; ratio: 0.8"></span>没有</a>
                    <#--  <p id="supportArticleRateThanks" class="vote-thanks uk-margin-remove">O(∩_∩)O谢谢反馈!</p>  -->
                    <p class="vote-question text-dark">有更多问题? <a href="#">提交工单</a></p>
                </div>

                <!-- 相关 -->
                <div class="uk-child-width-1-2@s text-dark article-related uk-margin-medium-top" uk-grid>
                    <div>
                        <h3>最近阅读</h3>
                        <ul id="supportArticleRecent" class="uk-list uk-list-large">
                            <!-- <li><a href="#">Sed ut perspiciatis unde omnis iste laudantium, totam rem aperiam</a></li> -->
                        </ul>
                    </div>
                    <div>
                        <h3>相关文章</h3>
                        <ul id="supportArticleRelated" class="uk-list uk-list-large">
                            <!-- <li><a href="#">Apsum dolor sit amet, consectet tempor incididunt ut dolore magna aliqua</a></li> -->
                            <#list related as article>
                                <li><a href="article_${article.aid}.html" target="_blank">${article.title!''}</a></li>
                            </#list>
                        </ul>
                    </div>
                </div>

            </article>
        </div>
    </div>

    <#include "../../../common/template/footer.ftl"/>

    <#include "../../../common/template/offcanvas.ftl"/>

    <script>
        
    
    </script>

    

</body>

</html>
