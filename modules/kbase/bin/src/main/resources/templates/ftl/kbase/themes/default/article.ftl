
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>${article.title!''} - 文章详情 - ${kbase.headline!'帮助中心'} - ${kbase.name!'微语'}</title>

    <#include "./template/header.ftl"/>

    <style>
        .uk-background-primary {
            background-color: ${kbase.primaryColor!''};
        }
        .uk-button-outline-primary {
            color: ${kbase.primaryColor!''};
            border: solid 1px ${kbase.primaryColor!''};
        }
        /* https://www.runoob.com/try/try.php?filename=tryhtml_js_accordion */
        /* 打开和关闭手风琴面板的样式 */
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
        /* 设置点击和鼠标移到选项上面时（悬停）的样式 */
        .active, .accordion:hover {
            background-color: #eee;
        }
        /* 为手风琴面板设计样式。 默认隐藏 */
        .panel {
            padding: 0 18px;
            background-color: white;
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.2s ease-out;
        }
        /* 设置 +、- 标志 */
        .accordion:after {
            content: '\002B';  /* Unicode 字符 + 号 */
            color: #777;
            font-weight: bold;
            float: right;
            margin-left: 5px;
        }
        .active:after { 
            content: "\2212";  /* Unicode 字符 - 号 */
        }
    </style>

</head>

<body>

    <#include "./template/toplink.ftl"/>

    <div class="uk-section section-sub-nav uk-padding-remove">
        <div class="uk-container">
            <div uk-grid>
                <div class="uk-width-2-3@m">
                    <ul id="supportArticleBreadcrumb" class="uk-breadcrumb uk-visible@m">
                        <li><a href="/helpcenter/${kbase.uid!''}">首页</a></li>
                        <#--  <li><a href="category/${article.categories[0].uid}.html">${article.categories[0].name}</a></li>  -->
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
        <div class="uk-container">

            <div class="uk-grid-medium" uk-grid>
                <div class="uk-width-1-4@m text-dark sidebar">
                    <h3>类别</h3>
                    <ul id="supportCategory" class="uk-list uk-list-large uk-margin-medium-bottom">
                        <#list categories as category>
                            <#--  <button class="accordion">  -->
                                <#--  ${category.name!''}  -->
                                <li>
                                <a href="/helpcenter/${kbase.uid!''}/category/${category.uid}.html" target="_blank">${category.name!''}</a>
                                </li>
                            <#--  </button>  -->
                            <#--  <div class="panel">  -->
                                <#--  <#list articles as article>
                                    <li><a href="article/${article.uid}.html" target="_blank">${article.title!''}</a></li>
                                </#list>
                                <#if (articles?size == 0)>
                                    <div>
                                        暂无文章
                                    </div>
                                </#if>  -->
                            <#--  </div>  -->
                        </#list>
                        <!-- <li><a href="#">Getting Started</a></li> -->
                        <!-- <li><a class="uk-text-bold" href="#">Account Management</a> <span uk-icon="icon: chevron-right"></span></li> -->
                    </ul>
                </div>

                <div class="uk-width-3-4@m uk-flex-last@m">
                    <article class="uk-article">
                        <!-- 文章作者 -->
                        <header>
                            <h1 id="supportArticleTitle" class="uk-article-title uk-margin-bottom">${article.title!''}</h1>
                            <div class="author-box uk-card">
                                <div class="uk-card-header uk-padding-remove">
                                    <div class="uk-grid-small uk-flex-middle  uk-position-relative" uk-grid>
                                        <div class="uk-width-auto">
                                            <img id="supportArticleUserAvatar" class="uk-border-circle" width="40" height="40"
                                                 src="${(article.user.avatar)!((kbase.logoUrl)!('/favicon.ico'))}">
                                        </div>
                                        <div class="uk-width-expand">
                                            <#--  <h5 id="supportArticleUserNickname" class="uk-card-title">${article.user.nickname!''}</h5>  -->
                                            <#--  <p id="supportArticleUpdatedAt" class="uk-article-meta uk-margin-remove-top">${(article.createdAt)?string("yyyy-MM-dd HH:mm:ss")}</p>  -->
                                            <p id="supportArticleUpdatedAt" class="uk-article-meta uk-margin-remove-top">${article.createdAt}</p>
                                        </div>
                                        <!-- <a href="#" class="uk-button uk-button-outline-primary uk-button-small uk-visible@m">关注</a> -->
                                        <#--  <a id="supportArticleReadCount" href="#" class="uk-button uk-button-outline-primary uk-button-small uk-visible@m">阅读次数：${article.readCount}</a>  -->
                                    </div>
                                </div>
                            </div>
                        </header>

                        <!-- 文章内容 -->
                        <div class="entry-content uk-margin-medium-top">
                            <p id="supportArticleSummary" class="uk-text-lead">${article.summary!''}</p>
                            <div id="supportArticleContent">${article.contentHtml!''}</div>
                            <span class="uk-article-meta uk-margin-remove-top">最近更新：${article.updatedAt}</span>
                        </div>

                        <!-- 评价 -->
                        <#--  <div class="article-votes uk-text-center uk-margin-medium-top uk-padding uk-padding-remove-horizontal border-bottom border-top">
                            <h3>是否对您有帮助?</h3>
                            <a id="supportArticleRateHelpfull" href="#" class="vote uk-button uk-button-outline-primary" onclick="rateHelpfull()"><span class="uk-margin-small-right" uk-icon="icon: check; ratio: 0.8"></span>是的</a>
                            <a id="supportArticleRateHelpless" href="#" class="vote uk-button uk-button-outline-primary uk-margin-small-left" onclick="rateHelpless()"><span class="uk-margin-small-right uk-inline" uk-icon="icon: close; ratio: 0.8"></span>没有</a>
                            <p id="supportArticleRateThanks" class="vote-thanks uk-margin-remove">O(∩_∩)O谢谢反馈!</p>
                            <p class="vote-question text-dark">有更多问题? <a href="#">提交工单</a></p>
                        </div>  -->

                        <!-- 相关 -->
                        <div class="uk-child-width-1-2@s text-dark article-related uk-margin-medium-top" uk-grid>
                            <div>
                                <h3>最近阅读</h3>
                                <ul id="supportArticleRecent" class="uk-list uk-list-large">
                                    <!-- <li><a href="#">Sed ut perspiciatis unde omnis iste laudantium, totam rem aperiam</a></li> -->
                                    暂无文章
                                </ul>
                            </div>
                            <div>
                                <h3>相关文章</h3>
                                <ul id="supportArticleRelated" class="uk-list uk-list-large">
                                    <!-- <li><a href="#">Apsum dolor sit amet, consectet tempor inuididunt ut dolore magna aliqua</a></li> -->
                                    <#list related as article>
                                        <li><a href="/helpcenter/${kbase.uid!''}/article/${article.uid}.html" target="_blank">${article.title!''}</a><span style="float: right;">${article.updatedAt?substring(5, 16)}</span></li>
                                    </#list>
                                    <#if (related?size == 0)>
                                        <div>
                                            暂无文章
                                        </div>
                                    </#if>
                                </ul>
                            </div>
                        </div>

                    </article>
                </div>
            </div>

        </div>
    </div>

    <#include "./template/footer.ftl"/>

    <#include "./template/offcanvas.ftl"/>

    <#include "./template/bytedesk.ftl"/>

    <#include "./template/img_enlarge.ftl"/>

    <script>
        function rateArticle (rate) {
            <#--  $.ajax({
                url: "${host}/visitor/api/article/rate" +
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    uid: '${article.uid}',
                    rate: rate,
                    client: 'web'
                }),
                success:function(response){
                    console.log("rate article success:", response.data);
                    if (response.status_code === 200) {

                    } else {
                        alert(response.message);
                    }
                },
                error: function(error) {
                    console.log(error);
                    alert("评价失败");
                }
            });  -->
        }
        // 评价文章有帮助
        function rateHelpfull() {
            console.log('rateHelpfull')
            rateArticle(true)
        }
        // 评价文章没有帮助
        function rateHelpless() {
            console.log('rateHelpless')
            rateArticle(false)
        }
    </script>
    <script>
        <#--  https://www.runoob.com/try/try.php?filename=tryhtml_js_accordion  -->
        var acc = document.getElementsByClassName("accordion");
        var i;
        for (i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function() {
                this.classList.toggle("active");
                var panel = this.nextElementSibling;
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null;
                } else {
                    panel.style.maxHeight = panel.scrollHeight + "px";
                } 
            });
        }
    </script>

</body>

</html>
