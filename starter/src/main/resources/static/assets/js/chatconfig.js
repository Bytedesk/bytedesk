
function updateChatConfig(lang) {
    const i18nConfig = {
        'en': {
            inviteText: 'Hello, how can I help you?',
            bubbleTitle: 'Need Help?',
            bubbleSubtitle: 'Click to chat with me'
        },
        'zh': {
            inviteText: '您好，请问有什么可以帮您？',
            bubbleTitle: '需要帮助么',
            bubbleSubtitle: '点击我，与我对话'
        },
        'zh-TW': {
            inviteText: '您好，請問有什麼可以幫您？',
            bubbleTitle: '需要幫助嗎',
            bubbleSubtitle: '點擊我，與我對話'
        }
    };

    const texts = i18nConfig[lang] || i18nConfig['en'];

    const config = {
        placement: 'bottom-right',
        autoPopup: false,
        inviteConfig: {
            show: false,
            text: texts.inviteText,
            delay: 1000, // 首次弹出延迟时间, 单位: 毫秒
            loop: true, // 是否启用循环
            loopDelay: 10000, // 循环间隔, 单位: 毫秒
            loopCount: 3, // 循环次数, 设置为0表示无限循环
        },
        bubbleConfig: {
            show: true,
            icon: '👋',
            title: texts.bubbleTitle,
            subtitle: texts.bubbleSubtitle
        },
        theme: {
            mode: 'system',
            backgroundColor: '#0066FF',
            textColor: '#ffffff'
        },
        window: {
            width: 380
        },
        chatConfig: {
            org: 'df_org_uid',
            t: '1',
            sid: 'df_wg_uid'
        }
    };

    // 如果已存在实例，先销毁
    if (window.bytedesk) {
        // 假设有销毁方法
        window.bytedesk.hideBubble();
        window.bytedesk.hideButton();
        window.bytedesk.hideChat();
        window.bytedesk.hideInviteDialog();
    }

    // 创建新实例
    window.bytedesk = new BytedeskWeb(config);
    window.bytedesk.init();
}

// 初始化时也要设置客服配置
const initialLang = localStorage.getItem('preferred-language') || 'en';
updateChatConfig(initialLang);