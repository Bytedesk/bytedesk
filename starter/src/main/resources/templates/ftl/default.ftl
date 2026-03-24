<!DOCTYPE html>
<html lang="${lang! 'zh-CN'}">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <#include "./common/macro/i18n.ftl" />
    <#assign currentLang = 'zh-cn'>
    <#if lang??>
        <#assign _lc = lang?lower_case>
        <#if _lc?index_of('zh-tw') == 0>
            <#assign currentLang = 'zh-tw'>
        <#elseif _lc?index_of('en') == 0>
            <#assign currentLang = 'en'>
        </#if>
    </#if>
    <#assign currentLangLabel = '简体中文'>
    <#if currentLang == 'zh-tw'>
        <#assign currentLangLabel = '繁體中文'>
    <#elseif currentLang == 'en'>
        <#assign currentLangLabel = 'English'>
    </#if>
    <title><@t key="default.title">系统入口</@t></title>
    <#--  <title>${(customName?exists && customName != '')?then(customName?html, '系统入口')}</title>  -->
    <#if customLogo?exists && customLogo != ''>
    <link rel="icon" href="${customLogo}" type="image/x-icon">
    <link rel="shortcut icon" href="${customLogo}" type="image/x-icon">
    </#if>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Helvetica Neue', Arial, sans-serif;
        }
        
        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        }
        
        .container {
            position: relative;
            max-width: 800px;
            width: 90%;
            padding: 40px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        .lang-switch {
            position: absolute;
            top: 24px;
            right: 24px;
        }

        .lang-switch details {
            position: relative;
        }

        .lang-switch summary {
            list-style: none;
            display: flex;
            align-items: center;
            gap: 8px;
            min-width: 120px;
            padding: 10px 14px;
            border: 1px solid #d9e2ec;
            border-radius: 999px;
            background: #f8fbff;
            color: #234;
            cursor: pointer;
            font-size: 0.95em;
            box-shadow: 0 8px 20px rgba(17, 38, 146, 0.08);
        }

        .lang-switch summary::-webkit-details-marker {
            display: none;
        }

        .lang-switch summary::after {
            content: '';
            width: 8px;
            height: 8px;
            border-right: 2px solid #5b6b7d;
            border-bottom: 2px solid #5b6b7d;
            transform: rotate(45deg) translateY(-1px);
            transition: transform 0.2s ease;
        }

        .lang-switch details[open] summary::after {
            transform: rotate(225deg) translateY(-1px);
        }

        .lang-menu {
            position: absolute;
            top: calc(100% + 10px);
            right: 0;
            min-width: 160px;
            padding: 8px;
            border: 1px solid #d9e2ec;
            border-radius: 14px;
            background: #ffffff;
            box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14);
        }

        .lang-option {
            display: flex;
            align-items: center;
            justify-content: space-between;
            width: 100%;
            padding: 10px 12px;
            border: 0;
            border-radius: 10px;
            background: transparent;
            color: #243447;
            cursor: pointer;
            font-size: 0.95em;
            text-align: left;
        }

        .lang-option:hover,
        .lang-option.is-active {
            background: #eef6ff;
            color: #0f5fbf;
        }

        .lang-option-check {
            font-size: 0.9em;
            opacity: 0.85;
        }
        
        h1 {
            color: #333;
            font-size: 2.5em;
            margin-bottom: 20px;
        }
        
        .logo {
            width: 120px;
            margin-bottom: 30px;
        }
        
        .description {
            color: #666;
            margin-bottom: 40px;
            line-height: 1.6;
        }
        
        .button-container {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 20px;
        }
        
        .button {
            display: flex;
            flex-direction: column;
            align-items: center;
            text-decoration: none;
            padding: 20px 30px;
            background-color: #fff;
            color: #333;
            border-radius: 8px;
            border: 1px solid #eaeaea;
            transition: all 0.3s ease;
            min-width: 200px;
        }
        
        .button:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
            border-color: #3498db;
        }
        
        .button i {
            font-size: 2.5em;
            margin-bottom: 15px;
            color: #3498db;
        }
        
        .button-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 8px;
        }
        
        .button-description {
            font-size: 0.9em;
            color: #777;
        }
        
        footer {
            margin-top: 60px;
            color: #888;
            font-size: 0.9em;
        }
        
        @media (max-width: 600px) {
            .container {
                padding: 30px 20px;
            }

            .lang-switch {
                top: 16px;
                right: 16px;
            }

            .lang-switch summary {
                min-width: 108px;
                padding: 8px 12px;
                font-size: 0.9em;
            }
            
            h1 {
                font-size: 2em;
            }
            
            .button {
                width: 100%;
            }
        }
    </style>
    <!-- 添加Font Awesome图标支持 -->
    <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="lang-switch">
            <details>
                <summary>${currentLangLabel}</summary>
                <div class="lang-menu">
                    <button class="lang-option ${(currentLang == 'zh-cn')?string('is-active', '')}" type="button" data-lang="zh-CN">
                        <span>简体中文</span>
                        <span class="lang-option-check">${(currentLang == 'zh-cn')?string('✓', '')}</span>
                    </button>
                    <button class="lang-option ${(currentLang == 'zh-tw')?string('is-active', '')}" type="button" data-lang="zh-TW">
                        <span>繁體中文</span>
                        <span class="lang-option-check">${(currentLang == 'zh-tw')?string('✓', '')}</span>
                    </button>
                    <button class="lang-option ${(currentLang == 'en')?string('is-active', '')}" type="button" data-lang="en">
                        <span>English</span>
                        <span class="lang-option-check">${(currentLang == 'en')?string('✓', '')}</span>
                    </button>
                </div>
            </details>
        </div>
        <#--  <#if customLogo?exists && customLogo != ''>
        <img src="${customLogo}" alt="${(customName?exists && customName != '')?then(customName?html, '系统入口')}" class="logo">
        </#if>  -->
    <#--  <h1>${(customName?exists && customName != '')?then(customName?html, '系统入口')}</h1>  -->
    <h1><@t key="default.title">系统入口</@t></h1>
    <#--  <p class="description">${(customDescription?exists && customDescription != '')?then(customDescription?html, '请选择您要进入的系统入口')}</p>  -->
    <p class="description"><@t key="default.select">请选择您要进入的系统入口</@t></p>
        
        <div class="button-container">
            <a href="/admin" target="_blank" class="button">
                <i class="fas fa-cogs"></i>
                <div class="button-title"><@t key="default.admin.title">管理后台</@t></div>
                <div class="button-description"><@t key="default.admin.desc">系统配置与管理</@t></div>
            </a>
            <a href="/agent/chat" target="_blank" class="button">
                <i class="fas fa-headset"></i>
                <div class="button-title"><@t key="default.agent.title">客服工作台</@t></div>
                <div class="button-description"><@t key="default.agent.desc">客户服务与沟通</@t></div>
            </a>
        </div>
        
        <footer>
            <p>© 2025 <@t key="default.copyright">版权所有</@t></p>
        </footer>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const options = document.querySelectorAll('.lang-option');

            options.forEach(function(option) {
                option.addEventListener('click', function() {
                    const targetLang = option.getAttribute('data-lang');
                    const url = new URL(window.location.href);

                    url.searchParams.set('lang', targetLang);
                    window.location.href = url.toString();
                });
            });
        });
    </script>
</body>
</html>