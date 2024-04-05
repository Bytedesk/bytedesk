var bot = new ChatSDK({
    config: {
        // lang: 'en-US',
        // lang: 'zh-CN',
        navbar: {
            // logo: 'https://gw.alicdn.com/tfs/TB1Wbldh7L0gK0jSZFxXXXWHVXa-168-33.svg',
            title: '浙江政务服务网',
        },
        // 转人工 https://chatui.io/sdk/agent
        agent: {
            quickReply: {
                icon: 'message',
                name: '召唤在线客服',
                isHighlight: true,
            },
        },
        // brand: {},
        // card: {
        //     code: 'sidebar-suggestion'
        // },
        // 头像白名单
        avatarWhiteList: ['knowledge', 'recommend'],
        // 机器人信息
        robot: {
            avatar:
                'https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg',
        },
        toolbar: [
            {
                type: "plus", // 类型
                icon: "plus", // 图标（svg），与下面的 img 二选一即可
                // img: "", // 图片（img），推荐用 56x56 的图，会覆盖 icon
                title: "plus title", // 名称
            },
        ],
        // 首屏消息
        messages: [
            {
                type: 'system',
                content: {
                    text: '智能助理进入对话，为您服务',
                },
            },
            {
                type: 'text',
                content: {
                    text: '浙江智能助理为您服务，请问有什么可以帮您？',
                },
            },
            {
                type: 'card',
                content: {
                    code: 'switch-location',
                },
            },
        ],
        // 快捷短语 https://chatui.io/sdk/quick-replies
        quickReplies: [
            {
                name: '发送文本',
                isHighlight: true,
            },
            {
                name: '可见文本',
                type: 'text',
                text: '实际发送的文本',
                isNew: true,
            },
            {
                name: '点击跳转',
                type: 'url',
                url: 'https://www.taobao.com/',
            },
            {
                name: '唤起卡片',
                type: 'card',
                card: { code: 'knowledge', data: {} },
            },
            {
                name: '人工客服',
                type: 'cmd',
                cmd: { code: 'agent_join' },
            },
        ],
        // 输入框占位符
        placeholder: '输入任何您想办理的服务',
        // 自定义卡片模板 https://chatui.io/sdk/custom-component
        components: {
            // 这样注册 会有一个隐私的转化 adaptable-action-card 会到全局上 取 AlimeComponentAdaptableActionCard 对象
            // 'adaptable-action-card': '//g.alicdn.com/alime-components/adaptable-action-card/0.1.7/index.js',
            // 'adaptable-action-card': './assets/components/adaptable-action-card/0.1.7/index.js',
            // 推荐主动指定 name 属性
            'adaptable-action-card': {
                name: 'AlimeComponentAdaptableActionCard',
                // url: '//g.alicdn.com/alime-components/adaptable-action-card/0.1.7/index.js'
                url: './assets/components/adaptable-action-card/0.1.7/index.js'
            },
        },
        // 侧边栏 https://chatui.io/sdk/sidebar
        sidebar: [
            {
                code: 'recommend',
                data: {
                    list: [
                        {
                            title: '如何办理准生证？'
                        },
                        {
                            title: '生育登记成功后在哪里看'
                        },
                        {
                            title: '链接形式',
                            url: 'https://www.baidu.com/'
                        }
                    ]
                }
            },
        ],
    },
    makeRecorder({ ctx }) {
        return {
            // 是否支持语音输入，
            canRecord: true,
            onStart() {
                console.log('开始录音');
            },
            onEnd() {
                console.log('停止录音');
                // 识别到文本后要 ctx.postMessage
            },
            onCancel() {
                console.log('取消录音');
            },
        };
    },
    makeSocket({ ctx }) {
        // 转人工后如何收发消息 https://chatui.io/sdk/agent
        // 连接 ws
        const ws = new WebSocket('ws://localhost:9090/ws');

        // ws 的事件配置
        ws.onmessage = (e) => { };
        ws.onopen = (e) => { };
        ws.onclose = (e) => { };

        return {
            // 发送接口，用户发送信息时触发
            send(msg) {
                ws.send('发给后端的数据');
            },
            // 结束接口，退出服务时触发
            close() {
                ws.close();
            },
        };
    },
    requests: {
        // 输入联想 https://chatui.io/sdk/autocomplete
        autoComplete(data) {
            console.log('autoComplete', data)
            return {
                url: '/xiaomi/associate.do',
                data: {
                    q: data.text,
                },
            };
        },
        send: function (msg) {
            if (msg.type === 'text') {
                return {
                    url: '//api.server.com/ask',
                    data: {
                        q: msg.content.text
                    }
                };
            }
        }
    },
    handlers: {
        onToolbarClick: function (item, ctx) {
            // item 即为上面 toolbar 中被点击的那一项，可通过 item.type 区分
            // ctx 为上下文，可用 ctx.appendMessage 渲染消息等
            console.log("onToolbarClick", item, ctx)
        },
        parseResponse: function (res, requestType) {
            console.log("parseResponse", res, requestType)
            if (requestType === 'autoComplete') {
                // 输入联想 https://chatui.io/sdk/autocomplete
                return {
                    list: res.AssociateList.slice(0, 8).map((t) => ({ title: t.Title })),
                    keyword: res.Utterance,
                };
            } else if (requestType === 'send' && res.Messages) {
                // 解析 ISV 消息数据
                return isvParser({ data: res });
            }

            return res;
        },
        track: function (data) {
            // 埋点 https://chatui.io/sdk/track
            console.log(data.eventType);

            // 传给后端，具体可用第三方工具或得自己实现
            // sendRequest(data);
        },
    }
});

bot.run();
// 获取 app 对象
// bot.getApp();
// 获取 ctx 对象
// bot.getCtx();
// 更新 config
// bot.setConfig(configKey, configValue);
// 停止录音
// bot.stopRecord();
// 获取上下文数据
// bot.getContext();
// 
console.log('ChatSDK.version', ChatSDK.version)