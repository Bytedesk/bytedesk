<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${(customName?exists && customName != '')?then(customName, '系统入口')}</title>
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
            max-width: 800px;
            width: 90%;
            padding: 40px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            text-align: center;
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
        <#if customLogo?exists && customLogo != ''>
        <img src="${customLogo}" alt="${(customName?exists && customName != '')?then(customName, '系统入口')}" class="logo">
        </#if>
        <h1>${(customName?exists && customName != '')?then(customName, '系统入口')}</h1>
        <p class="description">${(customDescription?exists && customDescription != '')?then(customDescription, '请选择您要进入的系统入口')}</p>
        
        <div class="button-container">
            <a href="/admin" target="_blank" class="button">
                <i class="fas fa-cogs"></i>
                <div class="button-title">管理后台</div>
                <div class="button-description">系统配置与管理</div>
            </a>
            <a href="/agent/chat" target="_blank" class="button">
                <i class="fas fa-headset"></i>
                <div class="button-title">客服工作台</div>
                <div class="button-description">客户服务与沟通</div>
            </a>
        </div>
        
        <footer>
            <p>© 2025 版权所有</p>
        </footer>
    </div>
</body>
</html>