<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>${(blog.name)!"Blog"}</title>
  <style>
    :root{
      --bg:#ffffff;--fg:#111827;--muted:#6b7280;--border:#e5e7eb;
      --surface:#ffffff;--surface2:#f9fafb;
      --radius:12px;
      --container:980px;
    }
    *{box-sizing:border-box}
    html,body{height:100%}
    body{margin:0;background:var(--bg);color:var(--fg);font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,"Noto Sans","PingFang SC","Hiragino Sans GB","Microsoft YaHei",sans-serif;line-height:1.7}
    a{color:inherit;text-decoration:none}
    a:hover{text-decoration:underline}
    img{max-width:100%;height:auto;border-radius:10px}
    .container{max-width:var(--container);margin:0 auto;padding:20px}
    .topbar{display:flex;gap:12px;align-items:flex-start;justify-content:space-between;flex-wrap:wrap;padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .brand h1{margin:0;font-size:20px;line-height:1.25}
    .nav{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
    .nav a{display:inline-block;padding:8px 10px;border-radius:10px;border:1px solid var(--border);background:var(--surface2)}
    .layout{margin-top:16px;display:grid;grid-template-columns:1fr;gap:16px}
    @media (min-width: 860px){.layout{grid-template-columns:1fr 280px}}
    .panel{padding:18px 20px;border:1px solid var(--border);border-radius:var(--radius);background:var(--surface)}
    .title{margin:0 0 6px;font-size:22px;line-height:1.25}
    .desc{margin:0 0 10px;color:var(--muted);font-size:13px}
    .tags{margin:10px 0 0;display:flex;flex-wrap:wrap;gap:6px}
    .tag{font-size:12px;color:var(--muted);border:1px dashed var(--border);border-radius:999px;padding:2px 8px;background:var(--surface2)}
    .content{margin-top:14px}
    .content pre{white-space:pre-wrap;word-break:break-word;background:var(--surface2);border:1px solid var(--border);border-radius:10px;padding:12px;margin:0}
    .content blockquote{margin:12px 0;padding:8px 12px;border-left:3px solid var(--border);background:var(--surface2);color:var(--muted)}
    .content table{border-collapse:collapse;max-width:100%}
    .content th,.content td{border:1px solid var(--border);padding:8px 10px}
    .aside h3{margin:0 0 12px;font-size:15px}
    .chips{display:flex;flex-wrap:wrap;gap:8px;margin:0;padding:0;list-style:none}
    .chip a{display:inline-block;padding:6px 10px;border:1px solid var(--border);border-radius:999px;background:var(--surface2);font-size:13px}
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
        <a href="/blog/${kbase.uid!''}/search.html">搜索</a>
        <#if (blog.categoryUid)??>
          <a href="/blog/${kbase.uid!''}/category/${blog.categoryUid}.html">返回分类</a>
        </#if>
      </nav>
    </header>

    <div class="layout">
      <article class="panel">
        <h2 class="title">${(blog.name)!blog.uid}</h2>
        <p class="desc">${(blog.description)!""}</p>

        <#if blog.coverImageUrl?? && blog.coverImageUrl?length gt 0>
          <p><img src="${blog.coverImageUrl}" alt="cover" /></p>
        </#if>

        <#if blog.tagList?? && (blog.tagList?size > 0)>
          <div class="tags">
            <#list blog.tagList as t>
              <span class="tag">#${t}</span>
            </#list>
          </div>
        </#if>

        <div class="content">
          <#if blog.contentHtml?? && blog.contentHtml?length gt 0>
            ${blog.contentHtml}
          <#elseif blog.contentMarkdown??>
            <pre>${blog.contentMarkdown}</pre>
          <#else>
            <p class="desc">（无内容）</p>
          </#if>
        </div>
      </article>

      <aside class="panel aside">
        <h3>分类</h3>
        <ul class="chips">
          <#list categories as c>
            <li class="chip"><a href="/blog/${kbase.uid!''}/category/${c.uid}.html">${(c.name)!c.uid}</a></li>
          </#list>
        </ul>
      </aside>
    </div>
  </div>
</body>
</html>
