<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <style>
        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            overflow: hidden;
            font-family: Arial, sans-serif;
        }
        .container {
            position: relative;
            height: 100vh;
            width: 100%;
        }
        .iframe-container {
            position: relative;
            width: 100%;
            height: 100%;
            overflow: hidden;
        }
        iframe {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            border: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="iframe-container">
            <iframe src="${chatUrl}" allowfullscreen></iframe>
        </div>
    </div>
</body>
</html> 