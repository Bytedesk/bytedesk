<#-- style="background-image: url(https://cdn.bytedesk.com/kb/assets/img/city.jpg)"  -->
<div class="section-hero uk-background-blend-color-burn uk-background-top-center uk-background-cover uk-section-large1 cta" style="background-image: url(/assets/kbase/default/img/city.jpg)">
    <nav class="uk-navbar-container uk-margin uk-navbar-transparent uk-light">
        <div class="uk-container">
            <div uk-navbar>
                <div class="uk-navbar-left">
                    <a id="supporHometTitle" class="uk-navbar-item uk-logo uk-text-uppercase" href="/">${kbase.name!'微语'}</a>
                </div>
                <div class="uk-navbar-right">
                    <ul class="uk-navbar-nav uk-text-uppercase uk-visible@m uk-margin-medium-left">
                        <#--  TODO: 后台自定义  -->
                        <#--  <li><a href="/index" target="_blank">首页</a></li>  -->
                        <#--  <li><a id="replaceForum" href="/forum" target="_blank">问答社区</a></li>  -->
                        <#--  <li><a id="replaceTicket" href="/ticket" target="_blank">创建工单</a></li>  -->
                        <#--  <li><a id="replaceFeedback" href="/feedback" target="_blank">意见反馈</a></li>  -->
                    </ul>
                    <a class="uk-navbar-toggle uk-hidden@m" href="#offcanvas" uk-navbar-toggle-icon uk-toggle></a>
                </div>
            </div>
        </div>
    </nav>
    <div class="uk-container hero">
        <h1 class="uk-heading-primary uk-text-center uk-margin-large-top uk-light">${kbase.headline!'帮助中心'}</h1>
        <!-- <p class="uk-text-lead uk-text-center uk-light">Lead volutpat nibh ligula gravida. Magna auctor eget venenatis phasellus luctus sodales pulvinar</p> -->
        <div class="uk-flex uk-flex-center">
            <form id="supportSearchForm" class="uk-margin-medium-top uk-margin-xlarge-bottom uk-search uk-search-default">
                <a href="javascript:void(0)" class="uk-search-icon-flip" uk-search-icon onclick="searchContent()"></a>
                <input id="supportAdminUid" name="uid" type="text" style="display:none"/>
                <input id="supportSearch" class="uk-search-input uk-form-large" type="search"
                    autocomplete="off" name="content" placeholder="请输入..."
                    onkeyup="onKeyUp(arguments[0] || window.event)">
            </form>
        </div>
    </div>
</div>
