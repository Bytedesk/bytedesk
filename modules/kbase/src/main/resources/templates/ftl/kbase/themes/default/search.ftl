
<!DOCTYPE html>
<html lang="en-gb" dir="ltr">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>搜索 - ${kbase.headline!'帮助中心'} - ${kbase.name!'微语'}</title>

    <#include "./template/header.ftl"/>

</head>

<body>

    <#include "./template/toplink.ftl"/>

    <div class="uk-section uk-padding-remove-top uk-padding-remove-bottom">
        <div class="uk-container">
            <hr>
        </div>
    </div>

    <div class="uk-section">
        <div class="uk-container">
            <div class="uk-child-width-1-2@s text-dark" uk-grid>
                <div>
                    <h3>搜索结果</h3>
                    <ul id="supportSearchArticle" class="uk-list uk-list-large uk-list-divider link-icon-right">
                        <!-- <li><a href="article">Voluptatem accusantium doloremque laudan rem aperiam</a></li> -->
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <#--  <div class="uk-section uk-padding-remove section-cta uk-background-blend-lighten uk-background-center-center uk-background-cover uk-text-center" style="background-image: url(/img/cafe.jpg)" >
        <div class="uk-background-muted1 uk-border-rounded1 uk-padding-large">
            <h2>找不到您要的答案?</h2>
            <p class="uk-margin-medium-top">
                <a href="contact" class="uk-button uk-button-primary uk-button-large">在线客服</a>
            </p>
        </div>
    </div>  -->

    <#include "./template/footer.ftl"/>

    <#include "./template/offcanvas.ftl"/>

    <#include "./template/bytedesk.ftl"/>

    <script>
        function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg); //匹配目标参数
            if (r != null) return decodeURIComponent(r[2]);
            return null; //返回参数值
        }
        function searchArticle () {
            var kbUid = getUrlParam("kbUid")
            var content = getUrlParam("content")
            console.log("kbUid:", kbUid);
            console.log("content:", content);
            $.ajax({
                url: "${apiHost}/visitor/api/v1/article/search",
                type: "get",
                data: {
                    kbUid: kbUid,
                    title: content,
                    content: content,
                    categoryUid: '',
                    pageNumber: 0,
                    pageSize: 20,
                    client: 'web'
                },
                success:function(response){
                    console.log("search article success:", response);
                    if (response.code === 200) {
                        if (response.data.content.length == 0) {
                            // 搜索结果为空
                            $("#supportSearchArticle").append("未找到结果");
                        } else {
                            // 搜索结果不为空
                            for (var i = 0; i < response.data.content.length; i++) {
                                var article = response.data.content[i];
                                $("#supportSearchArticle").append("<li><a href='/helpcenter/${kbase.uid!''}/article/" + article.uid + ".html' target='_blank'>" + article.title + "</a></li>");
                            }
                        }
                    } else {
                        alert(response.message);
                    }
                },
                error: function(error) {
                    console.log("search article detail error:", error);
                }
            });
        }
        searchArticle()
    </script>

</body>

</html>
