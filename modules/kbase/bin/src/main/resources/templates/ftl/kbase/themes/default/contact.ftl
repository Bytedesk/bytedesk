
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>联系我们 - ${kbase.headline!'帮助中心'} - ${kbase.name!'微语'}</title>

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
                        <li><span>Contact Us</span></li>
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
        <div class="uk-container container-xs">
            <div>
                <header>
                    <h1 class="uk-margin-bottom">Contact Us</h1>
                </header>
                <div class="entry-content uk-margin-medium-top">
                    <p class="uk-text-lead">Duis aute irure dolor in reprehenderit in voluptate velit esse cillum officia.</p>
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing voluptate velit esse cillum dolore eu fugiat nullar sint proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
                </div>
                <h3 class="uk-margin-medium-bottom">Ways To get In Touch</h3>
                <div class="uk-child-width-1-2@s text-dark" uk-grid>
                    <div>
                        <ul class="uk-list">
                            <li><a href="mailto:contact@company.com"><span class="uk-margin-small-right" uk-icon="icon: mail"></span>contact@company.com</a></li>
                            <li><a href="#"><span class="uk-margin-small-right" uk-icon="icon: whatsapp"></span>07512 325 451</a></li>
                        </ul>
                    </div>
                    <div>
                        <ul class="uk-list">
                            <li><a href="#"><span class="uk-margin-small-right" uk-icon="icon: twitter"></span>xycompany</a></li>
                            <li><a href="#"><span class="uk-margin-small-right" uk-icon="icon: google-plus"></span>xycompany</a></li>
                        </ul>
                    </div>
                </div>
                <div class="uk-margin-medium-top border-top padding-top">
                    <h3 class="uk-margin-medium-bottom">Contact Support</h3>
                    <form class="uk-form-stacked">
                        <div class="uk-margin">
                            <label class="uk-form-label" for="form-stacked-text">Name</label>
                            <div class="uk-form-controls">
                                <input class="uk-input" id="form-stacked-text" type="text">
                            </div>
                        </div>
                        <div class="uk-margin">
                            <label class="uk-form-label" for="form-stacked-text">Email</label>
                            <div class="uk-form-controls">
                                <input class="uk-input" id="form-stacked-text" type="email">
                            </div>
                        </div>
                        <div class="uk-margin">
                            <label class="uk-form-label" for="form-stacked-select">Type</label>
                            <div class="uk-form-controls">
                                <select class="uk-select" id="form-stacked-select">
                                    <option>Pre Sale</option>
                                    <option>Payments</option>
                                </select>
                            </div>
                        </div>
                        <div class="uk-margin">
                            <label class="uk-form-label" for="form-stacked-text">Comment</label>
                            <div class="uk-form-controls">
                                <textarea class="uk-textarea" rows="5"></textarea>
                            </div>
                        </div>
                        <div class="uk-margin">
                            <div class="uk-form-controls">
                                <button class="uk-button uk-button-primary uk-width-1-1 uk-width-auto@s">Submit</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <#include "./template/footer.ftl"/>

    <#include "./template/offcanvas.ftl"/>

</body>

</html>
