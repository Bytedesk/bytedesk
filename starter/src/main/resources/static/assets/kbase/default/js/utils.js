/**
 * 工具类
 */
var utils = {
    /**
     * 获取url参数值
     * @param {*} name 参数名
     */
    getUrlParam: function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); //匹配目标参数
        if (r != null) return decodeURIComponent(r[2]);
        return null; //返回参数值
    },
    //
    guid: function() {
        function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return (s4() + s4() + "-" + s4() + "-" + s4() + "-" + s4() + "-" + s4() + s4() + s4());
    },
    //
    searchContent: function() {
        var searchContent = $("#supportSearch").val()
        window.open("/support/search.html?uid=" + data.adminUid + "&content=" + searchContent);
    },
    //
    onKeyUp: function(event) {
        var key = event.keyCode || window.event.keyCode;
        // console.log("onKeyUp:", key);
        if (key === 13) {
            var searchContent = $("#supportSearch").val()
            window.open("/support/search.html?uid=" + data.adminUid + "&content=" + searchContent);
        }
    },
    // 评价文章有帮助
    rateHelpfull: function() {
        console.log('rateHelpfull')
        if (data.articleRate === false) {
            httpapi.rateArticle(data.aid, true)
        } else {
            alert('已经评价过，无需重复评价')
        }
    },
    // 评价文章没有帮助
    rateHelpless: function() {
        console.log('rateHelpless')
        if (data.articleRate === false) {
            httpapi.rateArticle(data.aid, false)
        } else {
            alert('已经评价过，无需重复评价')
        }
    }

};
