/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-18 16:13:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-02 15:20:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
// 假设这是一个纯JS文件，没有HTML文件
// 需要先在页面中引入jQuery库

// 封装ChatFloat相关逻辑
(function ($) {
    // 定义默认配置
    var defaults = {
        chatUrl: 'https://www.weiyuai.cn/chat?org=df_org_uid&t=1&sid=default_wg_uid&',
        buttonPosition: 'right',
        buttonBackgroundColor: 'blue',
        iframeWidth: 400,
        iframeHeight: 600,
        iframeMargins: { right: 20, bottom: 20, left: 20 },
        buttonMargins: { right: 20, bottom: 20, left: 20 },
        showButton: true,
        showIframe: true
    };

    // 插件核心功能
    function ChatFloat(element, options) {
        // 合并默认配置和用户配置
        var settings = $.extend({}, defaults, options);

        // 根据配置创建按钮和iframe
        var button = $('<button>').css({
            position: 'fixed',
            left: settings.buttonPosition === 'left' ? settings.buttonMargins.left + 'px' : '',
            right: settings.buttonPosition === 'right' ? settings.buttonMargins.right + 'px' : '',
            bottom: settings.buttonMargins.bottom + 'px',
            width: '50px',
            height: '50px',
            borderRadius: '50%',
            backgroundColor: settings.buttonBackgroundColor,
            color: 'white',
            border: 'none',
            cursor: 'pointer',
            boxShadow: '0px 0px 10px 0px rgba(0,0,0,0.5)',
            display: settings.showButton ? 'block' : 'none'
        }).on('click', function () {
            if (settings.showIframe) {
                // 在点击事件中动态加载iframe内容
                loadIframe();
                showFloatWindow();
            }
        }).html('<svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4384" width="26" height="26"><path d="M512 64c259.2 0 469.333333 200.576 469.333333 448s-210.133333 448-469.333333 448a484.48 484.48 0 0 1-232.725333-58.88l-116.394667 50.645333a42.666667 42.666667 0 0 1-58.517333-49.002666l29.76-125.013334C76.629333 703.402667 42.666667 611.477333 42.666667 512 42.666667 264.576 252.8 64 512 64z m0 64C287.488 128 106.666667 300.586667 106.666667 512c0 79.573333 25.557333 155.434667 72.554666 219.285333l5.525334 7.317334 18.709333 24.192-26.965333 113.237333 105.984-46.08 27.477333 15.018667C370.858667 878.229333 439.978667 896 512 896c224.512 0 405.333333-172.586667 405.333333-384S736.512 128 512 128z m-157.696 341.333333a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z m159.018667 0a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z m158.997333 0a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z" fill="#ffffff" p-id="4385"></></svg>');

        var iframeContainer = $('<div>').css({
            position: 'fixed',
            left: settings.buttonPosition === 'left' ? settings.iframeMargins.left + 'px' : '',
            right: settings.buttonPosition === 'right' ? settings.iframeMargins.right + 'px' : '',
            bottom: settings.iframeMargins.bottom + 'px',
            width: settings.iframeWidth + 'px',
            height: settings.iframeHeight + 'px',
            display: 'none', // 初始隐藏
            backgroundColor: 'white',
            zIndex: 1000,
            borderRadius: '2%',
            boxShadow: '5px 5px 10px 0px rgba(0,0,0,0.5)', // 添加阴影效果
        });


        var closeButton = $('<button>').text('×').css({
            position: 'absolute',
            right: '10px',
            top: '10px',
            padding: '5px',
            backgroundColor: 'gray',
            color: 'white',
            border: 'none',
            cursor: 'pointer',
            borderRadius: '10%'
        }).on('click', function () {
            hideFloatWindow();
        });

        // 将元素添加到页面中
        // 创建一个新的div元素
        // var element = $('<div></div>');
        // // 设置该元素的id为'chat-float-container'
        // element.attr('id', 'chat-float-container');
        // // 将新元素添加到body的末尾
        // $('body').append(element);
        // 
        $(element).append(button);
        $(element).append(
            iframeContainer
                // .append(iframe)
                .append(closeButton)
        );

        // 动态创建并加载iframe的函数
        function loadIframe() {
            var iframe = $('<iframe>').attr('src', settings.chatUrl).css({
                width: '100%',
                height: '100%',
                borderWidth: '2px',
                borderColor: '#ddd',
                borderStyle: 'solid',
                borderRadius: '2%'
            });
            iframeContainer.append(iframe)
        }

        // 显示或隐藏漂浮窗口的函数
        function showFloatWindow() {
            iframeContainer.show();
            button.hide(); // 如果需要同时隐藏按钮
        }

        function hideFloatWindow() {
            iframeContainer.hide();
            button.show(); // 如果需要同时显示按钮
        }
    }

    // 将ChatFloat插件添加到jQuery原型上，使其可以全局调用
    $.fn.ChatFloat = function (option) {
        return this.each(function () {
            var $this = $(this),
                data = $this.data('ChatFloat'),
                options = typeof option === 'object' && option;
            if (!data) {
                $this.data('ChatFloat', (data = new ChatFloat(this, options)));
            }
            if (typeof option === 'string') {
                data[option]();
            }
        });
    };

    // 使插件支持链式调用
    // 例如：$('#element').ChatFloat().anotherFunction();
    // 这里的anotherFunction可以是其他任何jQuery方法或插件方法

}(jQuery));


// 使用示例，在DOM准备好后初始化ChatFloat插件
// $(document).ready(function () {
//     // 假设有一个id为"bytedesk-float-chat"的元素用于放置ChatFloat组件
//     $('#bytedesk-float-chat').ChatFloat({
//         // 这里可以传入配置参数，例如：
//         chatUrl: 'http://localhost:9006/chat?t=1&sid=default_wg_uid&',
//         // buttonBackgroundColor: 'red',
//         // 等等...
//     });
// });