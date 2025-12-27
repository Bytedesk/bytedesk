<#--  TODO: 支持右下角 嵌入客服icon  -->
<footer id="footer" class="uk-section uk-margin-large-top uk-section-xsmall uk-text-small uk-text-muted border-top">
    <div class="uk-container">
        <div class="uk-child-width-1-2@m uk-text-center" uk-grid>
            <div class="uk-text-right@m">
                <#--  <a href="#" class="uk-icon-link uk-margin-small-right" uk-icon="icon: twitter"></a>  -->
            </div>
            <div class="uk-flex-first@m uk-text-left@m">
                    <#--  TODO: 后台自定义  -->
                <#--  <p class="uk-text-small">北京微语天下科技有限公司, 京ICP备17041763号-2</p>  -->
                ${knowledgebase.footerHtml!''}
            </div>
        </div>
    </div>
</footer>

<script>
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); //匹配目标参数
        if (r != null) return decodeURIComponent(r[2]);
        return null; //返回参数值
    }
    //
    function searchContent() {
        // 读取变量，注意：添加双引号
        let kbUid = '${knowledgebase.uid!''}'
        let searchContent = $("#supportSearch").val()
        window.open("/helpcenter/${knowledgebase.uid!''}/search.html?kbUid=" + kbUid + "&content=" + searchContent);
    }
    //
    function onKeyUp(event) {
        var key = event.keyCode || window.event.keyCode;
        // console.log("onKeyUp:", key);
        if (key === 13) {
            // 读取变量，注意：添加双引号
            let kbUid = '${knowledgebase.uid!''}'
            let searchContent = $("#supportSearch").val()
             window.open("/helpcenter/${knowledgebase.uid!''}/search.html?kbUid=" + kbUid + "&content=" + searchContent);
        }
    }
</script>