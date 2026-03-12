// 假设这是一个纯JS文件，没有HTML文件

// 封装ChatFloat相关逻辑
(function () {
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
    function ChatFloat(options) {
        // 获取元素
        // var element = document.getElementById(elementId);
        // document.createElement("bytedesk-float-chat"); //
        var element = document.createElement('div');
        element.id = 'bytedesk-float-chat';
        // 将新的div添加到HTML文档的body中
        document.body.appendChild(element);

        // 合并默认配置和用户配置
        var settings = Object.assign({}, defaults, options);

        // 根据配置创建按钮
        var button = document.createElement('button');
        button.style.position = 'fixed';
        button.style.left = settings.buttonPosition === 'left' ? settings.buttonMargins.left + 'px' : '';
        button.style.right = settings.buttonPosition === 'right' ? settings.buttonMargins.right + 'px' : '';
        button.style.bottom = settings.buttonMargins.bottom + 'px';
        button.style.width = '50px';
        button.style.height = '50px';
        button.style.borderRadius = '50%';
        button.style.backgroundColor = settings.buttonBackgroundColor;
        button.style.color = 'white';
        button.style.border = 'none';
        button.style.cursor = 'pointer';
        button.style.boxShadow = '0px 0px 10px 0px rgba(0,0,0,0.5)';
        button.style.display = settings.showButton ? 'block' : 'none';
        button.innerHTML = '<svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4384" width="26" height="26"><path d="M512 64c259.2 0 469.333333 200.576 469.333333 448s-210.133333 448-469.333333 448a484.48 484.48 0 0 1-232.725333-58.88l-116.394667 50.645333a42.666667 42.666667 0 0 1-58.517333-49.002666l29.76-125.013334C76.629333 703.402667 42.666667 611.477333 42.666667 512 42.666667 264.576 252.8 64 512 64z m0 64C287.488 128 106.666667 300.586667 106.666667 512c0 79.573333 25.557333 155.434667 72.554666 219.285333l5.525334 7.317334 18.709333 24.192-26.965333 113.237333 105.984-46.08 27.477333 15.018667C370.858667 878.229333 439.978667 896 512 896c224.512 0 405.333333-172.586667 405.333333-384S736.512 128 512 128z m-157.696 341.333333a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z m159.018667 0a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z m158.997333 0a42.666667 42.666667 0 1 1 0 85.333334 42.666667 42.666667 0 0 1 0-85.333334z" fill="#ffffff" p-id="4385"></></svg>';

        button.addEventListener('click', function () {
            if (settings.showIframe) {
                // 在点击事件中动态加载iframe内容
                loadIframe();
                showFloatWindow();
            }
        });

        // 创建iframe容器
        var iframeContainer = document.createElement('div');
        iframeContainer.style.position = 'fixed';
        iframeContainer.style.left = settings.buttonPosition === 'left' ? settings.iframeMargins.left + 'px' : '';
        iframeContainer.style.right = settings.buttonPosition === 'right' ? settings.iframeMargins.right + 'px' : '';
        iframeContainer.style.bottom = settings.iframeMargins.bottom + 'px';
        iframeContainer.style.width = settings.iframeWidth + 'px';
        iframeContainer.style.height = settings.iframeHeight + 'px';
        iframeContainer.style.display = 'none'; // 初始隐藏
        iframeContainer.style.backgroundColor = 'white';
        iframeContainer.style.zIndex = 1000;
        iframeContainer.style.borderRadius = '2%';
        iframeContainer.style.boxShadow = '5px 5px 10px 0px rgba(0,0,0,0.5)';

        // 创建iframe
        // var iframe = document.createElement('iframe');
        // iframe.src = settings.chatUrl;
        // iframe.style.width = '100%';
        // iframe.style.height = '100%';
        // iframe.style.borderWidth = '2px';
        // iframe.style.borderColor = '#ddd';
        // iframe.style.borderStyle = 'solid';
        // iframe.style.borderRadius = '2%';

        // 创建关闭按钮
        var closeButton = document.createElement('button');
        closeButton.innerText = '×';
        closeButton.style.position = 'absolute';
        closeButton.style.right = '10px';
        closeButton.style.top = '10px';
        closeButton.style.padding = '5px';
        closeButton.style.backgroundColor = 'gray';
        closeButton.style.color = 'white';
        closeButton.style.border = 'none';
        closeButton.style.cursor = 'pointer';
        closeButton.style.borderRadius = '10%';

        closeButton.addEventListener('click', function () {
            hideFloatWindow();
        });

        // 将元素添加到页面中
        element.appendChild(button);
        var iframeContainerElement = element.appendChild(iframeContainer);
        // iframeContainerElement.appendChild(iframe);
        iframeContainerElement.appendChild(closeButton);

        // 动态创建并加载iframe的函数
        function loadIframe() {
            var iframe = document.createElement('iframe');
            iframe.src = settings.chatUrl;
            iframe.style.width = '100%';
            iframe.style.height = '100%';
            iframe.style.borderWidth = '2px';
            iframe.style.borderColor = '#ddd';
            iframe.style.borderStyle = 'solid';
            iframe.style.borderRadius = '2%';
            iframeContainerElement.appendChild(iframe);
        }

        // 显示或隐藏漂浮窗口的函数
        function showFloatWindow() {
            iframeContainer.style.display = 'block';
            button.style.display = 'none'; // 如果需要同时隐藏按钮
        }

        function hideFloatWindow() {
            iframeContainer.style.display = 'none';
            button.style.display = 'block'; // 如果需要同时显示按钮
        }
    }

    // 初始化ChatFloat插件
    window.ChatFloat = function (elementId, option) {
        return new ChatFloat(elementId, option);
    };

})();
