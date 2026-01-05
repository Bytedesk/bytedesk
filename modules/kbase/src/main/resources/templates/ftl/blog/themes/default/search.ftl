<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>${(kbase.name)!"Blog"} - 搜索</title>
  <style>
    :root{
      --bg:#ffffff;--fg:#111827;--muted:#6b7280;--border:#e5e7eb;
      --surface:#ffffff;--surface2:#f9fafb;
      --radius:12px;
      --container:980px;
    }
    *{box-sizing:border-box}
    html,body{height:100%}
    body{margin:0;background:var(--bg);color:var(--fg);font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,"Noto Sans","PingFang SC","Hiragino Sans GB","Microsoft YaHei",sans-serif;line-height:1.6}
    a{color:inherit;text-decoration:none}
    a:hover{text-decoration:underline}
    .container{max-width:var(--container);margin:0 auto;padding:20px}
    .topbar{display:flex;gap:12px;align-items:flex-start;justify-content:space-between;flex-wrap:wrap;padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .brand h1{margin:0;font-size:20px;line-height:1.25}
    .nav{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
    .nav a{display:inline-block;padding:8px 10px;border-radius:10px;border:1px solid var(--border);background:var(--surface2)}
    .panel{margin-top:16px;padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .panel h2{margin:0 0 10px;font-size:16px}
    .hint{margin:0;color:var(--muted);font-size:13px}
    .kv{margin-top:10px;padding:10px 12px;border:1px dashed var(--border);border-radius:10px;background:var(--surface2);font-size:12px;color:var(--muted)}
    .kv code{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,"Liberation Mono","Courier New",monospace}
  </style>
</head>
<body>
  <div class="container">
    <header class="topbar">
      <div class="brand">
        <h1><a href="/blog/${kbase.uid!''}">${(kbase.name)!"Blog"}</a></h1>
      </div>
      <nav class="nav">
        <a href="/blog/${kbase.uid!''}">首页</a>
      </nav>
    </header>

    <main class="panel">
      <h2>搜索</h2>
      <p class="hint">此页面为静态模板占位。你可以在此处接入前端搜索（调用 <code>/api/v1/blog/query/org</code> 或者自定义 visitor 查询接口）。</p>
      <#if content?? && content?length gt 0>
        <div class="kv">当前关键词：<code>${content}</code></div>
      </#if>
    </main>
  </div>
</body>
</html>
