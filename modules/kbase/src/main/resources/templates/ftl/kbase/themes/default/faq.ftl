
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>常见问题 - ${kbase.headline!'帮助中心'} - ${kbase.name!'微语'}</title>

    <#include "./template/header.ftl"/>
    
</head>

<body>

    <#include "./template/toplink.ftl"/>

    <div class="uk-section section-sub-nav uk-padding-remove">
        <div class="uk-container">
            <div uk-grid>
                <div class="uk-width-2-3@m">
                    <ul class="uk-breadcrumb uk-visible@m">
                        <li><a href="index">Home</a></li>
                        <li><span href="">Frequently Asked Questions</span></li>
                    </ul>
                </div>
                <div class="uk-width-1-3@m">
                    <div class="uk-margin">
                        <form class="uk-search uk-search-default">
                            <a href="javascript:void(0)" class="uk-search-icon-flip" uk-search-icon onclick="utils.searchContent()"></a>
                            <input id="supportAdminUid" name="uid" type="text" style="display:none"/>
                            <input id="supportSearch" class="uk-search-input" type="search" 
                                autocomplete="off" placeholder="搜索" onkeyup="utils.onKeyUp(arguments[0] || window.event)">
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
                <div class="uk-width-1-4@m uk-visible@m text-dark sidebar">
                    <div uk-sticky="offset: 50">
                        <h3>Table of Contents</h3>
                        <ul class="uk-list uk-list-large">
                            <li><a href="#target1" uk-scroll="offset: 50">General Options</a></li>
                            <li><a href="#target2" uk-scroll="offset: 50">User Account</a></li>
                            <li><a href="#target3" uk-scroll="offset: 50">Shipping Methods</a></li>
                        </ul>
                    </div>
                </div>
                <div class="uk-width-3-4@m">
                    <h1>Frequently Asked Questions</h1>
                    <p class="uk-text-lead uk-margin-large-bottom">Here are answers to most common questions. Can't find an answer? Call us!</p>

                    <h2 id="target1">General Options</h2>
                    <ul class="list-faq" uk-accordion="multiple: true">
                        <li>
                            <h3 class="uk-accordion-title uk-margin-remove">Transfer account ownership</h3>
                            <div class="uk-accordion-content">
                                <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure eprehenderit in voluptate velit esse cillum dolore dolor reprehenderit.</p>
                            </div>
                        </li>
                        <li>
                            <h3 class="uk-accordion-title uk-margin-remove">Shipping options page settings</h3>
                            <div class="uk-accordion-content">
                                <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eeprehenderit in voluptate velit esse cillum doloreu fugiat nulla pariatur. Excepteur sint occaecat cupidatat proident.</p>
                            </div>
                        </li>
                    </ul>

                    <h2 id="target2">User Account</h2>
                    <ul class="list-faq" uk-accordion="multiple: true">
                        <li>
                            <h3 class="uk-accordion-title uk-margin-remove">Manage payment settings and invoices</h3>
                            <div class="uk-accordion-content">
                                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor eprehenderit in voluptate velit esse cillum dolore incididunt ut labore et dolore magna aliqua.</p>
                            </div>
                        </li>
                        <li>
                            <h3 class="uk-accordion-title uk-margin-remove">Pricing and plans guide</h3>
                            <div class="uk-accordion-content">
                                <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure eprehenderit in voluptate velit esse cillum dolore dolor reprehenderit.</p>
                            </div>
                        </li>
                    </ul>

                </div>
            </div>
        </div>
    </div>

    <#include "./template/footer.ftl"/>

    <#include "./template/offcanvas.ftl"/>

</body>

</html>
