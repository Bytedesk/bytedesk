<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>所有分类 - 良师宝 - 教培点评网</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <#--  <#include "../common/template/shenzhen/header.ftl"/>  -->

        <link href="../../assets/favicon.ico" rel="icon" >
        <link href="../../assets/vendors/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="../../assets/css/footer.css" rel="stylesheet">
        <link href="../../assets/css/gotop.css" rel="stylesheet">
        <link href="../../assets/css/liangshibao.css" rel="stylesheet">
        <#--  登录  -->
        <link href="../../assets/css/login.css" rel="stylesheet">
        <link href="../../assets/css/search.css" rel="stylesheet">

    </head>
    <body>

        <!-- 顶部导航 -->
        <#include "../common/template/navbar.ftl"/>

        <#--  搜索框  -->
        <#include "../common/template/search.ftl"/>

        <#--  导航  -->
        <nav class="navbar navbar-inverse">
            <div class="container">
                <div class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                    <li class="active"><a href="/">首页</a></li>
                    <#--  <li><a href="/exam.html" target="_blank">考试</a></li>  -->
                    <li><a href="/article.html" target="_blank">资讯</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </nav>

        <#--  核心内容  -->
        <div class="container" style="min-height: 800px;">
        
            <div style="height: 100px; text-align: center;">
                404，未找到页面
            </div>

        </div>

        <!-- 底部页脚 -->
        <#--  <#include "../../common/template/shenzhen/footer.ftl"/>  -->
        <footer class="footer">
            <div class="container">
                <p class="text-muted">让未来更美好 - 深圳良师教育科技有限公司</p>
            </div>
        </footer>
        
        <script src="../../assets/js/jquery.min.js"></script>
        <script src="../../assets/vendors/bootstrap/js/bootstrap.min.js"></script>
        <script src="../../assets/js/gotop.js"></script>
        <#--  <script src="../../assets/vendors/owlcarousel/owl.carousel.js"></script>  -->
        <#--  登录  -->
        <script src="../../assets/js/login.js"></script>

        <!-- 返回顶部 -->
        <#--  <#include "../../common/template/gotop.ftl"/>  -->
        <div class="go-top dn" id="go-top">
            <a href="javascript:;" class="uc-2vm"></a>
            <div class="uc-2vm-pop dn">
                <h2 class="title-2wm">微信咨询</h2>
                <div class="logo-2wm-box">
                    <img src="../../assets/img/luobosi_mp.png" width="240" height="240">
                </div>
            </div>
            <a href="https://vip.kefux.com/chat/pc/iframe.html?sub=vip&uid=201808221551193&wid=&type=appointed&history=0&aid=201808221551193&lang=cn&column=2&p"
                target="_blank" class="feedback"></a>
            <a href="javascript:;" class="go"></a>
        </div>

        <!--萝卜丝·智能客服代码 -->
        <#include "../common/template/bytedesk.ftl"/>

        <#--  网站统计代码  -->
        <#include "../common/template/track.ftl"/>

        
    </body>
</html>