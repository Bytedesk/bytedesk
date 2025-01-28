<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="/favicon.ico">
    <title>微语</title>
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

        .theme-switcher {
            position: fixed;
            top: 1rem;
            right: 1rem;
            display: flex;
            gap: 0.5rem;
            background: var(--container-bg);
            padding: 0.5rem;
            border-radius: 5px;
            box-shadow: 0 2px 10px var(--shadow-color);
        }

        .theme-btn {
            padding: 0.5rem;
            border: none;
            border-radius: 4px;
            background: var(--link-color);
            color: var(--container-bg);
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .theme-btn:hover {
            background: var(--link-hover-color);
        }

        .theme-btn.active {
            background: var(--heading-color);
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
    </style>
</head>
<body>
    <div class="theme-switcher">
        <button class="theme-btn" onclick="setTheme('light')" title="浅色主题">🌞</button>
        <button class="theme-btn" onclick="setTheme('dark')" title="深色主题">🌙</button>
        <button class="theme-btn" onclick="setTheme('system')" title="跟随系统">💻</button>
    </div>

    <div class="container">
        <h1>微语</h1>
        <p>
            username: admin@email.com<br>
            password: admin
        </p>

        <h2>系统入口</h2>
        <ul>
            <li><a href="/admin/" target="_blank">管理后台</a></li>
            <li><a href="/agent/chat" target="_blank">客服工作台</a></li>
            <li><a href="/chat/demo" target="_blank">访客对话</a></li>
            <li><a href="/agenticflow/" target="_blank">工单系统</a></li>
            <li><a href="/notebase/spaces" target="_blank">知识库</a></li>
            <li><a href="/kbase/" target="_blank">帮助中心</a></li>
        </ul>

        <h2>开发工具</h2>
        <ul>
            <li><a href="/swagger-ui/index.html" target="_blank">API 文档</a></li>
            <li><a href="/druid" target="_blank">Druid</a></li>
        </ul>
    </div>

    <script>
        function setTheme(theme) {
            if (theme === 'system') {
                document.documentElement.removeAttribute('data-theme');
                const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                document.documentElement.setAttribute('data-theme', prefersDark ? 'dark' : 'light');
            } else {
                document.documentElement.setAttribute('data-theme', theme);
            }
            localStorage.setItem('preferred-theme', theme);
            updateThemeButtons(theme);
        }

        function updateThemeButtons(activeTheme) {
            document.querySelectorAll('.theme-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            document.querySelector('.theme-btn[onclick*="' + activeTheme + '"]').classList.add('active');
        }

        // 初始化主题
        function initializeTheme() {
            const savedTheme = localStorage.getItem('preferred-theme') || 'system';
            setTheme(savedTheme);

            // 监听系统主题变化
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
                if (localStorage.getItem('preferred-theme') === 'system') {
                    document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light');
                }
            });
        }

        // 初始化
        initializeTheme();
    </script>
</body>
</html>