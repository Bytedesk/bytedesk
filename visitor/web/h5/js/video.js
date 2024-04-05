// https://webrtc.github.io/samples/
document.getElementById("app-wrapper").style.display = '';
var options = {
    fullscreenEl: false, //关闭全屏按钮
};
Vue.use(vuePhotoPreview, options);
// 准备翻译的语言环境信息
const messages = {
    en: {
        contactAgent: 'contact agent',
        typing: 'typing...',
        sendLink: 'send link',
        agentChat: 'agent chat',
        viewFile: 'view file',
        arrived: 'arrived',
        readed: 'readed',
        sending: 'sending',
        error: 'error',
        leaveWord: 'leave message',
        name: 'name',
        inputName: 'input name',
        mobile: 'mobile',
        inputMobile: 'input mobile',
        leaveContent: 'leave content',
        email: 'email',
        inputEmail: 'input email',
        age: 'age',
        inputAge: 'input age',
        job: 'job',
        inputJob: 'input job',
        pleaseRate: 'please rate',
        veryGood: 'very good',
        good: 'good',
        average: 'average',
        notGood: 'not good',
        bad: 'very bad',
        submit: 'submit',
        inviteRate: "invite rate",
        rateResult: "rated",
        rate: 'rate',
        rateContent: 'rate content',
        pleaseInput: 'please input',
        rateAgain: 'cant rate again',
        continueChat: 'continue',
        agentCloseThread: "agent close thread",
        visitorCloseThread: "visitor close thread",
        autoCloseThread: "system close thread",
        agentOffline: "agent offline, please leave message",
        // systemUser: 'system user',
        postScriptPrefix: '<postScript>:',
        send: 'send',
        joinQueueThread: 'start chat',
        cancel: 'cancel',
        image: 'image',
        restart: 'restart',
        wrongMobileNum: 'wrong mobile number',
        ageMustBeNum: 'age must be number',
        contentMustNotNull: 'content must not be null',
        contentTooLong: 'content too long',
        wrongWid: 'wrong wid',
        queuing: 'queuing, please wait'
    },
    cn: {
        contactAgent: '联系客服',
        typing: '对方正在输入...',
        sendLink: '发送链接',
        agentChat: '人工客服',
        viewFile: '查看文件',
        arrived: '送达',
        readed: '已读',
        sending: '发送中',
        error: '错误',
        leaveWord: '留言',
        name: '姓名',
        inputName: '请输入姓名',
        mobile: '手机号',
        inputMobile: '请输入手机号',
        leaveContent: '留言内容',
        email: '邮箱',
        inputEmail: '请输入邮箱',
        age: '年龄',
        inputAge: '请输入年龄',
        job: '职业',
        inputJob: '请输入职业',
        pleaseRate: '请对我们服务做出评价',
        veryGood: '非常满意',
        good: '满意',
        average: '一般',
        notGood: '不满意',
        bad: '非常不满意',
        submit: '提交',
        inviteRate: "邀请评价",
        rateResult: "已评价",
        rate: '评价',
        rateContent: '评价内容',
        pleaseInput: '请简单描述您的问题',
        rateAgain: '不能重复评价',
        continueChat: '继续会话',
        agentCloseThread: "客服关闭会话",
        visitorCloseThread: "访客关闭会话",
        autoCloseThread: "长时间没有对话，系统自动关闭会话",
        agentOffline: "当前无客服在线，请留言",
        // systemUser: '系统通知',
        postScriptPrefix: '<附言>:',
        send: '发送',
        joinQueueThread: '接入会话',
        cancel: '取消',
        image: '图片',
        restart: '重新开始',
        wrongMobileNum: '手机号错误',
        ageMustBeNum: '年龄必须为数字',
        contentMustNotNull: '消息不能为空',
        contentTooLong: '消息长度太长，请分多次发送',
        wrongWid: 'siteId或者工作组id错误',
        queuing: '排队中，请稍后'
    }
};
// 通过选项创建 VueI18n 实例
const i18n = new VueI18n({
    locale: 'cn', // 设置地区
    messages, // 设置地区信息
});
var app = new Vue({
    el: '#app',
    i18n,
    name: 'video',
    data() {
        return {
            //
            IS_PRODUCTION: false,
            HTTP_HOST: "http://127.0.0.1:8000",
            STOMP_HOST: "http://127.0.0.1:8000",
            // IS_PRODUCTION: false,
            // HTTP_HOST: "http://192.168.0.103:8000",
            // STOMP_HOST: "http://192.168.0.103:8000",
            // IS_PRODUCTION: true,
            // HTTP_HOST: "https://h5api.bytedesk.com",
            // STOMP_HOST: "https://h5stomp.bytedesk.com",
            //
            title: '在线客服',
            isInputingVisible: false,
            localPreviewContent: '',
            //
            imageDialogVisible: false,
            currentImageUrl: '',
            currentVoiceUrl: '',
            // show_emoji: false,
            // emojiBaseUrl: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/',
            inputContent: '',
            messages: [],
            loadMoreVisible: true,
            //
            // 留言 + 表单
            realname: '',
            mobile: '',
            email: '',
            age: 0,
            job: '',
            content: '',
            //
            showRealname: false,
            showMobile: false,
            showEmail: false,
            showAge: false,
            showJob: false,
            showContent: false,
            // 仅允许评价一次
            isRated: false,
            // 是否客服邀请评价
            isInviteRate: false,
            // 满意度评分
            rateScore: 5,
            rateValue: '非常满意',
            // 满意度附言
            rateContent: '',
            //
            isLoading: false,
            stompClient: '',
            sessionId: '',
            preSessionId: '',
            browseInviteBIid: '',
            //
            access_token: '',
            passport: {
                token: {
                    access_token: '',
                }
            },
            // adminUid: '',
            workGroupWid: '',
            subDomain: '',
            client: 'web_h5_video',
            thread: {
                id: 0,
                tid: '',
                topic: '',
                type: '',
                visitor: {
                    uid: '',
                    nickname: 'visitor',
                    avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'
                }
            },
            // 已经订阅的topic
            subscribedTopics: [],
            // 加载聊天记录offset
            page: 0,
            // 是否是最后一批聊天记录
            last: false,
            // workGroup/visitor/contact/group
            type: 'workGroup',
            // 指定客服
            agentUid: '',
            // 当前访客用户名
            selfuser: "0",
            uid: '',
            username: '',
            password: '',
            nickname: '',
            avatar: '',
            // 本地存储access_token的key
            token: 'bd_kfe_token',
            isConnected: false,
            answers: [],
            isRobot: false,
            isQueuing: false,
            isThreadStarted: false,
            isThreadClosed: false,
            isManulRequestThread: false,
            isRequestAgent: false,
            // focusStatus: true,
            leaveMessageTip: '',
            loadHistory: '0',
            robotUser: {
                uid: '',
                nickname: '',
                avatar: ''
            },
            //
            postscript: '',
            showScript: false,
            // 
            hideNav: false,
            backUrl: '',
            topTip: '',
            showTopTip: false,
            //
            showMessage: true,
            showInputBar: true,
            showLeaveMessage: false,
            showRate: false,
            showForm: false,
            //
            insertMessage: {},
            //
            showInputToolBar: true,
            // showInputWithTransfer: false,
            showPlusButton: true,
            showPlusPanel: false,
            showRestartPanel: false,
            //
            lang: 'cn',
            v2robot: '0',
            //
            loadHistoryTimer: '',
            sendMessageTimer: '',
            // 快捷按钮
            quickButtonArrow: '↓',
            showQuickButton: false,
            showQuickButtonItem: true,
            quickButtons: [],
            //
            emotionBaseUrl: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/',
            // 表情
            emotionMap: {
                '[微笑]': '100.gif',
                '[撇嘴]': '101.gif',
                '[色]': '102.gif',
                '[发呆]': '103.gif',
                '[得意]': '104.gif',
                '[流泪]': '105.gif',
                '[害羞]': '106.gif',
                '[闭嘴]': '107.gif',
                '[睡]': '108.gif',
                '[大哭]': '109.gif',

                '[尴尬]': '110.gif',
                '[发怒]': '111.gif',
                '[调皮]': '112.gif',
                '[呲牙]': '113.gif',
                '[惊讶]': '114.gif',
                '[难过]': '115.gif',
                '[酷]': '116.gif',
                '[冷汗]': '117.gif',
                '[抓狂]': '118.gif',
                '[吐]': '119.gif',

                '[偷笑]': '120.gif',
                '[愉快]': '121.gif',
                '[白眼]': '122.gif',
                '[傲慢]': '123.gif',
                '[饥饿]': '124.gif',
                '[困]': '125.gif',
                '[惊恐]': '126.gif',
                '[流汗]': '127.gif',
                '[憨笑]': '128.gif',
                '[悠闲]': '129.gif',

                '[奋斗]': '130.gif',
                '[咒骂]': '131.gif',
                '[疑问]': '132.gif',
                '[嘘]': '133.gif',
                '[晕]': '134.gif',
                '[疯了]': '135.gif',
                '[衰]': '136.gif',
                '[骷髅]': '137.gif',
                '[敲打]': '138.gif',
                '[再见]': '139.gif',

                '[擦汗]': '140.gif',
                '[抠鼻]': '141.gif',
                '[鼓掌]': '142.gif',
                '[糗大了]': '143.gif',
                '[坏笑]': '144.gif',
                '[左哼哼]': '145.gif',
                '[右哼哼]': '146.gif',
                '[哈欠]': '147.gif',
                '[鄙视]': '148.gif',
                '[委屈]': '149.gif',

                '[快哭]': '150.gif',
                '[阴险]': '151.gif',
                '[亲亲]': '152.gif',
                '[吓]': '153.gif',
                '[可怜]': '154.gif',
                '[菜刀]': '155.gif',
                '[西瓜]': '156.gif',
                '[啤酒]': '157.gif',
                '[篮球]': '158.gif',
                '[乒乓]': '159.gif',

                '[咖啡]': '160.gif',
                '[饭]': '161.gif',
                '[猪头]': '162.gif',
                '[玫瑰]': '163.gif',
                '[凋谢]': '164.gif',
                '[嘴唇]': '165.gif',
                '[爱心]': '166.gif',
                '[心碎]': '167.gif',
                '[蛋糕]': '168.gif',
                '[闪电]': '169.gif',

                '[炸弹]': '170.gif',
                '[刀]': '171.gif',
                '[足球]': '172.gif',
                '[瓢虫]': '173.gif',
                '[便便]': '174.gif',
                '[月亮]': '175.gif',
                '[太阳]': '176.gif',
                '[礼物]': '177.gif',
                '[拥抱]': '178.gif',
                '[强]': '179.gif',

                '[弱]': '180.gif',
                '[握手]': '181.gif',
                '[胜利]': '182.gif',
                '[抱拳]': '183.gif',
                '[勾引]': '184.gif',
                '[拳头]': '185.gif',
                '[差劲]': '186.gif',
                '[爱你]': '187.gif',
                '[No]': '188.gif',
                '[OK]': '189.gif',

                '[爱情]': '190.gif',
                '[飞吻]': '191.gif',
                '[跳跳]': '192.gif',
                '[发抖]': '193.gif',
                '[怄火]': '194.gif',
                '[转圈]': '195.gif',
                '[磕头]': '196.gif',
                '[回头]': '197.gif',
                '[跳绳]': '198.gif',
                '[投降]': '199.gif',

                '[激动]': '201.gif',
                '[乱舞]': '202.gif',
                '[献吻]': '203.gif',
                '[左太极]': '204.gif',
                '[右太极]': '205.gif'
            },
            // 音视频
            audioStreamConstraints: {
                audio: true,
                video: false,
            },
            videoStreamConstraints: {
                audio: true,
                video: true,
            },
            // Define initial start time of the call (defined as connection between peers).
            startTime: '',
            // 
            localVideo: '',
            remoteVideo: '',
            // 
            localStream: '',
            // remoteStream: '',
            // 
            localPeerConnection: '',
            // remotePeerConnection: '',
            // 是否在微信内运行
            isWeixinBrowser: false
        };
    },
    computed: {
        disabled() {
            return this.thread.tid === '';
        },
        sendButtonDisabled() {
            return this.inputContent.trim().length === 0;
        },
        threadTopic() {
            return this.thread.topic.replace(/\//g, ".");
        },
        show_header() {
            return true;
        },
        connectedImage() {
            return this.isConnected ? 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/connected.png'
                : 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/disconnected.png';
        },
        leaveWordTip() {
            return this.$t('leaveWord');
        },
        nameTip() {
            return this.$t("name");
        },
        inputNameTip() {
            return this.$t("inputName")
        },
        mobileTip() {
            return this.$t("mobile")
        },
        inputMobileTip() {
            return this.$t("inputMobile")
        },
        leaveContentTip() {
            return this.$t("leaveContent")
        },
        emailTip() {
            return this.$t("email")
        },
        inputEmailTip() {
            return this.$t("inputEmail")
        },
        ageTip() {
            return this.$t("age")
        },
        inputAgeTip() {
            return this.$t("inputAge")
        },
        jobTip() {
            return this.$t("job")
        },
        inputJobTip() {
            return this.$t("inputJob")
        },
        pleaseRateTip() {
            return this.$t("pleaseRate")
        },
        veryGoodTip() {
            return this.$t("veryGood")
        },
        goodTip() {
            return this.$t("good")
        },
        averageTip() {
            return this.$t("average");
        },
        notGoodTip() {
            return this.$t("notGood");
        },
        badTip() {
            return this.$t("bad");
        },
        pleaseInputTip() {
            return this.$t("pleaseInput");
        },
        rateTip() {
            return this.$t('rate');
        },
        rateContentTip() {
            return this.$t('rateContent')
        },
        postScriptPrefixTip() {
            return this.$t('postScriptPrefix')
        }
    },
    methods: {
        switchPlusPanel() {
            this.showPlusPanel = !this.showPlusPanel
        },
        // TODO: 实现imageClicked函数
        switchAgent() {
            this.showLeaveMessage = false;
            this.isRobot = false;
            this.requestThread();
        },
        switchLeaveMessage() {
            this.showMessage = false;
            this.showInputBar = false;
            this.showLeaveMessage = true;
        },
        switchForm() {
            this.showMessage = false;
            this.showInputBar = false;
            this.showForm = true;
        },
        switchRate() {
            this.showMessage = false
            this.showInputBar = false
            this.showRate = true
        },
        switchMessage() {
            this.showMessage = true
            this.showInputBar = true
            this.showRate = false
            this.showForm = false
            this.showLeaveMessage = false
        },
        imageClicked(imageUrl) {
            // console.log('image clicked:', imageUrl)
            window.open(imageUrl);
        },
        fileClicked(fileUrl) {
            window.open(fileUrl);
        },
        voiceClicked(voiceUrl) {
            window.open(voiceUrl);
        },
        videoClicked(videoOrShortUrl) {
            window.open(videoOrShortUrl);
        },
        is_self(message) {
            if (message.user == null) {
                return false
            }
            return message.user.uid === this.uid;
        },
        // 发送状态
        is_sending(message) {
            return message.status === 'sending'
        },
        is_stored(message) {
            return message.status === 'stored'
        },
        is_received(message) {
            return message.status === 'received'
        },
        is_error(message) {
            return message.status === 'error'
        },
        is_read(message) {
            return message.status === 'read'
        },
        can_recall(message) {
            return (
                this.callRecallMessage(message) &&
                this.is_self(message) &&
                (message.type === 'text' || message.type === 'image')
            );
        },
        // 检测是否在3分钟之内，允许撤回消息
        callRecallMessage(message) {
            let now = moment(new Date(), "YYYY-MM-DD HH:mm:ss");
            let createdAt = moment(message.createdAt, "YYYY-MM-DD HH:mm:ss");
            let seconds = now.diff(createdAt, "seconds");
            // 现在距消息发送的时间差
            // console.log('seconds passed: ', seconds)
            if (seconds < 180) {
                return true;
            }
            return false;
        },
        // 消息类型
        is_type_text(message) {
            return message.type === 'text'
                || message.type === 'notification_thread'
                || message.type === 'notification_auto_close'
        },
        is_type_robot(message) {
            return message.type === 'robot'
        },
        is_type_robot_v2(message) {
            return message.type === 'robotv2'
        },
        is_type_robot_result(message) {
            return message.type === 'robot_result'
        },
        is_type_image(message) {
            return message.type === 'image'
        },
        is_type_file(message) {
            return message.type === 'file'
        },
        is_type_voice(message) {
            return message.type === 'voice'
        },
        is_type_video: function (message) {
            return message.type === "video" || message.type === 'shortvideo';
        },
        is_type_commodity(message) {
            return message.type === 'commodity'
        },
        is_type_questionnaire(message) {
            return message.type === 'questionnaire'
        },
        is_type_company(message) {
            return message.type === 'company'
        },
        is_type_workGroup(message) {
            return message.type === 'workGroup'
        },
        is_type_form_request(message) {
            return message.type === 'notification_form_request'
        },
        is_type_form_result(message) {
            return message.type === 'notification_form_result'
        },
        is_type_thread(message) {
            return message.type === 'notification_thread'
        },
        is_type_notification(message) {
            return message.type !== 'notification_preview'
                && message.type !== 'notification_thread'
                && message.type.startsWith('notification')
                || message.type === 'commodity'
        },
        is_type_close(message) {
            return message.type === 'notification_auto_close'
                || message.type === 'notification_agent_close'
        },
        is_type_notification_agent_close(message) {
            return message.type === 'notification_agent_close'
        },
        is_type_notification_visitor_close(message) {
            return message.type === 'notification_visitor_close'
        },
        is_type_notification_auto_close(message) {
            return message.type === 'notification_auto_close'
        },
        is_type_notification_thread_reentry(message) {
            return message.type === 'notification_thread_reentry'
        },
        is_type_notification_connect(message) {
            return message.type === 'notification_connect'
        },
        is_type_notification_disconnect(message) {
            return message.type === 'notification_disconnect'
        },
        is_type_notification_offline(message) {
            return message.type === 'notification_offline'
        },
        is_type_notification_queue_accept(message) {
            return message.type === 'notification_queue_accept'
        },
        is_type_notification_invite_rate(message) {
            return message.type === 'notification_invite_rate'
        },
        is_type_notification_rate_result(message) {
            return message.type === 'notification_rate_result'
        },
        is_type_notification_rate_helpful(message) {
            return message.type === 'notification_rate_helpful'
        },
        is_type_notification_rate_helpless(message) {
            return message.type === 'notification_rate_helpless'
        },
        my_uid() {
            return this.uid
        },
        my_username() {
            return this.username
        },
        thread_nickname() {
            return this.nickname.trim().length > 0 ? this.nickname : this.thread.visitor.nickname
        },
        my_nickname() {
            return this.nickname.trim().length > 0 ? this.nickname : this.thread.visitor.nickname
        },
        my_avatar() {
            return this.avatar.trim().length > 0 ? this.avatar : this.thread.visitor.avatar
        },
        jsonObject(content) {
            // console.log('parse json:', content);
            return content === null ? '{"categoryCode":"","content":"","id":"0","imageUrl":"","price":"","title":"","type":"commodity","url":""}' : JSON.parse(content)
        },
        // 识别链接, FIXME: 对于不带http(s)前缀的url，会被识别为子链接，点击链接无法跳出
        replaceUrl(content) {
            if (!content) {
                return content;
            }
            const urlPattern = /(https?:\/\/|www\.)[a-zA-Z_0-9\-@]+(\.\w[a-zA-Z_0-9\-:]+)+(\/[()~#&\-=?+%/.\w]+)?/g;
            return content.replace(urlPattern, (url) => {
                // console.log('url:', url)
                return `<a href="${url}" target="_blank">${url}</a>`;
            })
        },
        //  在发送信息之后，将输入的内容中属于表情的部分替换成emoji图片标签
        //  再经过v-html 渲染成真正的图片
        replaceFace(content) {
            if (content === null || content === undefined) {
                return ''
            }
            // 识别链接
            let replaceUrl = this.replaceUrl(content)
            //
            var emotionMap = this.emotionMap;
            var reg = /\[[\u4E00-\u9FA5NoOK]+\]/g
            var matchresult = replaceUrl.match(reg)
            var result = replaceUrl
            if (matchresult) {
                for (var i = 0; i < matchresult.length; i++) {
                    result = result.replace(matchresult[i], '<img height=\'25px\' width=\'25px\' style=\'margin-bottom:4px;\' src=\'' + this.emotionBaseUrl + emotionMap[matchresult[i]] + '\'>')
                }
            }
            return result
        },
        escapeHTML(content) {
            return content.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        },
        handleImageDialogClose(done) {
            done()
        },
        scrollToBottom() {
            // 聊天记录滚动到最底部
            let vm = this;
            this.$nextTick(() => {
                const ulm = vm.$refs.listm;
                if (ulm != null) {
                    ulm.scrollTop = ulm.scrollHeight
                }
            })
        },
        pushToMessageArray(message) {
            // 判断是否已经存在
            let contains = false
            for (var i = 0; i < this.messages.length; i++) {
                let msg = this.messages[i]
                if (msg.mid === message.mid) {
                    contains = true
                }
            }
            // 如果不存在，则保存
            if (!contains) {
                this.messages.push(message);
                // 排序
                this.messages.sort(function (a, b) {
                    if (a.createdAt > b.createdAt) {
                        return 1
                    }
                    if (a.createdAt < b.createdAt) {
                        return -1
                    }
                    return 0
                });
            }
            // 消息持久化到 localstorage, 当前消息条数大于100时，清空数据
            // if (this.messages.length > 100) {
            //     localStorage.setItem(this.threadTopic, "")
            // } else {
            //     let localMessages = JSON.stringify(this.messages);
            //     localStorage.setItem(this.threadTopic, localMessages)
            // }
            // 查看大图刷新
            if (message.type === 'image') {
                app.$previewRefresh()
            }
        },
        unshiftToMessageArray(message) {
            // 判断是否已经存在
            let contains = false
            for (var i = 0; i < this.messages.length; i++) {
                let msg = this.messages[i]
                if (msg.mid === message.mid) {
                    contains = true
                }
            }
            // 如果不存在，则保存
            if (!contains) {
                this.messages.unshift(message);
            }
            // 查看大图刷新
            if (message.type === 'image') {
                app.$previewRefresh()
            }
        },
        // 获取URL参数
        getUrlParam(name) {
            // console.log('window.location:', window.location)
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            if (r != null)
                return decodeURIComponent(r[2]);
            return null; //返回参数值
        },
        // 判断是否是在微信浏览器中打开
        isWeixin() {
            var ua = navigator.userAgent.toLowerCase();
            var isWeixin = ua.indexOf('micromessenger') != -1;
            if (isWeixin) {
                return true;
            } else {
                return false;
            }
        },
        /**
         * 1. 首先判断是否已经注册过
         * 2. 如果已经注册过，则直接调用登录接口
         * 3. 如果没有注册过，则从服务器请求用户名
         */
        requestUsername() {
            this.$indicator.open();
            //
            // this.username = localStorage.bd_kfe_username;
            // this.password = this.username;
            // if (this.username) {
            //     this.login();
            // } else {
            //
            $.ajax({
                url: this.HTTP_HOST + '/visitor/api/username',
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    nickname: this.nickname,
                    avatar: this.avatar,
                    subDomain: this.subDomain,
                    client: this.client
                },
                success: function (response) {
                    console.log('user:', response.data);
                    // 登录
                    app.uid = response.data.uid;
                    app.username = response.data.username;
                    app.password = app.username;
                    app.nickname = response.data.nickname;
                    app.avatar = response.data.avatar;
                    // 本地存储
                    localStorage.bd_kfe_uid = app.uid;
                    localStorage.bd_kfe_username = app.username;
                    // 登录
                    app.login();
                },
                error: function (error) {
                    //Do Something to handle error
                    console.log(error);
                }
            });
            // }
        },
        registerUser() {
            //
            var username = this.getUrlParam("username");
            var nickname = this.getUrlParam("nickname") == null
                ? username : this.getUrlParam("nickname");
            var avatar = this.getUrlParam("avatar") === null
                ? "" : this.getUrlParam("avatar");
            this.printLog("username self:" + username + nickname + avatar);
            //
            var password = username; // 用户名作为默认密码
            $.ajax({
                url: this.HTTP_HOST + "/visitor/api/register/user",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    username: username,
                    nickname: nickname,
                    password: password,
                    avatar: avatar,
                    subDomain: this.subDomain,
                    client: this.client
                }),
                success: function (response) {
                    app.printLog("registerUser success: " + JSON.stringify(response));
                    //
                    if (response.status_code === 200) {
                        // 登录
                        app.uid = response.data.uid;
                        app.username = response.data.username;
                        app.password = password;
                        app.nickname = response.data.nickname;
                        app.avatar = response.data.avatar
                        // 本地存储
                        localStorage.bd_kfe_uid = app.uid;
                        localStorage.bd_kfe_username = app.username;
                        localStorage.bd_kfe_nickname = app.nickname;
                    } else {
                        // 账号已经存在
                        app.uid = response.data
                        app.username = username + '@' + app.subDomain;
                        app.password = password;
                        app.nickname = nickname;
                        app.avatar = avatar
                        // 本地存储
                        localStorage.bd_kfe_uid = app.uid;
                        localStorage.bd_kfe_username = app.username;
                        localStorage.bd_kfe_nickname = app.nickname;
                    }
                    // 登录
                    app.login();
                },
                error: function (error) {
                    //Do Something to handle error
                    app.printLog(error);
                }
            });
        },
        // 2. oauth2登录, TODO: 优化请求流程
        login() {
            $.ajax({
                url: this.HTTP_HOST + "/oauth/token",
                type: "post",
                data: {
                    "username": this.username,
                    "password": this.password,
                    "grant_type": "password",
                    "scope": "all"
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Basic Y2xpZW50OnNlY3JldA==');
                },
                success: function (response) {
                    // console.log("login success: ", response);
                    // 本地存储，
                    localStorage.access_token = response.access_token;
                    localStorage.bd_kfe_access_token = response.access_token;
                    // 请求会话
                    app.requestThread();
                    // // 拉取快捷按钮
                    // app.getQuickButtons()
                },
                error: function (error) {
                    console.log(error);
                }
            });
        },
        requestThread() {
            // 
            this.$indicator.open();
            //
            $.ajax({
                url: this.HTTP_HOST +
                    "/api/thread/request",
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    wId: this.workGroupWid,
                    type: this.type,
                    aId: this.agentUid,
                    webrtc: 1,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    app.printLog('requestThread:' + JSON.stringify(response));
                    //
                    app.$indicator.close();
                    //
                    app.dealWithThread(response)
                    // 发送指纹
                    // app.fingerPrint2();
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
            // 发送指纹
            // this.fingerPrint2();
        },
        dealWithThread(response) {
            console.log('dealWithThread:', response);
            let message = response.data;
            // 
            if (response.status_code === 200) { 
                // this.initVideo()
                // 1. 保存thread
                app.thread = message.thread;
                // 4. 设置窗口左上角标题
                if (app.thread.appointed) {
                    app.title = app.thread.agent.nickname
                } else {
                    app.title = app.thread.workGroup.nickname;
                }
                // 设置当前为人工客服
                app.isRobot = false;
                app.robotUser = message.user;
                // 防止会话超时自动关闭，重新标记本地打开会话
                app.isThreadClosed = false;
            } else if (response.status_code === 201) {
                // this.initVideo()
                // message.content = '继续之前会话';
                // if (app.isRequestAgent || app.isManulRequestThread || app.loadHistory === '0') {
                app.pushToMessageArray(message);
                // }
                // 1. 保存thread
                app.thread = message.thread;
                // 3. 加载聊天记录
                // app.loadHistoryMessagesByTopic(app.thread.topic);
                // 4. 头像、标题、描述
                if (app.thread.appointed) {
                    app.title = app.thread.agent.nickname
                } else {
                    app.title = app.thread.workGroup.nickname;
                }
                // 设置当前为人工客服
                app.isRobot = false;
                app.robotUser = message.user;
                // 防止会话超时自动关闭，重新标记本地打开会话
                app.isThreadClosed = false;
            } else if (response.status_code === 202) {
                app.$toast('当前视频客服忙线中，请稍后再试');
                this.hangupAction()
                // 排队
                app.pushToMessageArray(message);
                // 1. 保存thread
                app.thread = message.thread;
                // 是否正在排队
                app.isQueuing = true
            } else if (response.status_code === 203) {
                app.$toast('当前无视频客服在线，请稍后再试');
                this.hangupAction()
                // 当前非工作时间，请自助查询或留言
                app.pushToMessageArray(message);
                app.leaveMessageTip = message.content;
                // 1. 保存thread
                app.thread = message.thread;
                // 4. 设置窗口左上角标题
                if (app.thread.appointed) {
                    app.title = app.thread.agent.nickname
                } else {
                    app.title = app.thread.workGroup.nickname;
                }
                // 显示留言界面
                // app.switchLeaveMessage();
            } else if (response.status_code === 204) {
                app.$toast('当前无视频客服在线，请稍后再试');
                this.hangupAction()
                // 当前无客服在线，请自助查询或留言
                app.pushToMessageArray(message);
                app.leaveMessageTip = message.content;
                // 1. 保存thread
                app.thread = message.thread;
                // 4. 设置窗口左上角标题
                if (app.thread.appointed) {
                    app.title = app.thread.agent.nickname
                } else {
                    app.title = app.thread.workGroup.nickname;
                }
                // 显示留言界面
                // app.switchLeaveMessage();
            } else if (response.status_code === 205) {
                // 插入业务路由，相当于咨询前提问问卷（选择 或 填写表单）
                app.pushToMessageArray(message);
                // 1. 保存thread
                app.thread = message.thread;
            } else if (response.status_code === 206) {
                // 返回机器人初始欢迎语 + 欢迎问题列表
                // if (app.isRequestAgent || app.isManulRequestThread || app.loadHistory === '0') {
                app.pushToMessageArray(message);
                // }
                // 1. 保存thread
                app.thread = message.thread;
                // 3. 加载聊天记录
                // app.loadHistoryMessagesByTopic(app.thread.topic);
                // 4. 设置窗口左上角标题
                if (app.thread.appointed) {
                    app.title = app.thread.agent.nickname
                } else {
                    app.title = app.thread.workGroup.nickname;
                }
                // 返回机器人初始欢迎语 + 欢迎问题列表
                // app.pushToMessageArray(message);
                // 1. 保存thread
                // app.thread = message.thread;
                // 2. 设置当前状态为机器人问答
                app.isRobot = true;
                app.robotUser = message.user;
            } else if (response.status_code === 207) { 
                app.$toast('视频客服忙线中，请稍后再试');
                this.hangupAction()
            } else if (response.status_code === -1) {
                this.hangupAction()
                app.login();
            } else if (response.status_code === -2) {
                // sid 或 wid 错误
                app.$toast(this.$t('wrongWid'));
                this.hangupAction()
            } else if (response.status_code === -3) {
                // app.$toast('您已经被禁言');
                app.$toast('未知错误');
                this.hangupAction()
                // 显示留言界面
                // app.switchLeaveMessage();
            } else if (response.status_code === -4) {
                // 系统压力太大，限流；请稍后访问
                app.$toast('系统流量过大，请稍后再试');
                this.hangupAction()
            } else if (response.status_code === -5) {
                // 客服涉嫌诈骗，已经封号
                // app.$toast('客服涉嫌诈骗，已经封号');
                this.hangupAction()
            }
            // 设置窗口标题
            document.title = app.title;
            // app.scrollToBottom();
            // 拉取快捷按钮
            // app.getQuickButtons()
            // 建立长连接
            app.byteDeskConnect();
        },
        // 满意度评价
        rate() {
            // 隐藏满意度评价
            this.switchMessage()
            // 判断是否已经评价过，避免重复评价
            if (app.isRated) {
                // this.$message({ message: this.$t('rateAgain'), type: 'warning' });
                app.$toast(this.$t('rateAgain'))
                return;
            }
            if (this.rateValue === this.veryGoodTip) {
                this.rateScore = 5
            } else if (this.rateValue === this.goodTip) {
                this.rateScore = 4
            } else if (this.rateValue === this.averageTip) {
                this.rateScore = 3
            } else if (this.rateValue === this.notGoodTip) {
                this.rateScore = 2
            } else if (this.rateValue === this.badTip) {
                this.rateScore = 1
            }
            $.ajax({
                url: this.HTTP_HOST +
                    "/api/rate/do",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    tid: this.thread.tid,
                    score: this.rateScore,
                    note: this.rateContent,
                    invite: this.isInviteRate,
                    client: this.client
                }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    console.log("rate: ", response);
                    app.isRated = true;
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        upload() {
            // console.log('upload')
            // TODO: 待优化，去掉jquery依赖
            $('input[id=imagefile]').click();
        },
        uploadImage() {
            // console.log('uploadImage')
            var file = $('input[id=imagefile]')[0].files[0];
            // 
            this.uploadImage2(file)
        },
        uploadImage2(file) {
            // console.log('uploadImage2')
            //
            this.compressImage(file, function (newFile) {
                // 
                var formdata = new FormData();
                formdata.append("file_name", app.guid());
                formdata.append("username", app.username);
                formdata.append("file", newFile);
                formdata.append("client", app.client);
                //
                $.ajax({
                    url: app.HTTP_HOST + "/visitor/api/upload/image",
                    contentType: false,
                    cache: false,
                    processData: false,
                    mimeTypes: "multipart/form-data",
                    type: "post",
                    data: formdata,
                    success: function (response) {
                        app.printLog('upload response:' + JSON.stringify(response.data))
                        var imageUrl = response.data;
                        app.sendImageMessage(imageUrl);
                    },
                    error: function (error) {
                        app.printLog(error);
                        // token过期
                        app.login()
                    }
                });
            })
        },
        // 上传并发送文件
        uploadFile(file) {
            // TODO: 先在界面显示文件，并显示上传loading
            //
            var formdata = new FormData();
            formdata.append("file_name", app.guid());
            formdata.append("username", app.username);
            formdata.append("file", file);
            formdata.append("client", app.client);
            //
            $.ajax({
                url: app.HTTP_HOST + "/visitor/api/upload/file",
                contentType: false,
                cache: false,
                processData: false,
                mimeTypes: "multipart/form-data",
                type: "post",
                data: formdata,
                success: function (response) {
                    app.printLog('upload response:' + JSON.stringify(response.data))
                    var fileUrl = response.data;
                    app.sendFileMessage(fileUrl);
                },
                error: function (error) {
                    app.printLog(error);
                }
            });
        },
        compressImage(file, callback) {
            // 判断文件大小
            if (file.size > 1024 * 1024 * 1) {
                app.printLog('图片大于1M进行压缩')
                //
                app.canvasDataURL(file, function (blob) {
                    //
                    var newFile = new File([blob], file.name, {
                        type: file.type
                    })
                    // var isLt1M;
                    // if (file.size < newFile.size) {
                    //     isLt1M = file.size
                    // } else {
                    //     isLt1M = newFile.size
                    // }
                    app.printLog('file.size:' + (file.size / 1024 / 1024))
                    app.printLog('newFile.size: ' + (newFile.size / 1024 / 1024))
                    // 
                    if (file.size < newFile.size) {
                        callback(file)
                    }
                    callback(newFile)
                })
            } else {
                app.printLog('图片小于1M直接上传')
                // 
                return callback(file)
            }
        },
        // 图片压缩方法
        canvasDataURL(file, callback) {
            //
            var reader = new FileReader()
            reader.readAsDataURL(file)
            reader.onload = function (e) {
                const img = new Image()
                const quality = 0.1 // 图像质量
                const canvas = document.createElement('canvas')
                const drawer = canvas.getContext('2d')
                img.src = this.result
                img.onload = function () {
                    canvas.width = img.width
                    canvas.height = img.height
                    drawer.drawImage(img, 0, 0, canvas.width, canvas.height)
                    // 
                    return app.convertBase64UrlToBlob(canvas.toDataURL("image/jpeg", quality), callback);
                }
            }
        },
        // 将以base64的图片url数据转换为Blob
        convertBase64UrlToBlob(urlData, callback) {
            // 
            var bytes = window.atob(urlData.split(',')[1]);        //去掉url的头，并转换为byte  
            //处理异常,将ascii码小于0的转换为大于0  
            var ab = new ArrayBuffer(bytes.length);
            var ia = new Uint8Array(ab);
            for (var i = 0; i < bytes.length; i++) {
                ia[i] = bytes.charCodeAt(i);
            }
            var blob = new Blob([ab], { type: 'image/jpeg' });
            // 
            callback(blob)
        },
        loadHistoryMessagesByTopic(topic) {
            //
            if (this.isRequestAgent || this.isManulRequestThread || this.loadHistory === '0') {
                return;
            }
            $.ajax({
                url: this.HTTP_HOST +
                    "/api/messages/topic",
                type: "get",
                data: {
                    topic: topic,
                    page: this.page,
                    size: 10,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log('loadHistoryMessagesByTopic: ', response);
                    if (response.status_code === 200) {
                        for (let i = 0; i < response.data.content.length; i++) {
                            const message = response.data.content[i]
                            // console.log('message:', message);
                            // app.unshiftToMessageArray(message)
                            if (message.type === 'notification_form_request' ||
                                message.type === 'notification_form_result') {
                                // 暂时忽略表单消息
                            } if (message.type === 'notification_thread_reentry') {
                                // 连续的 ‘继续会话’ 消息，只显示最后一条
                                if (i + 1 < length) {
                                    var nextmsg = response.data.content[i + 1];
                                    if (nextmsg.type === 'notification_thread_reentry') {
                                        continue
                                    } else {
                                        app.unshiftToMessageArray(message)
                                    }
                                }
                            } else {
                                app.unshiftToMessageArray(message)
                            }
                        }
                    } else if (app.loadHistory === '1') {
                        app.pushToMessageArray(app.insertMessage);
                    }
                    app.scrollToBottom()
                    app.$previewRefresh()
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 加载最新10条消息，用于定时拉取最新消息
        loadLatestMessage() {
            //
            let count = app.loadHistory ? 10 : 1
            $.ajax({
                url: this.HTTP_HOST + "/api/messages/topic",
                type: "get",
                data: {
                    topic: app.thread.topic,
                    page: 0,
                    size: count,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log('loadLatestMessage: ', response);
                    if (response.status_code === 200) {
                        for (let i = 0; i < response.data.content.length; i++) {
                            const message = response.data.content[i]
                            // console.log('message:', message);
                            // app.unshiftToMessageArray(message)
                            if (message.type === 'notification_form_request' ||
                                message.type === 'notification_form_result') {
                                // 暂时忽略表单消息
                            } if (message.type === 'notification_thread_reentry') {
                                // 连续的 ‘继续会话’ 消息，只显示最后一条
                                if (i + 1 < length) {
                                    var nextmsg = response.data.content[i + 1];
                                    if (nextmsg.type === 'notification_thread_reentry') {
                                        continue
                                    } else {
                                        app.unshiftToMessageArray(message)
                                    }
                                }
                            } else {
                                app.unshiftToMessageArray(message)
                            }
                        }
                    }
                    app.scrollToBottom()
                    app.$previewRefresh()
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 拉取未读消息
        loadMessagesUnread() {
            //
            if (app.isConnected || app.isRobot) {
                return
            }
            //
            $.ajax({
                url: this.HTTP_HOST + "/api/messages/unread/message/visitor/schedule",
                type: "get",
                data: {
                    page: 0,
                    size: 10,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log('loadMessagesUnread: ', response);
                    //
                    if (response.status_code === 200) {
                        for (let i = 0; i < response.data.content.length; i++) {
                            const message = response.data.content[i]
                            // console.log('message:', message);
                            // app.unshiftToMessageArray(message)
                            if (message.type === 'notification_form_request' ||
                                message.type === 'notification_form_result') {
                                // 暂时忽略表单消息
                            } if (message.type === 'notification_thread_reentry') {
                                // 连续的 ‘继续会话’ 消息，只显示最后一条
                                if (i + 1 < length) {
                                    var nextmsg = response.data.content[i + 1];
                                    if (nextmsg.type === 'notification_thread_reentry') {
                                        continue
                                    } else {
                                        app.unshiftToMessageArray(message)
                                    }
                                }
                            } else {
                                app.unshiftToMessageArray(message)
                            }
                        }
                    }
                    app.scrollToBottom()
                    app.$previewRefresh()
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 请求机器人消息
        appendQueryMessage(content) {
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'robot',
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "text": {
                    "content": content
                },
                "answers": [],
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": content,
                    "nickname": this.thread_nickname(),
                    "avatar": this.thread.visitor.avatar,
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            // 插入本地
            this.onMessageReceived(json)
        },
        // 机器人回复消息
        appendReplyMessage(aid, mid, content) {
            //
            var json = {
                "mid": mid,
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'robot_result',
                "user": {
                    "uid": this.robotUser.uid,
                    "nickname": this.robotUser.nickname,
                    "avatar": this.robotUser.avatar,
                    "extra": {
                        "agent": true
                    }
                },
                "text": {
                    "content": content
                },
                "answer": {
                    "aid": aid
                },
                "answers": [],
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": content,
                    "nickname": this.thread_nickname(),
                    "avatar": this.thread.visitor.avatar,
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            // 插入本地
            this.onMessageReceived(json)
        },
        // 通过aid，请求智能答案
        queryAnswer(answer) {
            // console.log('answer:', answer);
            this.appendQueryMessage(answer.question)
            //
            let mid = this.guid();
            this.appendReplyMessage(answer.aid, mid, answer.answer)

            $.ajax({
                url: this.HTTP_HOST +
                    "/api/v2/answer/query",
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    tid: this.thread.tid,
                    aid: answer.aid,
                    mid: mid,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("query answer success:", response);
                    if (response.status_code === 200) {
                        //
                        // let queryMessage = response.data.query;
                        // let replyMessage = response.data.reply;
                        //
                        // app.pushToMessageArray(queryMessage);
                        // app.pushToMessageArray(replyMessage);
                        // app.scrollToBottom()
                    } else {
                        app.$toast(response.message)
                    }
                },
                error: function (error) {
                    console.log("query answers error:", error);
                    // token过期
                    app.login()
                }
            });
        },
        queryCategory(category) {
            console.log('category:', category);
            this.appendQueryMessage(category.name)
            //
            // let mid = this.guid();
            // this.appendReplyMessage(answer.aid, mid, answer.answer)

            $.ajax({
                url: this.HTTP_HOST + "/api/v2/answer/category",
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    tid: this.thread.tid,
                    cid: category.cid,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("queryCategoryAnswers success:", response);
                    if (response.status_code === 200) {
                        //
                        let replyMessage = response.data.reply;
                        //
                        app.pushToMessageArray(replyMessage);
                        app.scrollToBottom()
                    } else {
                        app.$toast(response.message)
                    }
                },
                error: function (error) {
                    console.log("query answers error:", error);
                    // token过期
                    app.login()
                }
            });
        },
        // 输入内容，请求智能答案
        messageAnswer(content) {
            // 直接在界面显示输入问题
            this.appendQueryMessage(content)
            // 包含’人工‘二字
            if (content.indexOf('人工') !== -1) {
                // 请求人工客服
                app.requestAgent()
                return
            }
            $.ajax({
                url: this.HTTP_HOST +
                    // "/api/v2/answer/message",
                    "/api/elastic/robot/message",
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    wid: this.workGroupWid,
                    content: content,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("message answer success:", response);
                    if (response.status_code === 200 ||
                        response.status_code === 201) {
                        //
                        // let queryMessage = response.data.query;
                        let replyMessage = response.data.reply;
                        replyMessage.type = 'robot_result'; // 返回类型特殊处理一下
                        //
                        // app.pushToMessageArray(queryMessage);
                        app.pushToMessageArray(replyMessage);
                        app.scrollToBottom()
                    } else {
                        app.$toast(response.data.message);
                    }
                },
                error: function (error) {
                    console.log("query answers error:", error);
                    // token过期
                    app.login()
                }
            });
        },
        // 评价机器人答案：有帮助
        rateAnswerHelpful(aid, mid) {
            $.ajax({
                url: this.HTTP_HOST + '/api/answer/rate',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    aid: aid,
                    mid: mid,
                    rate: true,
                    client: this.client
                }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("rateAnswerHelpful: ", response);
                    if (response.status_code === 200) {
                        var message = response.data;
                        app.pushToMessageArray(message);
                        app.scrollToBottom();
                    } else {
                        app.$toast(response.message)
                    }
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 评价机器人答案：无帮助
        rateAnswerHelpless(aid, mid) {
            $.ajax({
                url: this.HTTP_HOST + '/api/answer/rate',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    aid: aid,
                    mid: mid,
                    rate: false,
                    client: this.client
                }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("rateAnswerHelpless: ", response);
                    if (response.status_code === 200) {
                        var message = response.data;
                        app.pushToMessageArray(message);
                        app.scrollToBottom();
                    } else {
                        app.$toast(response.message)
                    }
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 切换
        switchQuickButtonItems() {
            this.showQuickButtonItem = !this.showQuickButtonItem
            if (this.showQuickButtonItem) {
                this.quickButtonArrow = '↓'
            } else {
                this.quickButtonArrow = '↑'
            }
        },
        quickButtonItemClicked(item) {
            // console.log(item)
            if (item.type === 'url') {
                window.open(item.content)
            } else {
                var localId = this.guid();
                var message = {
                    mid: localId,
                    type: 'text',
                    content: item.title,
                    createdAt: this.currentTimestamp(),
                    localId: localId,
                    status: 'stored',
                    user: {
                        uid: this.my_uid(),
                        username: this.my_username(),
                        nickname: this.my_nickname(),
                        avatar: this.my_avatar()
                    }
                };
                this.pushToMessageArray(message);
                //
                var localId2 = this.guid();
                var message2 = {
                    mid: localId2,
                    type: 'text',
                    content: item.content,
                    createdAt: this.currentTimestamp(),
                    localId: localId,
                    status: 'stored',
                    user: {
                        uid: '',
                        username: '',
                        nickname: '系统',
                        avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png'
                    }
                };
                this.pushToMessageArray(message2);
                this.scrollToBottom()
            }
        },
        // 拉取技能组-快捷按钮
        getQuickButtons() {
            //
            if (this.type !== 'workGroup') {
                return
            }
            $.ajax({
                url: this.HTTP_HOST + '/api/quickbutton/query/workGroup',
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    wid: this.workGroupWid,
                    client: this.client
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("getQuickButtons success:", response);
                    if (response.data.length > 0) {
                        app.showQuickButton = true
                    }
                    app.quickButtons = response.data
                },
                error: function (error) {
                    console.log("getQuickButtons error:", error);
                    // token过期
                    app.login()
                }
            });
        },
        // 技能组设置
        getPrechatSettings() {
            //
            if (this.type !== 'workGroup') {
                return
            }
            $.ajax({
                url: this.HTTP_HOST + "/visitor/api/prechat/settings",
                contentType: "application/json; charset=utf-8",
                type: "get",
                data: {
                    wid: this.workGroupWid,
                    client: this.client
                },
                success: function (response) {
                    // console.log("fetch pre setting success:", response);
                    app.showTopTip = response.data.showTopTip
                    app.topTip = response.data.topTip
                    //
                    if (response.data.showForm) {
                        app.showRealname = true
                        app.showMobile = true
                        app.switchForm()
                    }
                },
                error: function (error) {
                    console.log("fetch pre setting error:", error);
                    // token过期
                    app.login()
                }
            });
        },
        // 留言
        leaveMessage() {
            //
            if (this.mobile.trim().length !== 11) {
                this.$toast('手机号错误');
                return
            }
            if (this.content.trim().length === 0) {
                this.$toast('留言内容不能为空');
                return
            }
            // 隐藏留言页面
            this.switchMessage()
            $.ajax({
                url: this.HTTP_HOST + "/api/leavemsg/save",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({
                    wid: this.workGroupWid,
                    aid: this.agentUid,
                    type: this.type,
                    mobile: this.mobile,
                    email: '',
                    content: this.content,
                    client: this.client
                }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // console.log("leave message: ", response);
                    if (response.status_code === 200) {
                        // 留言写到聊天记录
                        app.sendTextMessageSync(app.mobile + ':' + app.content)
                        app.$toast('留言成功');
                    } else {
                        app.$toast(response.message);
                    }
                },
                error: function (error) {
                    console.log(error);
                    app.$toast('留言失败');
                    // token过期
                    app.login()
                }
            });
        },
        // 
        currentTimestamp(time = +new Date()) {
            var date = new Date(time + 8 * 3600 * 1000); // 增加8小时
            return date.toJSON().substr(0, 19).replace('T', ' ');
        },
        guid() {
            function s4() {
                return Math.floor((1 + Math.random()) * 0x10000)
                    .toString(16)
                    .substring(1)
            }
            let timestamp = moment(new Date()).format('YYYYMMDDHHmmss')
            return timestamp + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4()
        },
        cancelForm() {
            this.switchMessage()
        },
        submitForm() {
            // console.log('submit form')
            let formContent = JSON.stringify({
                'form': {
                    'realname': this.realname,
                    'mobile': this.mobile,
                    'email': this.email,
                    'age': this.age,
                    'job': this.job,
                }
            })
            if (this.mobile.length > 0 && this.mobile.length !== 11) {
                app.$toast(this.$t('wrongMobileNum'));
                return;
            }
            if (this.age.length > 0 && isNaN(this.age)) {
                app.$toast(this.$t('ageMustBeNum'));
                return;
            }
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'notification_form_result',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar()
                },
                "form": {
                    "content": formContent
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": "[表单]",
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json);
            // app.stompClient.send("/app/" + this.threadTopic, {},
            //     JSON.stringify(json)
            // );
            //
            this.switchMessage()
            this.showRealname = false
            this.showMobile = false
            this.showEmail = false
            this.showAge = false
            this.showJob = false
        },
        // 发送消息
        sendTextMessageSync(content) {
            //
            content = this.escapeHTML(content);
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'text',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "text": {
                    "content": content
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": content,
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        sendImageMessage(content) {
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'image',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "image": {
                    "imageUrl": content
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": "[图片]",
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        sendFileMessage(content) {
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'file',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "file": {
                    "fileUrl": content
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": "[文件]",
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        sendCommodityMessageSync() {
            let goods = this.getUrlParam("goods")
            if (goods !== "1") {
                return
            }
            //
            let jsonContent = this.commodityInfo();
            // 发送商品信息
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'commodity',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "text": {
                    "content": jsonContent
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": "[商品]",
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        appendCommodityInfo() {
            let goods = this.getUrlParam("goods")
            if (goods !== "1") {
                return
            }
            let jsonContent = this.commodityInfo();
            // 发送商品信息
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'commodity',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "content": jsonContent,
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": "[商品]",
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            app.pushToMessageArray(json)
        },
        commodityInfo() {
            //
            let commodidy = {
                "id": this.getUrlParam("goods_id"),
                "title": this.getUrlParam("goods_title"),
                "content": this.getUrlParam("goods_content"),
                "price": this.getUrlParam("goods_price"),
                "url": this.getUrlParam("goods_url"),
                "imageUrl": this.getUrlParam("goods_imageUrl"),
                "categoryCode": this.getUrlParam("goods_categoryCode"),
                "type": "commodity"
            }
            return JSON.stringify(commodidy)
        },
        // 
        sendAnswerMessage(sdp) {
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'notification_webrtc_answer',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "extra": {
                    "content": JSON.stringify(sdp)
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": 'answer',
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)

        },
        sendCandidateMessage(candidate) {
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": 'notification_webrtc_candidate',
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "extra": {
                        "agent": false
                    }
                },
                "extra": {
                    "content": JSON.stringify(candidate)
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": 'candidate',
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        // 必须添加前缀 '/topic/'
        subscribeTopic(topic) {
            console.log('subscribeTopic:', topic)
            // 防止重复订阅
            if (this.subscribedTopics.includes(topic)) {
                return;
            }
            this.subscribedTopics.push(topic);
            //
            this.stompClient.subscribe('/topic/' + topic, function (message) {
                // console.log('message :', message, 'body:', message.body);
                var messageObject = JSON.parse(message.body);
                app.onMessageReceived(messageObject);
            });
        },
        onMessageReceived(messageObject) {
            console.log('received:', JSON.stringify(messageObject))
            // 
            if (messageObject.type === 'notification_webrtc_ready') {
                console.log('notification_webrtc_ready')
                return
            } else if (messageObject.type === 'notification_webrtc_busy') {
                console.log('notification_webrtc_busy')
                this.$toast('客服忙，请稍后再试');
                this.hangupAction()
                return
            } else if (messageObject.type === 'notification_webrtc_close') {
                console.log('notification_webrtc_close')
                this.$toast('客服关闭视频会话');
                this.hangupAction()
                return
            } else if (messageObject.type === 'notification_webrtc_offer_video') {
                console.log('notification_webrtc_offer_video')
                if (messageObject.user.uid !== app.uid) {
                    let sessionDescription = JSON.parse(messageObject.extra.content);
                    this.onReceiveSdp(sessionDescription)
                }
                return
            } else if (messageObject.type === 'notification_webrtc_offer_audio') {
                console.log('notification_webrtc_offer_audio')
                return
            } else if (messageObject.type === 'notification_webrtc_answer') {
                console.log('notification_webrtc_answer')
                return
            } else if (messageObject.type === 'notification_webrtc_candidate') {
                console.log('notification_webrtc_candidate')
                // 接收并处理对方的candidate信息
                if (messageObject.user.uid !== app.uid) {
                    let candidate = JSON.parse(messageObject.extra.content)
                    if (candidate !== null && candidate !== undefined) {
                        // const newIceCandidate = new RTCIceCandidate(candidate);
                        let newIceCandidate = { candidate: candidate.sdp, sdpMLineIndex: candidate.sdpMLineIndex, sdpMid: candidate.sdpMid }
                        if (this.localPeerConnection && this.localPeerConnection.remoteDescription) {
                            this.localPeerConnection.addIceCandidate(newIceCandidate)
                                .then(() => {
                                    console.log('addIceCandidate success')
                                }).catch((error) => {
                                    console.log('addIceCandidate:', error)
                                });
                        }
                    }
                }
                return
            } else if (messageObject.type === 'notification_webrtc_accept') {
                console.log('notification_webrtc_accept')
                return
            } else if (messageObject.type === 'notification_webrtc_reject') {
                console.log('notification_webrtc_reject')
                this.$toast('客服拒绝视频，请稍后再试');
                this.hangupAction()
                return
            }
            
            // 
            if ((messageObject.type === 'text'
                || messageObject.type === 'robot'
                || messageObject.type === 'robot_result'
                || messageObject.type === 'image'
                || messageObject.type === 'file'
                || messageObject.type === 'voice'
                || messageObject.type === 'video'
                || messageObject.type === 'commodity')
            ) {
                // 新protobuf转换json
                messageObject.createdAt = messageObject.timestamp;
                if (messageObject.type === "text") {
                    messageObject.content = messageObject.text.content;
                } else if (messageObject.type === "robot") {
                    messageObject.content = messageObject.text.content;
                } else if (messageObject.type === "robot_result") {
                    messageObject.content = messageObject.text.content;
                } else if (messageObject.type === "image") {
                    messageObject.imageUrl = messageObject.image.imageUrl;
                } else if (messageObject.type === "file") {
                    messageObject.fileUrl = messageObject.file.fileUrl;
                } else if (messageObject.type === "voice") {
                    messageObject.voiceUrl = messageObject.voice.voiceUrl;
                    messageObject.length = messageObject.voice.length;
                } else if (messageObject.type === "video") {
                    messageObject.videoOrShortUrl = messageObject.video.videoOrShortUrl;
                } else if (messageObject.type === "commodity") {
                    messageObject.content = messageObject.text.content;
                }
                //
                let mid = messageObject.mid;
                // 非自己发送的消息，发送消息回执
                if (messageObject.user.uid !== app.uid && messageObject.type != 'robot' && messageObject.type !== "robot_result") {
                    app.sendReceiptMessage(mid, "read");
                } else {
                    // 自己发送的消息，更新消息发送状态
                    for (let i = app.messages.length - 1; i >= 0; i--) {
                        const msg = app.messages[i]
                        if (msg.mid === mid) {
                            // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
                            if (app.messages[i].status === 'sending') {
                                Vue.set(app.messages[i], 'status', 'stored')
                            }
                            break
                        }
                    }
                }
            } else if (messageObject.type === 'notification_browse_invite') {
                //
            } else if (messageObject.type === 'notification_queue') {
                // 排队
                app.isThreadClosed = false
                // 是否正在排队
                app.isQueuing = true
            } else if (messageObject.type === 'notification_queue_accept') {
                // 接入访客
                messageObject.createdAt = messageObject.timestamp;
                messageObject.content = messageObject.text.content;
                // 1. 保存thread
                // app.thread = messageObject.thread;
                // 2. 订阅会话消息
                // app.subscribeTopic(app.threadTopic);
                app.isThreadClosed = false
                // 是否正在排队
                app.isQueuing = false
            } else if (messageObject.type === 'notification_invite_rate') {
                // 邀请评价
                messageObject.createdAt = messageObject.timestamp;
                messageObject.content = messageObject.extra.content;
                app.isInviteRate = true;
                app.switchRate()
            } else if (messageObject.type === 'notification_rate_result') {
                // 访客评价结果
                messageObject.createdAt = messageObject.timestamp;
                messageObject.content = messageObject.extra.content;
            } else if (messageObject.type === 'notification_agent_close'
                || messageObject.type === 'notification_auto_close') {
                // 新protobuf转换json
                messageObject.createdAt = messageObject.timestamp;
                messageObject.content = messageObject.text.content;
                // TODO: 会话关闭，添加按钮方便用户点击重新请求会话
                app.isThreadClosed = true
            } else if (messageObject.type === 'notification_preview') {
                //
                if (messageObject.user.uid !== app.uid) {
                    app.isInputingVisible = true;
                    setTimeout(function () {
                        app.isInputingVisible = false;
                    }, 5000)
                }
            } else if (messageObject.type === 'notification_receipt') {
                // 消息状态：送达 received、已读 read
                if (messageObject.user.uid !== app.uid) {
                    for (let i = app.messages.length - 1; i >= 0; i--) {
                        const msg = app.messages[i]
                        if (msg.mid === messageObject.receipt.mid) {
                            // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
                            if (app.messages[i].status === 'read') {
                                return
                            }
                            Vue.set(app.messages[i], 'status', messageObject.receipt.status)
                        }
                    }
                }
            } else if (messageObject.type === 'notification_recall') {
                for (let i = 0; i < app.messages.length; i++) {
                    const element = app.messages[i];
                    if (element.mid === messageObject.recall.mid) {
                        app.messages.splice(i, 1)
                    }
                }
            } else if (messageObject.type === 'notification_form_request') {
                // 收到客服端表单请求
                messageObject.content = '表单请求'
                let formContent = messageObject.extra.content
                console.log('form:' + formContent)
                // let formContentObject = JSON.parse(formContent)
                if (formContent.indexOf('姓名') !== -1) {
                    console.log('showRealname')
                    app.showRealname = true
                }
                if (formContent.indexOf('手机') !== -1) {
                    app.showMobile = true
                }
                if (formContent.indexOf('邮箱') !== -1) {
                    app.showEmail = true
                }
                if (formContent.indexOf('年龄') !== -1) {
                    app.showAge = true
                }
                if (formContent.indexOf('职业') !== -1) {
                    app.showJob = true
                }
                app.switchForm()
            } else if (messageObject.type === 'notification_form_result') {
                // 自己发送的表单结果
                messageObject.content = '发送表单'
            }
            // 
            if (messageObject.type !== 'notification_preview'
                && messageObject.type !== 'notification_receipt'
                && messageObject.type !== 'notification_recall'
                && messageObject.type !== 'notification_form_request'
                && messageObject.type !== 'notification_form_result'
                && messageObject.type !== 'notification_connect'
                && messageObject.type !== 'notification_disconnect') {
                // app.isRobot = false;
                // 默认不显示附言
                if (messageObject.type === "text") {
                    if ((messageObject.content != null && !messageObject.content.startsWith(app.postScriptPrefixTip)) || app.showScript) {
                        app.pushToMessageArray(messageObject);
                    }
                } else {
                    app.pushToMessageArray(messageObject);
                }
                app.scrollToBottom()
            }
        },
        // 输入框变化
        onInputChange(content) {
            // console.log(content);
            if (this.isRobot || this.isThreadClosed) {
                return;
            }
            this.localPreviewContent = content
            this.delaySendPreviewMessage()
        },
        sendPreviewMessage() {
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": "notification_preview",
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar()
                },
                "preview": {
                    "content": this.localPreviewContent === undefined ? " " : this.localPreviewContent
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "content": this.localPreviewContent,
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "client": this.client,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        // 发送消息
        onKeyUp(e) {
            if (e.keyCode === 13 && this.inputContent.trim().length > 0) {
                this.inputContent = this.inputContent.trim();
                this.sendTextMessage()
            }
        },
        sendTextMessage() {
            // 内容为空，则直接返回
            if (this.inputContent.trim().length === 0) {
                app.$toast(this.$t('contentMustNotNull'));
                return;
            }
            // 长度不能超过500
            if (this.inputContent.trim().length > 500) {
                app.$toast(this.$t('contentTooLong'));
                return;
            }
            //
            if (this.isRobot) {
                // 请求机器人问答
                this.messageAnswer(this.inputContent);
            } else if (app.isQueuing) {
                app.$toast(this.$t('queuing'));
                return;
            } else {
                // 发送/广播会话消息
                this.sendTextMessageSync(this.inputContent)
            }
            // 清空输入框
            this.inputContent = "";
            // 设置焦点
            setTimeout(function () {
                $("input")[1].focus()
            }, 100);
        },
        // 消息回执：收到消息之后回复给消息发送方 消息content字段存放status: 1. received, 2. read
        sendReceiptMessage(mid, status) {
            //
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": "notification_receipt",
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar()
                },
                "receipt": {
                    "mid": mid,
                    "status": status
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        // 消息撤回
        sendRecallMessage(mid) {
            var json = {
                "mid": this.guid(),
                "timestamp": this.currentTimestamp(),
                "client": this.client,
                "version": "1",
                "type": "notification_recall",
                "status": "sending",
                "user": {
                    "uid": this.my_uid(),
                    "username": this.my_username(),
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar()
                },
                "recall": {
                    "mid": mid
                },
                "thread": {
                    "tid": this.thread.tid,
                    "type": this.thread.type,
                    // "content": content,
                    "nickname": this.my_nickname(),
                    "avatar": this.my_avatar(),
                    "topic": this.threadTopic,
                    "timestamp": this.currentTimestamp(),
                    "unreadCount": 0
                }
            };
            this.doSendMessage(json)
        },
        // 重新发送
        sendMessageJsonRest(mid, type, content) {
            //
            var json;
            if (type === 'text') {
                json = {
                    "mid": mid,
                    "timestamp": this.currentTimestamp(),
                    "client": this.client,
                    "version": "1",
                    "type": type,
                    "status": "sending",
                    "user": {
                        "uid": this.my_uid(),
                        "username": this.my_username(),
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "extra": {
                            "agent": false
                        }
                    },
                    "text": {
                        "content": content
                    },
                    "thread": {
                        "tid": this.thread.tid,
                        "type": this.thread.type,
                        "content": content,
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "topic": this.threadTopic,
                        "client": this.client,
                        "timestamp": this.currentTimestamp(),
                        "unreadCount": 0
                    }
                };
            } else if (type === 'image') {
                json = {
                    "mid": mid,
                    "timestamp": this.currentTimestamp(),
                    "client": this.client,
                    "version": "1",
                    "type": type,
                    "status": "sending",
                    "user": {
                        "uid": this.my_uid(),
                        "username": this.my_username(),
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "extra": {
                            "agent": false
                        }
                    },
                    "image": {
                        "imageUrl": content
                    },
                    "thread": {
                        "tid": this.thread.tid,
                        "type": this.thread.type,
                        "content": '图片',
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "topic": this.threadTopic,
                        "client": this.client,
                        "timestamp": this.currentTimestamp(),
                        "unreadCount": 0
                    }
                };
            } else if (type === 'file') {
                json = {
                    "mid": mid,
                    "timestamp": this.currentTimestamp(),
                    "client": this.client,
                    "version": "1",
                    "type": type,
                    "status": "sending",
                    "user": {
                        "uid": this.my_uid(),
                        "username": this.my_username(),
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "extra": {
                            "agent": false
                        }
                    },
                    "file": {
                        "fileUrl": content
                    },
                    "thread": {
                        "tid": this.thread.tid,
                        "type": this.thread.type,
                        "content": '[文件]',
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "topic": this.threadTopic,
                        "client": this.client,
                        "timestamp": this.currentTimestamp(),
                        "unreadCount": 0
                    }
                };
            } else if (type === 'voice') {
                json = {
                    "mid": mid,
                    "timestamp": this.currentTimestamp(),
                    "client": this.client,
                    "version": "1",
                    "type": type,
                    "status": "sending",
                    "user": {
                        "uid": this.my_uid(),
                        "username": this.my_username(),
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "extra": {
                            "agent": false
                        }
                    },
                    "voice": {
                        "voiceUrl": content,
                        "length": '0', // TODO:替换为真实值
                        "format": 'wav',
                    },
                    "thread": {
                        "tid": this.thread.tid,
                        "type": this.thread.type,
                        "content": '[语音]',
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "topic": this.threadTopic,
                        "client": this.client,
                        "timestamp": this.currentTimestamp(),
                        "unreadCount": 0
                    }
                };
            } else if (type === 'video') {
                json = {
                    "mid": mid,
                    "timestamp": this.currentTimestamp(),
                    "client": this.client,
                    "version": "1",
                    "type": type,
                    "status": "sending",
                    "user": {
                        "uid": this.my_uid(),
                        "username": this.my_username(),
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "extra": {
                            "agent": false
                        }
                    },
                    "video": {
                        "videoOrShortUrl": content
                    },
                    "thread": {
                        "tid": this.thread.tid,
                        "type": this.thread.type,
                        "content": '[视频]',
                        "nickname": this.my_nickname(),
                        "avatar": this.my_avatar(),
                        "topic": this.threadTopic,
                        "client": this.client,
                        "timestamp": this.currentTimestamp(),
                        "unreadCount": 0
                    }
                };
            }
            this.sendMessageRest2(mid, JSON.stringify(json))
        },
        // 发送消息
        doSendMessage(jsonObject) {
            //
            if (app.isConnected) {
                // 发送长连接消息
                app.stompClient.send("/app/" + this.threadTopic, {},
                    JSON.stringify(jsonObject)
                );
            } else {
                // 调用rest接口发送消息
                app.sendMessageRest(JSON.stringify(jsonObject))
            }
            // 先插入本地
            this.onMessageReceived(jsonObject)
        },
        // 在长连接断开的情况下，发送消息
        sendMessageRest(json) {
            $.ajax({
                url: this.HTTP_HOST + "/api/messages/send",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({ json: json, }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // 更新消息发送状态
                    // console.log("send rest message: ", response.data);
                    let message = JSON.parse(response.data)
                    for (let i = app.messages.length - 1; i >= 0; i--) {
                        const msg = app.messages[i]
                        // console.log('mid:', msg.mid, message.mid)
                        if (msg.mid === message.mid) {
                            // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
                            if (app.messages[i].status === 'read' ||
                                app.messages[i].status === 'received') {
                                return
                            }
                            Vue.set(app.messages[i], 'status', 'stored')
                            return;
                        }
                    }
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        sendMessageRest2(mid, json) {
            $.ajax({
                url: this.HTTP_HOST + "/api/messages/send",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "post",
                data: JSON.stringify({ json: json, }),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
                },
                success: function (response) {
                    // 更新消息发送状态
                    for (let i = app.messages.length - 1; i >= 0; i--) {
                        const msg = app.messages[i]
                        // console.log('mid:', msg.mid, message.mid)
                        if (msg.mid === mid) {
                            // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
                            if (app.messages[i].status === 'read' ||
                                app.messages[i].status === 'received') {
                                return
                            }
                            Vue.set(app.messages[i], 'status', 'stored')
                            return;
                        }
                    }
                },
                error: function (error) {
                    console.log(error);
                    // token过期
                    app.login()
                }
            });
        },
        // 重新发送
        resendButtonClicked(message) {
            // 5秒没有发送成功，则尝试使用http rest接口发送
            let content = ''
            if (message.type === 'text') {
                content = message.content
            } else if (message.type === 'image') {
                content = message.imageUrl
            } else if (message.type === 'file') {
                content = message.fileUrl
            } else if (message.type === 'voice') {
                content = message.voiceUrl
            } else if (message.type === 'video') {
                content = message.videoOrShortUrl
            }
            this.sendMessageJsonRest(message.mid, message.type, content)
        },
        // 消息撤回
        recallButtonClicked(message) {
            this.$messagebox.confirm('确定要撤回消息?').then(action => {
                console.log('撤回:', action)
                if (action === 'confirm') {
                    app.sendRecallMessage(message.mid)
                }
            });
        },
        // 检测-消息是否超时发送失败
        checkTimeoutMessage() {
            for (let i = 0; i < this.messages.length; i++) {
                const message = this.messages[i];
                if (this.is_self(message) && this.is_sending(message)) {
                    let timestamp = moment(message.createdAt);
                    let now = moment(new Date());
                    let diff = now.diff(timestamp, "seconds");
                    // console.log('diff:', diff)
                    if (diff > 15) {
                        // 超时15秒，设置为消息状态为error
                        // this.messages[i].status = 'error'
                        Vue.set(this.messages[i], 'status', 'error')
                    } else if (diff > 3) {
                        // 5秒没有发送成功，则尝试使用http rest接口发送
                        this.resendButtonClicked(message)
                    }
                }
            }
        },
        //
        byteDeskConnect() {
            var socket = new SockJS(this.STOMP_HOST + '/stomp/?access_token=' + localStorage.bd_kfe_access_token);
            this.stompClient = Stomp.over(socket);
            this.stompClient.reconnect_delay = 1000;
            // client will send heartbeats every 10000ms, default 10000
            this.stompClient.heartbeat.outgoing = 20000;
            // client does not want to receive heartbeats from the server, default 10000
            this.stompClient.heartbeat.incoming = 20000;
            // 上线时打开下面注释，to disable logging, set it to an empty function:
            if (this.IS_PRODUCTION) {
                this.stompClient.debug = function (value) { }
            }
            // 连接bytedesk，如果后台开启了登录，需要登录之后才行
            this.stompClient.connect({}, function (frame) {
                // console.log('stompConnected: ' + frame + " username：" + frame.headers['user-name']);
                app.isConnected = true;
                // 获取 websocket 连接的 sessionId
                // FIXME: Uncaught TypeError: Cannot read property '1' of null
                // app.sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
                // console.log("connected, session id: " + app.sessionId);
                // 订阅会话消息，处理断开重连的情况
                if (app.thread.tid !== null && app.thread.tid !== undefined && app.thread.tid !== '') {
                    app.subscribeTopic(app.threadTopic);
                } else {
                    console.log('thread.tid:', app.thread.tid)
                }
                // 发送附言
                // if (app.postscript !== null && app.postscript !== undefined && app.postscript !== '') {
                //     let postcontent = app.postScriptPrefixTip + app.postscript
                //     app.sendTextMessageSync(postcontent)
                // }
                // 技能组设置
                // app.getPrechatSettings();
                // 接受通知
                // app.subscribeQueue('notification');
                // 订阅错误消息
                // app.subscribeQueue('errors');
            }, function (error) {
                console.log('连接断开【' + error + '】');
                app.isConnected = false;
                // 为断开重连做准备
                app.subscribedTopics = [];
                // 10秒后重新连接，实际效果：每10秒重连一次，直到连接成功
                setTimeout(function () {
                    console.log("reconnecting...");
                    app.byteDeskConnect();
                }, 5000);
            })
        },
        // 打印log
        printLog(content) {
            if (!this.IS_PRODUCTION) {
                const now = (window.performance.now() / 1000).toFixed(3);
                console.log(now, content)
            }
        },
        // 拖拽上传初始化
        initDragUpload() {
            // 拖拽上传发送文件
            var oDragWrap = document.body;
            //拖进
            oDragWrap.addEventListener(
                "dragenter",
                function (e) {
                    e.preventDefault();
                    // console.log('dragenter:')
                    // console.log(e)
                },
                false
            );
            //拖离
            oDragWrap.addEventListener(
                "dragleave",
                function (e) {
                    e.preventDefault();
                    // dragleaveHandler(e);
                    // console.log('dragleave:')
                    // console.log(e)
                },
                false
            );
            //拖来拖去 , 一定要注意dragover事件一定要清除默认事件
            //不然会无法触发后面的drop事件
            oDragWrap.addEventListener(
                "dragover",
                function (e) {
                    e.preventDefault();
                    // console.log('dragover:')
                    // console.log(e)
                },
                false
            );
            //扔
            oDragWrap.addEventListener(
                "drop",
                function (e) {
                    dropHandler(e);
                    // console.log('drop:')
                    // console.log(e)
                },
                false
            );
            var dropHandler = function (e) {
                // 将本地图片拖拽到页面中后要进行的处理都在这
                e.preventDefault(); //获取文件列表
                // console.log('dropHandler:')
                // console.log(e)
                // 
                var fileList = e.dataTransfer.files;
                // 检测是否是拖拽文件到页面的操作
                if (fileList.length == 0) {
                    return;
                }
                // 检测文件是不是图片
                if (fileList[0].type.indexOf("image") === -1) {
                    return;
                }
                // 上传文件，并发送
                let file = fileList[0]
                if (/\.(gif|jpg|jpeg|png|webp|GIF|JPG|PNG|WEBP)$/.test(file.name)) {
                    // 发送图片
                    app.uploadImage2(file)
                    return
                }
                // 发送其他文件
                app.uploadFile(file)
            };
        },
        //////////////////////////////////////////

        initLocalStream() {
            console.log('initLocalStream')
            //
            let app = this
            this.localVideo = document.getElementById('localVideo')
            this.localVideo.addEventListener('loadedmetadata', event => {
                const video = event.target
                console.log(`loadedmetadata 本地视频窗口 ${video.id} videoWidth: ${video.videoWidth}px, ` + `videoHeight: ${video.videoHeight}px.`)
                // app.sendReadyMessage()
            })
            //
            navigator.mediaDevices.getUserMedia(app.videoStreamConstraints).then(mediaStream => {
                app.localStream = mediaStream
                app.localVideo.srcObject = mediaStream
                // 普通浏览器不需要调用play(),微信浏览器需要手动播放？
                app.localVideo.play()
                // Get local media stream tracks.
                // const videoTracks = app.localStream.getVideoTracks();
                // const audioTracks = app.localStream.getAudioTracks();
                // for (var i = 0; i < videoTracks.length; i++) {
                //     printLog(`Using video device: ${videoTracks[i].label}.`);
                // }
                // for (var i = 0; i < audioTracks.length; i++) {
                //     printLog(`Using audio device: ${audioTracks[0].label}.`);
                // }
                // 创建连接，并发送ice candidate信息
                app.createPeerConnection()
                // Added local stream to localPeerConnection
                // app.localPeerConnection.addStream(mediaStream)
            }).catch(error => {
                console.log('getUserMedia() error: ' + error)
            })

        },
        initRemoteStream() {
            //
            this.remoteVideo = document.getElementById('remoteVideo')
            this.remoteVideo.addEventListener('loadedmetadata', event => {
                const video = event.target
                console.log(`loadedmetadata 远程视频窗口 ${video.id} videoWidth: ${video.videoWidth}px, ` + `videoHeight: ${video.videoHeight}px.`)
            })
            this.remoteVideo.addEventListener('onresize', event => {
                // This event is fired when video begins streaming.
                const video = event.target
                console.log(`接通远程视频 remote resize ${video.id} videoWidth: ${video.videoWidth}px, ` + `videoHeight: ${video.videoHeight}px.`)
            })
        },
        createPeerConnection() {
            console.log('createPeerConnection')
            try {
                let app = this
                const servers = {
                    iceServers: [{
                        urls: "turn:turn.bytedesk.com:3478",
                        username: "jackning",
                        credential: "kX1JiyPGVTtO3y0o"
                    }]
                };
                this.localPeerConnection = new RTCPeerConnection(servers)
                // 调用createOffer，设置setLocalDescription之后才会调用onicecandidate
                // The PeerConnection won't start gathering candidates until you call setLocalDescription();
                this.localPeerConnection.onicecandidate = function (event) {
                    // this.localPeerConnection.addEventListener('icecandidate', event => {
                    console.log('onicecandidate event: ', event)
                    // const peerConnection = event.target
                    // const iceCandidate = event.candidate
                    // app.rtcIceCandidate = event.candidate
                    // app.isRtcStarted = true
                    // 发送自己candidate信息给对方
                    if (event.candidate !== null && event.candidate !== undefined) {
                        let candidateObject = { sdp: event.candidate.candidate, sdpMLineIndex: event.candidate.sdpMLineIndex, sdpMid: event.candidate.sdpMid }
                        // console.log('candidateObject:', candidateObject)
                        app.sendCandidateMessage(candidateObject)
                    }
                }
                this.localPeerConnection.oniceconnectionstatechange = function (event) {
                    console.log('iceconnectionstatechange: ', event)
                }
                this.localPeerConnection.onaddstream = function (event) {
                    console.log('Remote stream added.')
                    app.remoteVideo.srcObject = event.stream
                    // 普通浏览器不需要调用play(),微信浏览器需要手动播放？
                    app.remoteVideo.play()
                }
                this.localPeerConnection.onremovestream = function (event) {
                    console.log('Remote stream removed. Event: ', event)
                }
                console.log('Created RTCPeerConnnection')
                this.localPeerConnection.addStream(this.localStream)
                // 
            } catch (error) {
                console.log('Failed to create PeerConnection, exception: ' + error.message)
            }
        },
        onReceiveSdp(sessionDescription) {
            let app = this
            this.printLog('localPeerConnection setRemoteDescription start.');
            this.localPeerConnection.setRemoteDescription(sessionDescription)
                .then(() => {
                    //
                }).catch(app.setSessionDescriptionError);
        
            this.printLog('localPeerConnection createAnswer start.');
            this.localPeerConnection.createAnswer()
                .then(app.createdAnswer)
                .catch(app.setSessionDescriptionError);
        },
        // Logs answer to offer creation and sets peer connection session descriptions.
        createdAnswer(description) {
            this.printLog(`Answer from localPeerConnection:\n${description.sdp}.`);
            this.printLog('localPeerConnection setLocalDescription start.');
            let app = this
            this.localPeerConnection.setLocalDescription(description)
                .then(() => {
                    app.sendAnswerMessage(description);
                }).catch(app.setSessionDescriptionError);
        },
        // Handles hangup action: ends up call, closes connections and resets peers.
        hangupAction() {
            // 
            if (this.localStream) {
                this.localStream.getAudioTracks()[0].stop()
                this.localStream.getVideoTracks()[0].stop()
            }
            // this.localVideo = null;
            // this.remoteVideo = null;
            if (this.localPeerConnection) {
                this.localPeerConnection.close();
                this.localPeerConnection = null;
            }
            // 
            this.printLog('Ending call.');
        },
        // 
        initVideo() {
            this.initLocalStream()
            this.initRemoteStream()
        }
    },
    directives: {
        focus: {
            // When the bound element is inserted into the DOM...
            inserted: function (el) {
                el.focus()
            }
        }
    },
    created() {
        // console.log("created:", localStorage.iframe);
        // this.adminUid = this.getUrlParam("uid");
        this.workGroupWid = this.getUrlParam("wid");
        this.subDomain = this.getUrlParam("sub");
        this.type = this.getUrlParam("type");
        this.thread.type = this.type.toLowerCase();
        //
        this.agentUid = this.getUrlParam("aid");
        this.nickname = this.getUrlParam("nickname") === null ? '' : this.getUrlParam("nickname");
        this.avatar = this.getUrlParam("avatar") === null ? '' : this.getUrlParam("avatar");
        this.postscript = this.getUrlParam("postscript");
        this.showScript = this.getUrlParam("showScript") === '1' ? true : false;
        this.hideNav = this.getUrlParam("hidenav") === '1' ? true : false;
        this.backUrl = (this.getUrlParam("backurl") === null || this.getUrlParam("backurl") === '') ? document.referrer : this.getUrlParam("backurl");
        this.lang = this.getUrlParam("lang") === null ? 'cn' : this.getUrlParam("lang");
        // this.v2robot = this.getUrlParam("v2robot") === null ? '0' : this.getUrlParam("v2robot");
        this.$i18n.locale = this.lang
        // 
        this.selfuser = this.getUrlParam("selfuser");
        if (this.selfuser === "1") {
            // 之前未启用自定义用户名，初次启用自定义用户名
            if (localStorage.bd_kfe_selfuser !== "1") {
                // 初次启用自定义用户名
                localStorage.bd_kfe_selfuser = "1";
                // 
                this.registerUser()
            } else {
                // 之前已经启用了自定义用户名
                //
                this.access_token = localStorage.bd_kfe_access_token
                // console.log('access_token:', this.access_token)
                this.uid = localStorage.bd_kfe_uid;
                this.username = localStorage.bd_kfe_username;
                this.password = this.username;
                //
                var username = this.getUrlParam("username");
                if (this.username === username) {
                    // 同一个用户，直接调用登录流程
                    // console.log("mount");
                    if (this.access_token !== null
                        && this.access_token !== undefined
                        && this.access_token !== '') {
                        //
                        this.requestThread()
                    } else if (this.username !== null
                        && this.username !== undefined
                        && this.username !== '') {
                        // 暂未考虑自定义用户名情况
                        this.login()
                    }
                } else {
                    // 切换了用户名，重新调用注册流程
                    this.registerUser();
                }
            }
        } else {
            // 未启用自定义用户名
            if (localStorage.bd_kfe_selfuser === "1") {
                // 之前使用自定义用户名
                // 标记使用非自定义用户名
                localStorage.bd_kfe_selfuser = "0"; // 注意：不能提取到if之前
                // 新注册匿名用户
                this.requestUsername();
            } else {
                // 一直使用非自定义用户名
                // 标记使用非自定义用户名
                localStorage.bd_kfe_selfuser = "0"; // 注意：不能提取到if之前
                //
                this.access_token = localStorage.bd_kfe_access_token
                // console.log('access_token:', this.access_token)
                this.uid = localStorage.bd_kfe_uid;
                this.username = localStorage.bd_kfe_username;
                this.password = this.username;
                // console.log("mount");
                if (this.access_token !== null
                    && this.access_token !== undefined
                    && this.access_token !== '') {
                    //
                    this.requestThread()
                } else if (this.username !== null
                    && this.username !== undefined
                    && this.username !== '') {
                    // 暂未考虑自定义用户名情况
                    this.login()
                }
                else {
                    this.requestUsername();
                }
            }
        }
    },
    mounted() {
        // 使ie支持startsWith
        if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) {
                position = position || 0;
                return this.indexOf(searchString, position) === position;
            };
        }
        // 使ie支持includes
        if (!String.prototype.includes) {
            String.prototype.includes = function (str) {
                var returnValue = false;
                if (this.indexOf(str) !== -1) {
                    returnValue = true;
                }
                return returnValue;
            };
        }
        // 使ie支持endsWith
        if (!String.prototype.endsWith) {
            String.prototype.endsWith = function (suffix) {
                return this.indexOf(suffix, this.length - suffix.length) !== -1;
            };
        }
        // 防止长连接断开，则定时刷新聊天记录
        // this.loadHistoryTimer = setInterval(this.loadLatestMessage, 1000 * 10);
        // TODO: 智能调节时长，如果长时间没有未读消息，则拉取时间间隔逐渐加长
        this.loadHistoryTimer = setInterval(this.loadMessagesUnread, 1000 * 5);
        this.sendMessageTimer = setInterval(this.checkTimeoutMessage, 1000);
        // console.log('h5 websiteUrl:', window.location.href, 'h5 refererUrl:', document.referrer)
        // 初始化拖拽上传
        this.initDragUpload()
        //
        let app = this
        document.addEventListener("WeixinJSBridgeReady", function () {
            console.log('WeixinJSBridgeReady')
            app.isWeixinBrowser = true
        }, false);
        // 
        this.initVideo()
    },
    beforeDestroy() {
        clearInterval(this.loadHistoryTimer);
        clearInterval(this.sendMessageTimer);
    }
});
