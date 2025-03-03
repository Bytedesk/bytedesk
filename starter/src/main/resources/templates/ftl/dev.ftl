<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="/favicon.ico">
    <title>${i18n['en']["title"]}</title>
    <style>
        :root {
            /* 浅色主题变量 */
            --bg-color: #f8f9fa;
            --container-bg: #ffffff;
            --text-color: #333333;
            --link-color: #3498db;
            --link-hover-color: #2980b9;
            --border-color: #eeeeee;
            --shadow-color: rgba(0, 0, 0, 0.1);
            --heading-color: #2c3e50;
            --arrow-color: #95a5a6;
            --button-bg: #3498db;
            --button-active: #2c3e50;
        }

        [data-theme="dark"] {
            /* 深色主题变量 */
            --bg-color: #1a1a1a;
            --container-bg: #2d2d2d;
            --text-color: #e0e0e0;
            --link-color: #61dafb;
            --link-hover-color: #4fa9d6;
            --border-color: #404040;
            --shadow-color: rgba(0, 0, 0, 0.3);
            --heading-color: #e0e0e0;
            --arrow-color: #808080;
            --button-bg: #4a4a4a;
            --button-active: #61dafb;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
            padding: 2rem;
            background: var(--bg-color);
            color: var(--text-color);
            line-height: 1.6;
            transition: background-color 0.3s, color 0.3s;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background: var(--container-bg);
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 15px var(--shadow-color);
            transition: background-color 0.3s;
        }

        .switchers {
            position: fixed;
            top: 1rem;
            right: 1rem;
            display: flex;
            gap: 1rem;
        }

        .language-switcher, .theme-switcher {
            display: flex;
            gap: 0.5rem;
            background: var(--container-bg);
            padding: 0.5rem;
            border-radius: 5px;
            box-shadow: 0 2px 10px var(--shadow-color);
        }

        button {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 4px;
            background: var(--button-bg);
            color: white;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        button:hover {
            background: var(--link-hover-color);
        }

        button.active {
            background: var(--button-active);
        }

        h1, h2 {
            color: var(--heading-color);
            margin: 1.5rem 0 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid var(--border-color);
        }

        a {
            color: var(--link-color);
            text-decoration: none;
            transition: color 0.2s;
        }

        a:hover {
            color: var(--link-hover-color);
            text-decoration: underline;
        }

        ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        li {
            margin: 0.5rem 0;
            padding-left: 1.5rem;
            position: relative;
        }

        li::before {
            content: "→";
            position: absolute;
            left: 0;
            color: var(--arrow-color);
        }

        [lang] {
            display: none;
        }

        [lang].active {
            display: block;
        }
    </style>
</head>

<body>
    <div class="switchers">
        <div class="language-switcher">
            <#list languages?keys as lang>
                <button onclick="setLanguage('${lang}')">${languages[lang]}</button>
            </#list>
        </div>
        <div class="theme-switcher">
            <button onclick="setTheme('light')" title="浅色主题">🌞</button>
            <button onclick="setTheme('dark')" title="深色主题">🌙</button>
            <button onclick="setTheme('system')" title="跟随系统">💻</button>
        </div>
    </div>

    <#list languages?keys as lang>
        <div class="container" lang="${lang}">
            <h1>${i18n[lang]["title"]}</h1>
            <#--  <p>
                demo username: admin@email.com<br>
                demo password: admin
            </p>  -->
            <#--  <p>线上演示环境需要自己手机号+验证码登录</p>  -->

            <h2>${i18n[lang]["systemEntrance"]}</h2>
            <ul>
                <li><a href="/admin/" target="_blank">${i18n[lang]["adminDashboard"]}</a></li>
                <li><a href="/agent/chat" target="_blank">${i18n[lang]["agentClient"]}</a></li>
                <li><a href="/chat/demo" target="_blank">${i18n[lang]["visitorChat"]}</a></li>
                <li><a href="/agenticflow/" target="_blank">${i18n[lang]["workFlow"]}</a></li>
                <li><a href="/notebase/" target="_blank">${i18n[lang]["knowledgeBase"]}</a></li>
                <li><a href="/helpcenter/df_org_uid_df_kb_hc_uid" target="_blank">${i18n[lang]["helpCenter"]}</a></li>
                <li><a href="/kanban/" target="_blank">${i18n[lang]["kanban"]}</a></li>
            </ul>

            <h2>${i18n[lang]["systemDevelopment"]}</h2>
            <ul>
                <li><a href="/apidocs/index.html" target="_blank">${i18n[lang]["javaDoc"]}</a></li>
                <li><a href="/swagger-ui/index.html" target="_blank">${i18n[lang]["apiDoc"]}</a></li>
                <li><a href="/doc.html" target="_blank">${i18n[lang]["knife4jDoc"]}</a></li>
                <li><a href="/druid" target="_blank">Druid</a></li>
                <li><a href="https://www.weiyuai.cn/docs/zh-CN/" target="_blank">${i18n[lang]["docs"]}</a></li>
                <li><a href="/web" target="_blank">Web</a></li>
            </ul>

            <h2>${i18n[lang]["moduleDevelopment"]}</h2>
            <ul>
                <li><a href="/team/" target="_blank">${i18n[lang]["team"]}</a></li>
                <li><a href="/service/" target="_blank">${i18n[lang]["service"]}</a></li>
                <li><a href="/kbase/" target="_blank">${i18n[lang]["kbase"]}</a></li>
                <li><a href="/ai/" target="_blank">${i18n[lang]["ai"]}</a></li>
                <li><a href="/ticket/" target="_blank">${i18n[lang]["ticket"]}</a></li>
                <li><a href="/social/" target="_blank">${i18n[lang]["social"]}</a></li>                
                <li><a href="/voc/" target="_blank">${i18n[lang]["voiceOfCustomer"]}</a></li>
                <#--  <li><a href="/forum/" target="_blank">${i18n[lang]["forum"]}</a></li>  -->
            </ul>

            <#--  demo  -->
            <h2>${i18n[lang]["demo"]}[TODO]</h2>
            <ul>
                <li><a href="/chat/demo/airline" target="_blank">${i18n[lang]["airline"]}</a></li>
                <li><a href="/chat/demo/bytedesk" target="_blank">${i18n[lang]["bytedesk"]}</a></li>
                <li><a href="/chat/demo/shopping" target="_blank">${i18n[lang]["shopping"]}</a></li>
            </ul>
        </div>
    </#list>


    <script src="https://www.weiyuai.cn/embed/bytedesk-web.js"></script>
    <script>
        // DOM 加载完成后执行初始化
        document.addEventListener('DOMContentLoaded', function() {
            initializeLanguage();
            initializeTheme();
            initializeChatConfig();
        });

        // 语言切换相关
        function initializeLanguage() {
            const savedLang = localStorage.getItem('preferred-language') || 'en';
            setLanguage(savedLang, false); // false表示不更新chat配置
        }

        function setLanguage(lang, updateChat = true) {
            // 防止无效的语言值
            if (!document.querySelector(`[lang="${lang}"]`)) {
                console.warn('Invalid language:', lang);
                lang = 'en'; // 默认回退到英语
            }

            // 更新DOM
            document.querySelectorAll('[lang]').forEach(el => {
                el.style.display = 'none';
            });
            const selectedLang = document.querySelector(`[lang="${lang}"]`);
            if (selectedLang) {
                selectedLang.style.display = 'block';
            }

            // 更新按钮状态
            document.querySelectorAll('.language-switcher button').forEach(btn => {
                btn.classList.remove('active');
            });
            const langButton = document.querySelector(`button[onclick="setLanguage('${lang}')"]`);
            if (langButton) {
                langButton.classList.add('active');
            }

            // 保存语言偏好
            localStorage.setItem('preferred-language', lang);

            // 更新客服配置（如果需要）
            if (updateChat) {
                updateChatConfig(lang);
            }
        }

        // 主题切换相关
        function initializeTheme() {
            const savedTheme = localStorage.getItem('preferred-theme') || 'system';
            setTheme(savedTheme);

            // 监听系统主题变化
            const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
            mediaQuery.addEventListener('change', handleSystemThemeChange);
        }

        function handleSystemThemeChange(e) {
            if (localStorage.getItem('preferred-theme') === 'system') {
                document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light');
            }
        }

        function setTheme(theme) {
            // 验证主题值
            if (!['light', 'dark', 'system'].includes(theme)) {
                console.warn('Invalid theme:', theme);
                theme = 'system';
            }

            // 设置主题
            if (theme === 'system') {
                const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                document.documentElement.setAttribute('data-theme', prefersDark ? 'dark' : 'light');
            } else {
                document.documentElement.setAttribute('data-theme', theme);
            }
            
            // 更新按钮状态
            document.querySelectorAll('.theme-switcher button').forEach(btn => {
                btn.classList.remove('active');
            });
            const themeButton = document.querySelector(`.theme-switcher button[onclick*="${theme}"]`);
            if (themeButton) {
                themeButton.classList.add('active');
            }

            // 保存主题偏好
            localStorage.setItem('preferred-theme', theme);
        }

        // 客服配置相关
        function initializeChatConfig() {
            const initialLang = localStorage.getItem('preferred-language') || 'en';
            updateChatConfig(initialLang);
        }

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

            try {
                // 如果已存在实例，先清理
                if (window.bytedesk) {
                    window.bytedesk.hideBubble();
                    window.bytedesk.hideButton();
                    window.bytedesk.hideChat();
                }

                const config = {
                    placement: 'bottom-right',
                    autoPopup: false,
                    inviteConfig: {
                        show: false,
                        text: texts.inviteText,
                        delay: 1000,
                        loop: true,
                        loopDelay: 10000,
                        loopCount: 3,
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

                // 创建新实例
                window.bytedesk = new BytedeskWeb(config);
                window.bytedesk.init();
            } catch (error) {
                console.error('Error updating chat config:', error);
            }
        }
    </script>

    

</body>
</html>