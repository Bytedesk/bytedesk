<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="/favicon.ico">
    <title>${i18n['zh']["title"]}</title>
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

        ['zh'] {
            display: none;
        }

        ['zh'].active {
            display: block;
        }
    </style>
</head>

<body>


            <h1>${i18n['zh']["title"]}</h1>

            <h2>${i18n['zh']["systemEntrance"]}</h2>
            <ul>
                <li><a href="/admin/" target="_blank">${i18n['zh']["adminDashboard"]}</a></li>
                <li><a href="/agent/chat" target="_blank">${i18n['zh']["agentClient"]}</a></li>
                <li><a href="/chat/demo" target="_blank">${i18n['zh']["visitorChat"]}</a></li>
                <li><a href="/agenticflow/" target="_blank">${i18n['zh']["workFlow"]}</a></li>
                <li><a href="/notebase/" target="_blank">${i18n['zh']["notebase"]}</a></li>
                <li><a href="/kanban/" target="_blank">${i18n['zh']["kanban"]}</a></li>
            </ul>

            <h2>${i18n['zh']["systemDevelopment"]}</h2>
            <ul>
                <li><a href="/javadocs/index.html" target="_blank">${i18n['zh']["javaDoc"]}</a></li>
                <li><a href="/swagger-ui/index.html" target="_blank">${i18n['zh']["apiDoc"]}</a></li>
                <li><a href="/doc.html" target="_blank">${i18n['zh']["knife4jDoc"]}</a></li>
                <li><a href="/druid" target="_blank">Druid</a></li>
                <li><a href="https://www.weiyuai.cn/docs/zh-CN/" target="_blank">${i18n['zh']["docs"]}</a></li>
                <li><a href="/web" target="_blank">Web</a></li>
            </ul>

            <h2>${i18n['zh']["moduleDevelopment"]}</h2>
            <ul>
                <li><a href="/team/" target="_blank">${i18n['zh']["team"]}</a></li>
                <li><a href="/service/" target="_blank">${i18n['zh']["service"]}</a></li>
                <li><a href="/kbase/" target="_blank">${i18n['zh']["kbase"]}</a></li>
                <li><a href="/ai/" target="_blank">${i18n['zh']["ai"]}</a></li>
                <li><a href="/ticket/" target="_blank">${i18n['zh']["ticket"]}</a></li>
                <li><a href="/social/" target="_blank">${i18n['zh']["social"]}</a></li>                
                <li><a href="/voc/" target="_blank">${i18n['zh']["voiceOfCustomer"]}</a></li>
                <li><a href="/plugins/kanban/" target="_blank">${i18n['zh']["kanban"]}</a></li>      
                <#--  <li><a href="/forum/" target="_blank">${i18n['zh']["forum"]}</a></li>  -->
            </ul>

            <#--  demo  -->
            <#--  <h2>${i18n['zh']["demo"]}[TODO]</h2>
            <ul>
                <li><a href="/chat/demo/airline" target="_blank">${i18n['zh']["airline"]}</a></li>
                <li><a href="/chat/demo/bytedesk" target="_blank">${i18n['zh']["bytedesk"]}</a></li>
                <li><a href="/chat/demo/shopping" target="_blank">${i18n['zh']["shopping"]}</a></li>
            </ul>  -->


</body>
</html>