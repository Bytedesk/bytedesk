/*  
    bytedesk.com
    2022 - v1.0.0
*/
class ByteDesk {

    constructor() {
        // 配置
        this.config = undefined
        // 启动会话按钮
        this.roundButton = undefined
        this.rectangleButton = undefined
        // 对话窗口
        this.chatWindow = undefined
        this.chatWindowDOM = undefined
        // 移动端对话窗口
        this.rectangleButtonMobile = undefined
        this.chatWindowMobile = undefined
        this.chatWindowDomMobile = undefined
        this.chatWindowMobileMask = undefined
        // 未读消息数目
        this.unreadCount = 0
        // 新消息气泡
        this.messageTips = undefined
        //
        this.firstStart = true
        this.firstOpen = true
        this.isChatWindowShown = false
        // 
        this.chatUrl = ''
        this.chatMaxUrl = ''
        // 预加载页面url
        this.chatUrlPreload = ''
        // 当前站点url
        this.websiteUrl = ''
        this.websiteTitle = ''
        // 来源站点url
        this.refererUrl = ''
        // 测试环境
        this.isProduction = false
        this.h5BaseUrl = 'http://127.0.0.1:8887/chat/h5/index.html'
        this.h5BaseUrlEn = 'http://127.0.0.1:8887/chat/h5/indexen.html'
        this.pcBaseUrl = 'http://127.0.0.1:8887/chat/pc/index.html'
        this.pcBaseUrlEn = 'http://127.0.0.1:8887/chat/pc/indexen.html'
        this.httpHost = "http://127.0.0.1:8000"
        // 线上环境
        // this.isProduction = true
        // this.h5BaseUrl = 'https://h5.kefux.com/chat/h5/index.html'
        // this.h5BaseUrlEn = 'https://h5.kefux.com/chat/h5/indexen.html'
        // this.pcBaseUrl = 'https://pc.kefux.com/chat/pc/index.html'
        // this.pcBaseUrlEn = 'https://pc.kefux.com/chat/pc/indexen.html'
        // this.httpHost = "https://pcapi.bytedesk.com"
        //
        this.type = ''
        this.client = "web_pc"
    }

    render() {
        this.addCss()
        this.createDOMElements()
        this.showIconButton()
        //
        this.firstOpen = true
    }

    addCss() {
        const stylesheet = document.createElement('link');
        stylesheet.setAttribute('rel', 'stylesheet');
        stylesheet.setAttribute('href', this.isProduction ? `https://cdn.kefux.com/chat/pc/css/custom.min.css` : `http://127.0.0.1:8887/chat/pc/css/custom.css`);
        document.head.appendChild(stylesheet);
    }

    createDOMElements() {
        // 遮罩
        this.chatWindowMobileMask = document.createElement("div");
        this.chatWindowMobileMask.classList.add("bytedesk_black_overlay");
        this.chatWindowMobileMask.setAttribute("id", "bytedesk_black_overlay");
        document.body.appendChild(this.chatWindowMobileMask);
        document.getElementById("bytedesk_black_overlay").addEventListener("click", () => {
            document.getElementById("bytedesk_black_overlay").style.display = "none";
            this.showIconButton()
        })
        //  pc长方形icon
        this.rectangleButton = document.createElement("div");
        this.rectangleButton.innerHTML = `<button type="button" id="bytedesk_rectangle_button" class="bytedesk_rectangle_button bytedesk_${this.config.position}_${this.config.margin} bytedesk_bottom_${this.config.bottomMargin} animation" style="color: ${this.config.iconButton.color}; background-color: ${this.config.iconButton.background};">
                                    <svg fill="currentColor" style="margin-right: 7px; margin-top: 2px; vertical-align: text-top;" height="16px" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                                        <path d="M506.496 939.136c85.76 0 168.64-22.464 240.512-65.024l187.712 54.656-43.136-193.792c41.28-64.384 62.912-137.344 62.912-212.032 0-229.44-201.024-416.128-448-416.128s-448 186.688-448 416.128C58.496 752.512 259.456 939.136 506.496 939.136L506.496 939.136 506.496 939.136zM748.672 831.168l-9.536 5.952c-68.672 43.008-149.184 65.664-232.64 65.664-228.224 0-413.888-170.368-413.888-379.84s185.664-379.84 413.888-379.84c228.224 0 413.888 170.368 413.888 379.84 0 70.528-21.632 139.712-62.592 200.064l-6.4 9.344 30.656 137.536L748.672 831.168 748.672 831.168 748.672 831.168zM739.904 819.904" p-id="6481"></path>
                                        <path d="M602.368 524.224c0 26.88 21.376 48.704 47.744 48.704s47.744-21.824 47.744-48.704c0-26.88-21.376-48.704-47.744-48.704S602.368 497.344 602.368 524.224L602.368 524.224 602.368 524.224zM602.368 524.224" p-id="6482"></path>
                                        <path d="M436.608 524.224c0 26.88 21.376 48.704 47.744 48.704 26.432 0 47.744-21.824 47.744-48.704 0-26.88-21.312-48.704-47.744-48.704C457.984 475.584 436.608 497.344 436.608 524.224L436.608 524.224 436.608 524.224zM436.608 524.224" p-id="6483"></path>
                                        <path d="M270.848 524.224c0 26.88 21.376 48.704 47.744 48.704 26.368 0 47.744-21.824 47.744-48.704 0-26.88-21.376-48.704-47.744-48.704C292.224 475.584 270.848 497.344 270.848 524.224L270.848 524.224 270.848 524.224zM270.848 524.224" p-id="6484"></path>
                                    </svg>${this.config.iconButton.text}</button>`;
        this.rectangleButton.style.display = "none";
        document.body.appendChild(this.rectangleButton);

        // pc圆形icon
        this.roundButton = document.createElement("div"); //
        this.roundButton.setAttribute("id", "bytedesk_round_button");
        this.roundButton.classList.add(`bytedesk_${this.config.position}_${this.config.margin}`);
        this.roundButton.classList.add(`bytedesk_bottom_${this.config.bottomMargin}`);
        this.roundButton.setAttribute("style", `color: ${this.config.iconButton.color}; background-color: ${this.config.iconButton.background};`);
        this.roundButton.innerHTML = `<img src="https://cdn.bytedesk.com/assets/img/icon/chat.png" class="bytedesk_round_button_icon"/>` +
            `<span id="bytedesk_unread_count" class="bytedesk_unread_count">1</span>`;
        this.roundButton.style.display = "none";
        document.body.appendChild(this.roundButton);

        // pc聊天窗口
        this.chatWindow = document.createElement("div");
        this.chatWindow.setAttribute("id", "bytedesk_chat_window");
        this.chatWindow.classList.add(`bytedesk_chat_window_${this.config.column}`);
        this.chatWindow.classList.add(`bytedesk_${this.config.positionWindow}`);
        this.chatWindow.setAttribute("style", `background-color: ${this.config.background}`);
        this.chatWindow.innerHTML = `<iframe name="bytedesk" id="bytedesk_iframe" loading="lazy" width="100%" height="100%" src="" frameborder="0"></iframe>
                                    <iframe name="bytedesk_preload" id="bytedesk_iframe_preload" loading="lazy" width="0%" height="0%" src="" frameborder="0"></iframe>`;
        document.body.appendChild(this.chatWindow);
        this.chatWindowDOM = document.getElementById('bytedesk_chat_window')

        // h5长方形icon
        this.rectangleButtonMobile = document.createElement("div");
        this.rectangleButtonMobile.setAttribute("id", "bytedesk_rectangle_button_mobile");
        this.rectangleButtonMobile.setAttribute("style", `color: ${this.config.iconButton.color}; background-color: ${this.config.iconButton.background};`);
        this.rectangleButtonMobile.innerHTML = `<div style="margin-top: 14px;">` +
            `<img src="https://cdn.bytedesk.com/assets/img/icon/chat.png" class="bytedesk_rectangle_button_mobile_icon"/>` +
            `在线客服` +
            `<span id="bytedesk_rectangle_button_mobile_unread_count" class="bytedesk_unread_count">1</span>` +
            `</div>`;
        this.rectangleButtonMobile.style.display = "none";
        document.body.appendChild(this.rectangleButtonMobile)

        // h5对话窗口
        this.chatWindowMobile = document.createElement("div");
        this.chatWindowMobile.setAttribute("id", "bytedesk_chat_window_mobile");
        this.chatWindowMobile.classList.add("bytedesk_chat_window_mobile");
        this.chatWindowMobile.setAttribute("style", `background-color: ${this.config.background};`);
        this.chatWindowMobile.innerHTML = `<iframe name="bytedesk_mobile" id="bytedesk_iframe_mobile" loading="lazy" width="100%" height="100%" src="" frameborder="0"></iframe>
                <iframe name="bytedesk_preload_mobile" id="bytedesk_iframe_preload_mobile" loading="lazy" width="0%" height="0%" src="" frameborder="0"></iframe>`;
        document.body.appendChild(this.chatWindowMobile)
        this.chatWindowDomMobile = document.getElementById("bytedesk_chat_window_mobile");

        //
        if (document.getElementById("bytedesk_rectangle_button") != null) {
            document.getElementById('bytedesk_rectangle_button').addEventListener('click', () => this.openChatWindow())
        }
        if (document.getElementById("bytedesk_round_button") != null) {
            document.getElementById('bytedesk_round_button').addEventListener('click', () => this.openChatWindow())
        }
        if (document.getElementById("bytedesk_rectangle_button_mobile") != null) {
            document.getElementById('bytedesk_rectangle_button_mobile').addEventListener('click', () => this.openChatWindow())
        }
    }

    removeDOMElements() {
        if (document.getElementById("bytedesk_rectangle_button") != null) {
            document.getElementById("bytedesk_rectangle_button").remove();
        }
        if (document.getElementById("bytedesk_round_button") != null) {
            document.getElementById("bytedesk_round_button").remove();
        }
        if (document.getElementById("bytedesk_chat_window") != null) {
            document.getElementById("bytedesk_chat_window").remove();
        }
        if (document.getElementById("bytedesk_rectangle_button_mobile") != null) {
            document.getElementById("bytedesk_rectangle_button_mobile").remove();
        }
    }

    showIconButton() {
        // console.log('showIconButton')
        if (this.isMobile()) {
            // this.rectangleButtonMobile.style.display = "block";
            if (this.config.shape === 'rectangle') {
                this.rectangleButtonMobile.style.display = "block";
            } else {
                this.roundButton.style.display = "block"
            }
        } else {
            if (this.config.shape === 'rectangle') {
                this.rectangleButton.style.display = "block"
            } else {
                this.roundButton.style.display = "block"
            }
        }
        //
        this.chatWindowDOM.classList.remove('bytedesk_show')
        this.chatWindowDomMobile.classList.remove('bytedesk_show')
        // 标记聊天窗口不可见
        this.isChatWindowShown = false
        // 隐藏遮罩
        // this.chatWindowMobileMask.style.display = "none";
        document.getElementById("bytedesk_black_overlay").style.display = "none";
    }

    openChatWindow() {
        this.printLog('openChatWindow:', window.frames)
        this.rectangleButton.style.display = "none";
        this.roundButton.style.display = "none";
        this.rectangleButtonMobile.style.display = "none";
        if (this.isMobile()) {
            console.log('isMobile display mask')
            // this.chatWindowMobileMask.style.display = "block";
            document.getElementById("bytedesk_black_overlay").style.display = "block";
            this.chatWindowDomMobile.classList.add('bytedesk_show')
        } else {
            console.log('is not isMobile')
            this.chatWindowDOM.classList.add('bytedesk_show')
        }
        //
        if (this.firstOpen) {
            // console.log('firstOpen')
            // pc
            if (this.isMobile()) {
                console.log('bytedesk_iframe_mobile', this.chatUrl)
                window.frames['bytedesk_iframe_mobile'].src = this.chatUrl
                window.frames['bytedesk_iframe_preload_mobile'].src = ""
            } else {
                console.log('bytedesk_iframe', this.chatUrl)
                window.frames['bytedesk_iframe'].src = this.chatUrl
                window.frames['bytedesk_iframe_preload'].src = ""
            }
            this.firstOpen = false
        }
        // 标记聊天窗口可见
        this.isChatWindowShown = true
        // 隐藏未读数字
        this.unreadCount = 0
        // this.printLog('unreadCount = ' + this.unreadCount)
        document.getElementById("bytedesk_unread_count").style.display = "none";
        document.getElementById("bytedesk_unread_count").innerHTML = this.unreadCount;
        //
        document.getElementById("bytedesk_rectangle_button_mobile_unread_count").style.display = "none";
        document.getElementById("bytedesk_rectangle_button_mobile_unread_count").innerHTML = this.unreadCount;
    }

    isMobile() {
        function Android() {
            return navigator.userAgent.match(/Android/i);
        }
        function BlackBerry() {
            return navigator.userAgent.match(/BlackBerry/i);
        }
        function iOS() {
            return navigator.userAgent.match(/iPhone|iPad|iPod/i);
        }
        function Opera() {
            return navigator.userAgent.match(/Opera Mini/i);
        }
        function Windows() {
            return navigator.userAgent.match(/IEMobile/i);
        }
        return Android() || BlackBerry() || iOS() || Opera() || Windows();
    }

    start(lang, obj) {
        if (!obj) obj = {}
        //
        this.config = {
            // border: obj.border || 'border',
            shape: obj.shape || 'rectangle',
            // bannerStyle: obj.style || 2,
            column: obj.column || 'one',
            history: obj.history || '0',
            closable: obj.history || '1',
            // 自动弹窗，请求后台弹窗目前有点问题，暂时使用前端弹窗设置
            autoPop: obj.autoPop === undefined ? false : obj.autoPop,
            // 机器人是否优先显示分类
            v2robot: obj.v2robot === undefined ? "0" : obj.v2robot,
            // 自定义用户信息
            selfuser: obj.selfuser || '0',
            username: obj.username || '',
            nickname: obj.nickname || '',
            avatar: obj.avatar || '',
            // 商品信息
            goods: obj.goods || '0',
            goods_id: obj.goods_id || '0',
            goods_title: obj.goods_title || '',
            goods_content: obj.goods_content || "",
            goods_price: obj.goods_price || "0",
            goods_url: obj.goods_url || "",
            goods_imageUrl: obj.goods_imageUrl || "",
            goods_categoryCode: obj.goods_categoryCode || "",
            // 附言
            postscript: obj.postscript || '',
            // 位置信息
            position: obj.position || 'left',
            margin: obj.margin || '15',
            bottomMargin: obj.bottomMargin || '15',
            positionWindow: obj.positionWindow || 'right',
            // 颜色
            background: obj.bannerBackground || '#fff',
            color: obj.bannerColor || '#1d2e38',
            iconButton: {
                color: obj.buttonColor || '#1d2e38',
                background: obj.buttonBackground || '#fff',
                text: obj.buttonText || (lang === 'cn' ? '在线客服' : 'Live Chat'),
            }
        }
        //
        this.websiteUrl = window.location.href
        this.websiteTitle = document.title
        this.refererUrl = document.referrer
        // this.printLog('float websiteUrl:' + this.websiteUrl + ' float refererUrl:' + this.refererUrl)
        this.type = obj.type
        // this.workGroupWid = obj.workGroupWid
        // this.printLog('goods:' + this.config.goods)
        //
        if (this.isMobile()) {
            // h5
            this.chatUrl = (lang === 'cn' ? this.h5BaseUrl : this.h5BaseUrlEn) +
                '?sub=' + obj.subDomain +
                // '&uid=' + obj.adminUid +
                '&wid=' + obj.workGroupWid +
                '&type=' + obj.type +
                '&aid=' + obj.agentUid +
                '&history=' + this.config.history +
                '&lang=' + lang +
                '&color=' + encodeURIComponent(this.config.color) +
                '&background=' + encodeURIComponent(this.config.background) +
                // '&websiteurl=' + encodeURIComponent(this.websiteUrl) +
                // '&websitetitle=' + encodeURIComponent(this.websiteTitle) +
                // '&refererurl=' + encodeURIComponent(this.refererUrl) +
                '&v2robot=' + this.config.v2robot +
                (this.config.selfuser === "1" ? ('&selfuser=' + this.config.selfuser +
                    '&username=' + encodeURIComponent(this.config.username) +
                    '&nickname=' + encodeURIComponent(this.config.nickname) +
                    '&avatar=' + encodeURIComponent(this.config.avatar)) : "") +
                (this.config.goods === "1" ? ('&goods=' + this.config.goods +
                    '&goods_id=' + this.config.goods_id +
                    '&goods_title=' + encodeURIComponent(this.config.goods_title) +
                    '&goods_content=' + encodeURIComponent(this.config.goods_content) +
                    '&goods_price=' + encodeURIComponent(this.config.goods_price) +
                    '&goods_url=' + encodeURIComponent(this.config.goods_url) +
                    '&goods_imageUrl=' + encodeURIComponent(this.config.goods_imageUrl) +
                    '&goods_categoryCode=' + this.config.goods_categoryCode) : "") +
                (this.config.postscript !== '' ? ('&postscript=' + encodeURIComponent(this.config.postscript)) : '') +
                '&isembed=1&closable=1&preload=0&p'
            this.chatUrlPreload = (lang === 'cn' ? this.h5BaseUrl : this.h5BaseUrlEn) +
                '?sub=' + obj.subDomain +
                // '&uid=' + obj.adminUid +
                '&wid=' + obj.workGroupWid +
                '&type=' + obj.type +
                '&aid=' + obj.agentUid +
                '&history=' + this.config.history +
                '&lang=' + lang +
                '&color=' + encodeURIComponent(this.config.color) +
                '&background=' + encodeURIComponent(this.config.background) +
                // '&websiteurl=' + encodeURIComponent(this.websiteUrl) +
                // '&websitetitle=' + encodeURIComponent(this.websiteTitle) +
                // '&refererurl=' + encodeURIComponent(this.refererUrl) +
                '&v2robot=' + this.config.v2robot +
                (this.config.selfuser === "1" ? ('&selfuser=' + this.config.selfuser +
                    '&username=' + encodeURIComponent(this.config.username) +
                    '&nickname=' + encodeURIComponent(this.config.nickname) +
                    '&avatar=' + encodeURIComponent(this.config.avatar)) : "") +
                (this.config.goods === "1" ? ('&goods=' + this.config.goods +
                    '&goods_id=' + this.config.goods_id +
                    '&goods_title=' + encodeURIComponent(this.config.goods_title) +
                    '&goods_content=' + encodeURIComponent(this.config.goods_content) +
                    '&goods_price=' + encodeURIComponent(this.config.goods_price) +
                    '&goods_url=' + encodeURIComponent(this.config.goods_url) +
                    '&goods_imageUrl=' + encodeURIComponent(this.config.goods_imageUrl) +
                    '&goods_categoryCode=' + this.config.goods_categoryCode) : "") +
                (this.config.postscript !== '' ? ('&postscript=' + encodeURIComponent(this.config.postscript)) : '') +
                '&isembed=1&closable=1&preload=1&p'
        } else {
            // console.log('postscript:', this.config.postscript)
            // pc
            this.chatUrl = (lang === 'cn' ? this.pcBaseUrl : this.pcBaseUrlEn) +
                '?sub=' + obj.subDomain +
                '&uid=' + obj.adminUid +
                '&wid=' + obj.workGroupWid +
                '&type=' + obj.type +
                '&aid=' + obj.agentUid +
                '&history=' + this.config.history +
                '&lang=' + lang +
                '&color=' + encodeURIComponent(this.config.color) +
                '&background=' + encodeURIComponent(this.config.background) +
                '&column=' + (obj.column === 'two' ? '2' : '1') +
                '&websiteurl=' + encodeURIComponent(this.websiteUrl) +
                '&websitetitle=' + encodeURIComponent(this.websiteTitle) +
                '&refererurl=' + encodeURIComponent(this.refererUrl) +
                '&v2robot=' + this.config.v2robot +
                (this.config.selfuser === "1" ? ('&selfuser=' + this.config.selfuser +
                    '&username=' + encodeURIComponent(this.config.username) +
                    '&nickname=' + encodeURIComponent(this.config.nickname) +
                    '&avatar=' + encodeURIComponent(this.config.avatar)) : "") +
                (this.config.goods === "1" ? ('&goods=' + this.config.goods +
                    '&goods_id=' + this.config.goods_id +
                    '&goods_title=' + encodeURIComponent(this.config.goods_title) +
                    '&goods_content=' + encodeURIComponent(this.config.goods_content) +
                    '&goods_price=' + encodeURIComponent(this.config.goods_price) +
                    '&goods_url=' + encodeURIComponent(this.config.goods_url) +
                    '&goods_imageUrl=' + encodeURIComponent(this.config.goods_imageUrl) +
                    '&goods_categoryCode=' + this.config.goods_categoryCode) : "") +
                (this.config.postscript !== '' ? ('&postscript=' + encodeURIComponent(this.config.postscript)) : '') +
                '&closable=1&preload=0&p'
            this.chatMaxUrl = (lang === 'cn' ? this.pcBaseUrl : this.pcBaseUrlEn) +
                '?sub=' + obj.subDomain +
                '&uid=' + obj.adminUid +
                '&wid=' + obj.workGroupWid +
                '&type=' + obj.type +
                '&aid=' + obj.agentUid +
                '&history=' + this.config.history +
                '&lang=' + lang +
                '&color=' + encodeURIComponent(this.config.color) +
                '&background=' + encodeURIComponent(this.config.background) +
                '&column=2' +
                '&websiteurl=' + encodeURIComponent(this.websiteUrl) +
                '&websitetitle=' + encodeURIComponent(this.websiteTitle) +
                '&refererurl=' + encodeURIComponent(this.refererUrl) +
                '&v2robot=' + this.config.v2robot +
                (this.config.selfuser === "1" ? ('&selfuser=' + this.config.selfuser +
                    '&username=' + encodeURIComponent(this.config.username) +
                    '&nickname=' + encodeURIComponent(this.config.nickname) +
                    '&avatar=' + encodeURIComponent(this.config.avatar)) : "") +
                (this.config.goods === "1" ? ('&goods=' + this.config.goods +
                    '&goods_id=' + this.config.goods_id +
                    '&goods_title=' + encodeURIComponent(this.config.goods_title) +
                    '&goods_content=' + encodeURIComponent(this.config.goods_content) +
                    '&goods_price=' + encodeURIComponent(this.config.goods_price) +
                    '&goods_url=' + encodeURIComponent(this.config.goods_url) +
                    '&goods_imageUrl=' + encodeURIComponent(this.config.goods_imageUrl) +
                    '&goods_categoryCode=' + this.config.goods_categoryCode) : "") +
                (this.config.postscript !== '' ? ('&postscript=' + encodeURIComponent(this.config.postscript)) : '') +
                '&closable=0&preload=0&p'
            this.chatUrlPreload = (lang === 'cn' ? this.pcBaseUrl : this.pcBaseUrlEn) +
                '?sub=' + obj.subDomain +
                '&uid=' + obj.adminUid +
                '&wid=' + obj.workGroupWid +
                '&type=' + obj.type +
                '&aid=' + obj.agentUid +
                '&history=' + this.config.history +
                '&lang=' + lang +
                '&color=' + encodeURIComponent(this.config.color) +
                '&background=' + encodeURIComponent(this.config.background) +
                '&column=' + (obj.column === 'two' ? '2' : '1') +
                '&websiteurl=' + encodeURIComponent(this.websiteUrl) +
                '&websitetitle=' + encodeURIComponent(this.websiteTitle) +
                '&refererurl=' + encodeURIComponent(this.refererUrl) +
                '&v2robot=' + this.config.v2robot +
                (this.config.selfuser === "1" ? ('&selfuser=' + this.config.selfuser +
                    '&username=' + encodeURIComponent(this.config.username) +
                    '&nickname=' + encodeURIComponent(this.config.nickname) +
                    '&avatar=' + encodeURIComponent(this.config.avatar)) : "") +
                (this.config.goods === "1" ? ('&goods=' + this.config.goods +
                    '&goods_id=' + this.config.goods_id +
                    '&goods_title=' + encodeURIComponent(this.config.goods_title) +
                    '&goods_content=' + encodeURIComponent(this.config.goods_content) +
                    '&goods_price=' + encodeURIComponent(this.config.goods_price) +
                    '&goods_url=' + encodeURIComponent(this.config.goods_url) +
                    '&goods_imageUrl=' + encodeURIComponent(this.config.goods_imageUrl) +
                    '&goods_categoryCode=' + this.config.goods_categoryCode) : "") +
                (this.config.postscript !== '' ? ('&postscript=' + encodeURIComponent(this.config.postscript)) : '') +
                '&closable=1&preload=1&p'
        }
        // console.log('chaturl:', this.chatUrl)
        let app = this
        app.render()
        //
        //声明需要用到的变量
        var mx = 0, my = 0;      //鼠标x、y轴坐标（相对于left，top）
        var dx = 0, dy = 0;      //对话框坐标（同上）
        var isDraging = false;      //不可拖动
        //
        if (app.firstStart) {
            app.firstStart = false
            window.addEventListener('message', function (event) {
                // console.log('parent received: ', event)
                if (event.data === 'bytedesk-close') {
                    app.showIconButton();
                } else if (event.data === 'bytedesk-minus') {
                    app.showIconButton();
                } else if (event.data === 'bytedesk-max') {
                    // 打开大窗口
                    window.open(app.chatMaxUrl)
                } else if (event.data === 'bytedesk-popup') {
                    // 自动弹窗
                    app.openChatWindow()
                } else if (event.data.msg === 'bytedesk-mousedown') {
                    dx = app.chatWindowDOM.offsetLeft;
                    dy = app.chatWindowDOM.offsetTop;
                    //
                    mx = event.data.mouseX + dx;     //点击时鼠标X坐标
                    my = event.data.mouseY + dy;     //点击时鼠标Y坐标
                    //
                    isDraging = true;      //标记对话框可拖动      
                    // console.log('mx:' + mx + ' my:' + my + ' dx:' + dx + ' dy:' + dy)
                } else if (event.data.msg === 'bytedesk-mousemove') {
                    var x = event.data.mouseX + dx;      //移动时鼠标X坐标
                    var y = event.data.mouseY + dy;      //移动时鼠标Y坐标
                    if (isDraging) {        //判断对话框能否拖动
                        var moveX = dx + x - mx;      //移动后对话框新的left值
                        var moveY = dy + y - my;      //移动后对话框新的top值
                        //设置拖动范围
                        var pageW = window.screen.width;
                        var pageH = window.screen.height;
                        var dialogW = app.chatWindowDOM.offsetWidth;
                        var dialogH = app.chatWindowDOM.offsetHeight;
                        var maxX = pageW - dialogW;       //X轴可拖动最大值
                        var maxY = pageH - dialogH;       //Y轴可拖动最大值
                        moveX = Math.min(Math.max(0, moveX), maxX);     //X轴可拖动范围
                        moveY = Math.min(Math.max(0, moveY), maxY);     //Y轴可拖动范围
                        //重新设置对话框的left、top
                        app.chatWindowDOM.style.left = moveX + 'px';
                        app.chatWindowDOM.style.top = moveY + 'px';
                        // console.log('left:' + moveX + ' top:' + moveY)
                    };
                } else if (event.data === 'bytedesk-mouseup') {
                    isDraging = false;
                } else if (event.data.msg === 'bytedesk-message') {
                    // 收到消息
                    if (!app.isChatWindowShown) {
                        app.unreadCount += 1
                        app.printLog("unreadCount: " + app.unreadCount);
                        document.getElementById("bytedesk_unread_count").style = "";
                        document.getElementById("bytedesk_unread_count").innerHTML = app.unreadCount;
                        //
                        document.getElementById("bytedesk_rectangle_button_mobile_unread_count").style = "";
                        document.getElementById("bytedesk_rectangle_button_mobile_unread_count").innerHTML = app.unreadCount;
                    }
                } else if (event.data.msg === 'bytedesk-status') {
                    // 在线状态
                    // bytedesk-status type:workGroup uuid:201807171659201 status:online
                    app.printLog('bytedesk-status type:' + event.data.type + ' uuid:' + event.data.uuid + ' status:' + event.data.status)
                    // TODO: 根据在线状态，给出不同提示
                }
            }, false);
        }
        // 获取技能组配置
        if (this.config.autoPop) {
            this.openChatWindow()
        }
        // 隐藏未读数字
        if (app.unreadCount === 0) {
            document.getElementById("bytedesk_unread_count").style.display = "none";
            document.getElementById("bytedesk_rectangle_button_mobile_unread_count").style.display = "none";
        }
        // 预加载
        if (this.isMobile()) {
            window.frames['bytedesk_iframe_mobile'].src = this.chatUrl
            // window.frames['bytedesk_iframe_preload_mobile'].src = this.chatUrlPreload
        } else {
            window.frames['bytedesk_iframe_preload'].src = this.chatUrlPreload
        }
    }

    // 初始化，不显示icon
    // 不提供漂浮框，开放触发客服接口，支持用户自定义漂浮框
    startWithoutIcon() {
        this.openChatWindow()
    }

    // 隐藏icon
    hideIcon() {
        this.rectangleButton.style.display = "none";
        this.roundButton.style.display = "none";
    }

    showIcon() {
        this.roundButton.style.display = "block";
    }

    printLog(content) {
        // if (!this.isProduction) {
            console.log(content)
        // }
    }
}

const byteDesk = new ByteDesk();
