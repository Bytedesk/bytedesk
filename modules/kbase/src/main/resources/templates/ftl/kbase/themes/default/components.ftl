
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Components - Knowledge Base HTML Template</title>

    <#include "./template/header.ftl"/>

</head>

<body>

    <nav class="uk-navbar-container uk-margin uk-navbar-transparent uk-background-primary uk-light uk-margin-remove-bottom">
        <div class="uk-container">
            <div uk-navbar>
                <div class="uk-navbar-left">
                    <a class="uk-navbar-item uk-logo uk-text-uppercase" href="index"><span class="uk-margin-small-right" uk-icon="icon: lifesaver"></span> Knowledge</a>
                </div>
                <div class="uk-navbar-right">
                    <ul class="uk-navbar-nav uk-text-uppercase uk-visible@m uk-margin-medium-left">
                        <li><a href="index">Home</a></li>
                        <li>
                            <a href="article">Article</a>
                            <div class="uk-navbar-dropdown">
                                <ul class="uk-nav uk-navbar-dropdown-nav">
                                    <li><a href="article">Scrollspy</a></li>
                                    <li><a href="article-narrow">Narrow</a></li>
                                </ul>
                            </div>
                        </li>
                        <li><a href="faq">Faq</a></li>
                        <li><a href="contact">Contact</a></li>
                        <li><a href="components">Components</a></li>
                    </ul>
                    <a class="uk-navbar-toggle uk-hidden@m" href="#offcanvas" uk-navbar-toggle-icon uk-toggle></a>
                </div>
            </div>
        </div>
    </nav>

    <div class="uk-section section-sub-nav uk-padding-remove">
        <div class="uk-container">
            <div uk-grid>
                <div class="uk-width-2-3@m">
                    <ul class="uk-breadcrumb uk-visible@m">
                        <li><a href="index">Home</a></li>
                        <li><a href="category">Administration</a></li>
                        <li><span href="">How to setup payment gateways</span></li>
                    </ul>
                </div>
                <div class="uk-width-1-3@m">
                    <div class="uk-margin">
                        <form class="uk-search uk-search-default">
                            <a href="javascript:void(0)" class="uk-search-icon-flip" uk-search-icon onclick="utils.searchContent()"></a>
                            <input id="supportSearch" class="uk-search-input" type="search" autocomplete="off" placeholder="Search">
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
                <header>
                    <h1 class="uk-article-title uk-margin-bottom">Components</h1>
                </header>
                <div class="entry-content uk-margin-medium-top">
                    <h2 class="uk-margin-medium-bottom">
                        Alerts
                    </h2>
                    <div uk-alert>
                        <a class="uk-alert-close" uk-close></a>
                        <h3>Notice</h3>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
                    </div>
                    <div class="uk-alert-primary" uk-alert>
                        <a class="uk-alert-close" uk-close></a>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.</p>
                    </div>

                    <div class="uk-alert-success" uk-alert>
                        <a class="uk-alert-close" uk-close></a>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.</p>
                    </div>

                    <div class="uk-alert-warning" uk-alert>
                        <a class="uk-alert-close" uk-close></a>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.</p>
                    </div>

                    <div class="uk-alert-danger" uk-alert>
                        <a class="uk-alert-close" uk-close></a>
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.</p>
                    </div>
                    <h2 class="uk-margin-medium-bottom">
                        Label
                    </h2>
                    <span class="uk-label">Default</span>
                    <span class="uk-label uk-label-success">Success</span>
                    <span class="uk-label uk-label-warning">Warning</span>
                    <span class="uk-label uk-label-danger">Danger</span>
                    <h2 class="uk-margin-medium-bottom">
                        Badge
                    </h2>
                    <span class="uk-badge">1</span>
                    <span class="uk-badge">100</span>
                    <span class="uk-badge">Lorem</span>
                    <h2 class="uk-margin-medium-bottom">
                        Table
                    </h2>
                    <table class="uk-table uk-table-hover">
                        <thead>
                            <tr>
                                <th>Table Heading</th>
                                <th>Table Heading</th>
                                <th>Table Heading</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                        </tbody>
                    </table>
                    <table class="uk-table uk-table-striped">
                        <thead>
                            <tr>
                                <th>Table Heading</th>
                                <th>Table Heading</th>
                                <th>Table Heading</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                            <tr>
                                <td>Table Data</td>
                                <td>Table Data</td>
                                <td>Table Data</td>
                            </tr>
                        </tbody>
                    </table>
                    <h2 class="uk-margin-medium-bottom">
                        Tabset
                    </h2>
                    <ul uk-tab>
                        <li class="uk-active"><a href="#">Left</a></li>
                        <li><a href="#">Item</a></li>
                        <li><a href="#">Item</a></li>
                    </ul>
                    <h2 class="uk-margin-medium-bottom">
                        Tooltip
                    </h2>
                    <p uk-margin>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip>Top</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: top-left">Top Left</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: top-right">Top Right</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: bottom">Bottom</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: bottom-left">Bottom Left</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: bottom-right">Bottom Right</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: left">Left</button>
                        <button class="uk-button uk-button-default" title="Hello World" uk-tooltip="pos: right">Right</button>
                    </p>
                    <h2 class="uk-margin-medium-bottom">
                        Button
                    </h2>
                    <p uk-margin>
                        <button class="uk-button uk-button-small uk-button-default">Default</button>
                        <button class="uk-button uk-button-small uk-button-primary">Primary</button>
                        <button class="uk-button uk-button-small uk-button-secondary">Secondary</button>
                        <button class="uk-button uk-button-small uk-button-danger">Danger</button>
                    </p>
                    <p uk-margin>
                        <button class="uk-button uk-button-default">Default</button>
                        <button class="uk-button uk-button-primary">Primary</button>
                        <button class="uk-button uk-button-secondary">Secondary</button>
                        <button class="uk-button uk-button-danger">Danger</button>
                    </p>
                    <p uk-margin>
                        <button class="uk-button uk-button-large uk-button-default">Default</button>
                        <button class="uk-button uk-button-large uk-button-primary">Primary</button>
                        <button class="uk-button uk-button-large uk-button-secondary">Secondary</button>
                        <button class="uk-button uk-button-large uk-button-danger">Danger</button>
                    </p>
                    <h2 class="uk-margin-medium-bottom">
                        Accordion
                    </h2>
                    <ul uk-accordion>
                        <li class="uk-open">
                            <h3 class="uk-accordion-title">Item 1</h3>
                            <div class="uk-accordion-content">
                                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>
                            </div>
                        </li>
                        <li>
                            <h3 class="uk-accordion-title">Item 2</h3>
                            <div class="uk-accordion-content">
                                <p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor reprehenderit.</p>
                            </div>
                        </li>
                        <li>
                            <h3 class="uk-accordion-title">Item 3</h3>
                            <div class="uk-accordion-content">
                                <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat proident.</p>
                            </div>
                        </li>
                    </ul>
                    <h2 class="uk-margin-medium-bottom">
                        Icon
                    </h2>
                    <ul class="uk-grid-small uk-flex-middle" uk-grid>
                        <!-- App -->
                        <li><span uk-icon="icon: home"></span></li>
                        <li><span uk-icon="icon: sign-out"></span></li>
                        <li><span uk-icon="icon: sign-in"></span></li>
                        <li><span uk-icon="icon: user"></span></li>
                        <li><span uk-icon="icon: users"></span></li>
                        <li><span uk-icon="icon: lock"></span></li>
                        <li><span uk-icon="icon: unlock"></span></li>
                        <li><span uk-icon="icon: settings"></span></li>
                        <li><span uk-icon="icon: cog"></span></li>
                        <li><span uk-icon="icon: nut"></span></li>
                        <li><span uk-icon="icon: comment"></span></li>
                        <li><span uk-icon="icon: commenting"></span></li>
                        <li><span uk-icon="icon: comments"></span></li>
                        <li><span uk-icon="icon: hashtag"></span></li>
                        <li><span uk-icon="icon: tag"></span></li>
                        <li><span uk-icon="icon: cart"></span></li>
                        <li><span uk-icon="icon: credit-card"></span></li>
                        <li><span uk-icon="icon: mail"></span></li>
                        <li><span uk-icon="icon: search"></span></li>
                        <li><span uk-icon="icon: location"></span></li>
                        <li><span uk-icon="icon: bookmark"></span></li>
                        <li><span uk-icon="icon: code"></span></li>
                        <li><span uk-icon="icon: paint-bucket"></span></li>
                        <li><span uk-icon="icon: camera"></span></li>
                        <li><span uk-icon="icon: bell"></span></li>
                        <li><span uk-icon="icon: bolt"></span></li>
                        <li><span uk-icon="icon: star"></span></li>
                        <li><span uk-icon="icon: heart"></span></li>
                        <li><span uk-icon="icon: happy"></span></li>
                        <li><span uk-icon="icon: lifesaver"></span></li>
                        <li><span uk-icon="icon: rss"></span></li>
                        <li><span uk-icon="icon: social"></span></li>
                        <li><span uk-icon="icon: git-branch"></span></li>
                        <li><span uk-icon="icon: git-fork"></span></li>
                        <li><span uk-icon="icon: world"></span></li>
                        <li><span uk-icon="icon: calendar"></span></li>
                        <li><span uk-icon="icon: clock"></span></li>
                        <li><span uk-icon="icon: history"></span></li>
                        <li><span uk-icon="icon: future"></span></li>
                        <li><span uk-icon="icon: pencil"></span></li>
                        <li><span uk-icon="icon: link"></span></li>
                        <li><span uk-icon="icon: trash"></span></li>
                        <li><span uk-icon="icon: move"></span></li>
                        <li><span uk-icon="icon: question"></span></li>
                        <li><span uk-icon="icon: info"></span></li>
                        <li><span uk-icon="icon: warning"></span></li>
                        <li><span uk-icon="icon: image"></span></li>
                        <li><span uk-icon="icon: thumbnails"></span></li>
                        <li><span uk-icon="icon: table"></span></li>
                        <li><span uk-icon="icon: list"></span></li>
                        <li><span uk-icon="icon: menu"></span></li>
                        <li><span uk-icon="icon: grid"></span></li>
                        <li><span uk-icon="icon: more"></span></li>
                        <li><span uk-icon="icon: more-vertical"></span></li>
                        <li><span uk-icon="icon: plus"></span></li>
                        <li><span uk-icon="icon: plus-circle"></span></li>
                        <li><span uk-icon="icon: minus"></span></li>
                        <li><span uk-icon="icon: minus-circle"></span></li>
                        <li><span uk-icon="icon: close"></span></li>
                        <li><span uk-icon="icon: check"></span></li>
                        <li><span uk-icon="icon: ban"></span></li>
                        <li><span uk-icon="icon: refresh"></span></li>
                        <li><span uk-icon="icon: play"></span></li>
                        <li><span uk-icon="icon: play-circle"></span></li>
                        <!-- Devices -->
                        <li><span uk-icon="icon: tv"></span></li>
                        <li><span uk-icon="icon: desktop"></span></li>
                        <li><span uk-icon="icon: laptop"></span></li>
                        <li><span uk-icon="icon: tablet"></span></li>
                        <li><span uk-icon="icon: phone"></span></li>
                        <li><span uk-icon="icon: tablet-landscape"></span></li>
                        <li><span uk-icon="icon: phone-landscape"></span></li>
                        <!-- Storage -->
                        <li><span uk-icon="icon: file"></span></li>
                        <li><span uk-icon="icon: copy"></span></li>
                        <li><span uk-icon="icon: file-edit"></span></li>
                        <li><span uk-icon="icon: folder"></span></li>
                        <li><span uk-icon="icon: album"></span></li>
                        <li><span uk-icon="icon: push"></span></li>
                        <li><span uk-icon="icon: pull"></span></li>
                        <li><span uk-icon="icon: server"></span></li>
                        <li><span uk-icon="icon: database"></span></li>
                        <li><span uk-icon="icon: cloud-upload"></span></li>
                        <li><span uk-icon="icon: cloud-download"></span></li>
                        <li><span uk-icon="icon: download"></span></li>
                        <li><span uk-icon="icon: upload"></span></li>
                        <!-- Direction -->
                        <li><span uk-icon="icon: reply"></span></li>
                        <li><span uk-icon="icon: forward"></span></li>
                        <li><span uk-icon="icon: expand"></span></li>
                        <li><span uk-icon="icon: shrink"></span></li>
                        <li><span uk-icon="icon: arrow-up"></span></li>
                        <li><span uk-icon="icon: arrow-down"></span></li>
                        <li><span uk-icon="icon: arrow-left"></span></li>
                        <li><span uk-icon="icon: arrow-right"></span></li>
                        <li><span uk-icon="icon: chevron-up"></span></li>
                        <li><span uk-icon="icon: chevron-down"></span></li>
                        <li><span uk-icon="icon: chevron-left"></span></li>
                        <li><span uk-icon="icon: chevron-right"></span></li>
                        <li><span uk-icon="icon: triangle-up"></span></li>
                        <li><span uk-icon="icon: triangle-down"></span></li>
                        <li><span uk-icon="icon: triangle-left"></span></li>
                        <li><span uk-icon="icon: triangle-right"></span></li>
                        <!-- Editor -->
                        <li><span uk-icon="icon: bold"></span></li>
                        <li><span uk-icon="icon: italic"></span></li>
                        <li><span uk-icon="icon: strikethrough"></span></li>
                        <li><span uk-icon="icon: video-camera"></span></li>
                        <li><span uk-icon="icon: quote-right"></span></li>
                        <!-- Brands -->
                        <li><span uk-icon="icon: behance"></span></li>
                        <li><span uk-icon="icon: dribbble"></span></li>
                        <li><span uk-icon="icon: facebook"></span></li>
                        <li><span uk-icon="icon: github-alt"></span></li>
                        <li><span uk-icon="icon: github"></span></li>
                        <li><span uk-icon="icon: foursquare"></span></li>
                        <li><span uk-icon="icon: tumblr"></span></li>
                        <li><span uk-icon="icon: whatsapp"></span></li>
                        <li><span uk-icon="icon: soundcloud"></span></li>
                        <li><span uk-icon="icon: flickr"></span></li>
                        <li><span uk-icon="icon: google-plus"></span></li>
                        <li><span uk-icon="icon: google"></span></li>
                        <li><span uk-icon="icon: linkedin"></span></li>
                        <li><span uk-icon="icon: vimeo"></span></li>
                        <li><span uk-icon="icon: instagram"></span></li>
                        <li><span uk-icon="icon: joomla"></span></li>
                        <li><span uk-icon="icon: pagekit"></span></li>
                        <li><span uk-icon="icon: pinterest"></span></li>
                        <li><span uk-icon="icon: twitter"></span></li>
                        <li><span uk-icon="icon: uikit"></span></li>
                        <li><span uk-icon="icon: wordpress"></span></li>
                        <li><span uk-icon="icon: xing"></span></li>
                        <li><span uk-icon="icon: youtube"></span></li>
                    </ul>
                    <h2 class="uk-margin-medium-bottom">Typography</h2>
                    <p class="uk-text-lead">This is a lead text. Aute irure dolor in reprehenderit occaecat cupidatat.</p>
                    <h1>
                        This Is An H1 Tag
                    </h1>
                    <h2>
                        This Is An H2 Tag
                    </h2>
                    <h3>
                        This Is An H3 Tag
                    </h3>
                    <h4>
                        This Is An H4 Tag
                    </h4>
                    <h5>
                        This Is An H5 Tag
                    </h5>
                </div>
            </article>
        </div>
    </div>

    <footer id="footer" class="uk-section uk-margin-large-top uk-section-xsmall uk-text-small uk-text-muted border-top">
        <div class="uk-container">
            <div class="uk-child-width-1-2@m uk-text-center" uk-grid>
                <div class="uk-text-right@m">
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: facebook"></a>
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: google"></a>
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: vimeo"></a>
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: instagram"></a>
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: twitter"></a>
                    <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: youtube"></a>
                </div>
                <div class="uk-flex-first@m uk-text-left@m">
                    <p class="uk-text-small">Copyright 2017 Powered by Code Love</p>
                </div>
            </div>
        </div>
    </footer>

    <div id="offcanvas" uk-offcanvas="flip: true; overlay: true">
        <div class="uk-offcanvas-bar">
            <a class="uk-margin-small-bottom uk-logo uk-text-uppercase" href="index"><span class="uk-margin-small-right" uk-icon="icon: lifesaver"></span> Knowledge</a>
            <ul class="uk-nav uk-nav-default uk-text-uppercase">
                <li><a href="index">Home</a></li>
                <li class="uk-parent">
                    <a href="article">Article</a>
                    <ul class="uk-nav-sub">
                        <li><a href="article">Scrollspy</a></li>
                        <li><a href="article-narrow">Narrow</a></li>
                    </ul>
                </li>
                <li><a href="faq">Faq</a></li>
                <li><a href="contact">Contact</a></li>
                <li><a href="components">Components</a></li>
            </ul>
            <a href="contact" class="uk-button uk-button-small uk-button-default uk-width-1-1 uk-margin">Support</a>
            <div class="uk-width-auto uk-text-center">
                <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: facebook"></a>
                <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: google"></a>
                <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: twitter"></a>
            </div>
        </div>
    </div>

</body>

</html>
