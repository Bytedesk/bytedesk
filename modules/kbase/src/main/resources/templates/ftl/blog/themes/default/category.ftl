<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>${(kbase.name)!"Blog"} - ${(category.name)!"分类"}</title>
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
    .brand h2{margin:8px 0 0;font-size:14px;color:var(--muted);font-weight:600}
    .nav{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
    .nav a{display:inline-block;padding:8px 10px;border-radius:10px;border:1px solid var(--border);background:var(--surface2)}
    .layout{margin-top:16px;display:grid;grid-template-columns:1fr;gap:16px}
    @media (min-width: 860px){.layout{grid-template-columns:280px 1fr}}
    .panel{padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .panel h3{margin:0 0 12px;font-size:15px}
    .chips{display:flex;flex-wrap:wrap;gap:8px;margin:0;padding:0;list-style:none}
    .chip a{display:inline-block;padding:6px 10px;border:1px solid var(--border);border-radius:999px;background:var(--surface2);font-size:13px}
    .list{margin:0;padding:0;list-style:none;display:grid;grid-template-columns:repeat(1,minmax(0,1fr));gap:12px}
    .card{border:1px solid var(--border);border-radius:var(--radius);background:var(--surface2);padding:14px 14px}
    .card h4{margin:0 0 6px;font-size:16px;line-height:1.35}
    .meta{color:var(--muted);font-size:12px}
    .empty{color:var(--muted);margin:0}
  </style>
</head>
<body>
  <div class="container">
    <header class="topbar">
      <div class="brand">
        <h1><a href="../index.html">${(kbase.name)!"Blog"}</a></h1>
        <h2>${(category.name)!category.uid}</h2>
      </div>
      <nav class="nav">
        <a href="../index.html">首页</a>
        <a href="../search.html">搜索</a>
      </nav>
    </header>

    <div class="layout">
      <aside class="panel">
        <h3>全部分类</h3>
        <ul class="chips">
          <#list categories as c>
            <li class="chip"><a href="${c.uid}.html">${(c.name)!c.uid}</a></li>
          </#list>
        </ul>
      </aside>

      <main class="panel">
        <h3>文章列表</h3>
        <#if blogs?size == 0>
          <p class="empty">暂无内容</p>
        <#else>
          <ul class="list">
            <#list blogs as b>
              <li class="card">
                <h4><a href="../post/${b.uid}.html">${(b.name)!b.uid}</a></h4>
                <div class="meta">${(b.description)!""}</div>
              </li>
            </#list>
          </ul>
        </#if>
      </main>
    </div>
  </div>
</body>
</html>
