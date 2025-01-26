<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="/favicon.ico">
    <title>ByteDesk</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background-color: #f8f9fa;
            color: #333;
            line-height: 1.6;
            padding: 2rem;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
        }

        h3 {
            color: #2c3e50;
            margin: 1.5rem 0 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #eee;
            font-weight: 600;
        }

        a {
            color: #3498db;
            text-decoration: none;
            padding: 0.5rem 0;
            display: inline-block;
            transition: color 0.2s;
        }

        a:hover {
            color: #2980b9;
            text-decoration: underline;
        }

        .section {
            margin-bottom: 2rem;
        }

        .section:last-child {
            margin-bottom: 0;
        }

        .section-title {
            font-size: 1.25rem;
            color: #2c3e50;
            margin-bottom: 1rem;
        }

        .link-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .link-list li {
            margin-bottom: 0.5rem;
        }

        .link-list a {
            display: inline-flex;
            align-items: center;
        }

        .link-list a:before {
            content: "→";
            margin-right: 0.5rem;
            color: #95a5a6;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="section">
            <a href="/" target="_blank">ByteDesk</a>
            <p>
                username: admin@email.com, password: admin
            </p>
        </div>

        <div class="section">
            <h3>Entrance</h3>
            <ul class="link-list">
                <li><a href="/admin" target="_blank">Admin Dashboard</a></li>
                <li><a href="/agent/chat" target="_blank">Agent Client</a></li>
                <li><a href="/chat/demo" target="_blank">Visitor Chat</a></li>
                <li><a href="/formflow" target="_blank">Work Flow</a></li>
                <li><a href="/notebase/spaces" target="_blank">Knowledge Base</a></li>
            </ul>
        </div>

        <div class="section">
            <h3>Development</h3>
            <ul class="link-list">
                <li><a href="/swagger-ui/index.html" target="_blank">API Documentation</a></li>
                <#--  <li><a href="/actuator" target="_blank">Monitoring</a></li>  -->
                <li><a href="/druid" target="_blank">Druid</a></li>
            </ul>
        </div>
    </div>
</body>
</html>