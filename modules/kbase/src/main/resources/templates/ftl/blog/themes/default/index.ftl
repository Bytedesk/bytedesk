<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>${(kbase.name)!"Blog"}</title>
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
    .brand h1{margin:0;font-size:22px;line-height:1.25}
    .brand p{margin:6px 0 0;color:var(--muted);font-size:14px}
    .nav{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
    .nav a{display:inline-block;padding:8px 10px;border-radius:10px;border:1px solid var(--border);background:var(--surface2)}
    .section{margin-top:16px;padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .section h2{margin:0 0 12px;font-size:16px}
    .chips{display:flex;flex-wrap:wrap;gap:8px;margin:0;padding:0;list-style:none}
    .chip a{display:inline-block;padding:6px 10px;border:1px solid var(--border);border-radius:999px;background:var(--surface2);font-size:13px}
    .list{margin:0;padding:0;list-style:none;display:grid;grid-template-columns:repeat(1,minmax(0,1fr));gap:12px}
    @media (min-width: 860px){.list{grid-template-columns:repeat(2,minmax(0,1fr))}}
    .card{border:1px solid var(--border);border-radius:var(--radius);background:var(--surface2);padding:14px 14px}
    .card h3{margin:0 0 6px;font-size:16px;line-height:1.35}
    .meta{color:var(--muted);font-size:12px}
    .tags{margin-top:8px;display:flex;flex-wrap:wrap;gap:6px}
    .tag{font-size:12px;color:var(--muted);border:1px dashed var(--border);border-radius:999px;padding:2px 8px;background:var(--surface)}
    .empty{color:var(--muted);margin:0}
  </style>
</head>
<body>
  <div class="container">
    <header class="topbar">
      <div class="brand">
        <h1>${(kbase.name)!"Blog"}</h1>
        <p>${(kbase.descriptionHtml)!""}</p>
      </div>
      <nav class="nav">
        <a href="/blog/${kbase.uid!''}">首页</a>
        <a href="/blog/${kbase.uid!''}/search.html">搜索</a>
      </nav>
    </header>

    <section class="section">
      <h2>分类</h2>
      <ul class="chips">
        <#list categories as c>
          <li class="chip"><a href="/blog/${kbase.uid!''}/category/${c.uid}.html">${(c.name)!c.uid}</a></li>
        </#list>
      </ul>
    </section>

    <section class="section">
      <h2>文章</h2>
      <#if blogs?size == 0>
        <p class="empty">暂无内容</p>
      <#else>
        <ul class="list">
          <#list blogs as b>
            <li class="card">
              <h3>
                <a href="/blog/${kbase.uid!''}/post/${b.uid}.html">${(b.name)!b.uid}</a>
                <#if (b.published)?? && b.published == false>
                  <span class="meta">（未发布）</span>
                </#if>
              </h3>
              <div class="meta">${(b.description)!""}</div>
              <#if b.tagList?? && (b.tagList?size > 0)>
                <div class="tags">
                  <#list b.tagList as t>
                    <span class="tag">#${t}</span>
                  </#list>
                </div>
              </#if>
            </li>
          </#list>
        </ul>
      </#if>
    </section>
  </div>
</body>
</html>
