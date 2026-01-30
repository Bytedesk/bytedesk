<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${(blog.name)!'Blog'}</title>
  <meta name="description" content="${(blog.description)!''}">
  <meta name="keywords" content="<#if blog.tagList??><#list blog.tagList as t>${t}<#if t_has_next>, </#if></#list></#if>">

  <!-- Bootstrap CSS -->
  <link href="/assets/vendor/bootstrap5/css/bootstrap.min.css" rel="stylesheet">
  <!-- Font Awesome -->
  <link href="/assets/vendor/font-awesome/css/all.min.css" rel="stylesheet">
  <!-- Bootstrap Icons -->
  <link href="/assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">

  <style>
    :root {
      --primary-color: #0d6efd;
      --secondary-color: #6c757d;
      --success-color: #198754;
      --info-color: #0dcaf0;
      --warning-color: #ffc107;
      --danger-color: #dc3545;
      --light-bg: #f8f9fa;
      --border-color: #dee2e6;
      --text-muted: #6c757d;
    }

    body {
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
      line-height: 1.7;
      color: #212529;
      background-color: #fff;
    }

    a {
      color: var(--primary-color);
      text-decoration: none;
      transition: color 0.15s ease-in-out;
    }

    a:hover {
      color: #0b5ed7;
      text-decoration: underline;
    }

    /* Top Bar */
    .topbar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem 0;
      margin-bottom: 2rem;
    }

    .topbar .brand h1 {
      font-size: 1.75rem;
      font-weight: 700;
      margin-bottom: 0;
      color: white;
    }

    .topbar .brand h1 a {
      color: white;
    }

    .topbar .brand h1 a:hover {
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
    }

    .topbar .nav-link {
      color: white;
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      padding: 0.5rem 1rem;
      border-radius: 0.375rem;
      transition: all 0.3s ease;
    }

    .topbar .nav-link:hover {
      background: rgba(255, 255, 255, 0.3);
      color: white;
      text-decoration: none;
      transform: translateY(-2px);
    }

    /* Breadcrumb */
    .breadcrumb {
      background: var(--light-bg);
      border-radius: 0.5rem;
      padding: 0.75rem 1rem;
      margin-bottom: 1.5rem;
      border: 1px solid var(--border-color);
    }

    .breadcrumb-item a {
      color: var(--text-muted);
    }

    .breadcrumb-item a:hover {
      color: var(--primary-color);
    }

    /* Article Panel */
    .article-panel {
      background: white;
      border-radius: 0.5rem;
      padding: 2rem;
      box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
      border: 1px solid var(--border-color);
      margin-bottom: 1.5rem;
    }

    .article-title {
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 1rem;
      color: #212529;
      line-height: 1.3;
    }

    .article-meta {
      color: var(--text-muted);
      font-size: 0.875rem;
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid var(--border-color);
    }

    .article-meta .badge {
      margin-right: 0.5rem;
      margin-bottom: 0.5rem;
    }

    .article-cover {
      margin-bottom: 1.5rem;
    }

    .article-cover img {
      width: 100%;
      border-radius: 0.5rem;
      box-shadow: 0 0.25rem 0.5rem rgba(0, 0, 0, 0.1);
    }

    .article-tags .tag {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      margin: 0.125rem;
      background: var(--light-bg);
      border: 1px dashed var(--border-color);
      border-radius: 1rem;
      font-size: 0.75rem;
      color: var(--text-muted);
    }

    /* Content Styles */
    .article-content {
      margin-top: 1.5rem;
      font-size: 1.05rem;
    }

    .article-content h1,
    .article-content h2,
    .article-content h3,
    .article-content h4,
    .article-content h5,
    .article-content h6 {
      margin-top: 1.5rem;
      margin-bottom: 0.75rem;
      font-weight: 600;
      color: #212529;
    }

    .article-content h1 { font-size: 1.75rem; }
    .article-content h2 { font-size: 1.5rem; }
    .article-content h3 { font-size: 1.25rem; }
    .article-content h4 { font-size: 1.1rem; }

    .article-content p {
      margin-bottom: 1rem;
    }

    .article-content ul,
    .article-content ol {
      margin-bottom: 1rem;
      padding-left: 1.5rem;
    }

    .article-content li {
      margin-bottom: 0.5rem;
    }

    .article-content blockquote {
      margin: 1rem 0;
      padding: 0.75rem 1rem;
      border-left: 4px solid var(--primary-color);
      background: var(--light-bg);
      color: var(--text-muted);
      font-style: italic;
    }

    .article-content pre {
      background: var(--light-bg);
      border: 1px solid var(--border-color);
      border-radius: 0.375rem;
      padding: 1rem;
      overflow-x: auto;
      margin-bottom: 1rem;
    }

    .article-content code {
      background: var(--light-bg);
      border: 1px solid var(--border-color);
      border-radius: 0.25rem;
      padding: 0.125rem 0.375rem;
      font-size: 0.875rem;
    }

    .article-content pre code {
      background: transparent;
      border: none;
      padding: 0;
    }

    .article-content table {
      width: 100%;
      border-collapse: collapse;
      margin-bottom: 1rem;
    }

    .article-content th,
    .article-content td {
      border: 1px solid var(--border-color);
      padding: 0.75rem;
      text-align: left;
    }

    .article-content th {
      background: var(--light-bg);
      font-weight: 600;
    }

    .article-content img {
      max-width: 100%;
      height: auto;
      border-radius: 0.375rem;
      margin: 1rem 0;
    }

    .article-content a {
      color: var(--primary-color);
      text-decoration: underline;
    }

    .article-content a:hover {
      color: #0b5ed7;
    }

    /* Sidebar Panel */
    .sidebar-panel {
      background: white;
      border-radius: 0.5rem;
      padding: 1.5rem;
      box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
      border: 1px solid var(--border-color);
      position: sticky;
      top: 2rem;
    }

    .sidebar-panel h3 {
      font-size: 1.25rem;
      font-weight: 600;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid var(--primary-color);
    }

    .category-chip {
      display: inline-block;
      padding: 0.5rem 1rem;
      margin: 0.25rem;
      background: var(--light-bg);
      border: 1px solid var(--border-color);
      border-radius: 1.5rem;
      font-size: 0.875rem;
      transition: all 0.3s ease;
    }

    .category-chip:hover {
      background: var(--primary-color);
      color: white;
      border-color: var(--primary-color);
      text-decoration: none;
      transform: translateY(-2px);
      box-shadow: 0 0.25rem 0.5rem rgba(13, 110, 253, 0.25);
    }

    /* Footer */
    .blog-footer {
      background: var(--light-bg);
      border-top: 1px solid var(--border-color);
      padding: 2rem 0;
      margin-top: 3rem;
      text-align: center;
      color: var(--text-muted);
    }

    /* Responsive */
    @media (max-width: 768px) {
      .topbar {
        text-align: center;
        padding: 1.5rem 0;
      }

      .topbar .nav {
        margin-top: 1rem;
      }

      .article-panel {
        padding: 1.5rem 1rem;
      }

      .article-title {
        font-size: 1.5rem;
      }

      .sidebar-panel {
        position: static;
        margin-top: 1.5rem;
      }
    }
  </style>
</head>
<body>

  <!-- Top Bar -->
  <div class="topbar">
    <div class="container">
      <div class="row align-items-center">
        <div class="col-md-8">
          <div class="brand">
            <h1>
              <a href="/blog/${kbase.uid!''}">
                <i class="fas fa-book-open me-2"></i>${(kbase.name)!'Blog'}
              </a>
            </h1>
          </div>
        </div>
        <div class="col-md-4 text-md-end mt-3 mt-md-0">
          <nav class="nav">
            <a class="nav-link" href="/blog/${kbase.uid!''}"><i class="fas fa-home me-1"></i>首页</a>
            <a class="nav-link" href="/blog/${kbase.uid!''}/search.html"><i class="fas fa-search me-1"></i>搜索</a>
            <#if (blog.categoryUid)??>
              <a class="nav-link" href="/blog/${kbase.uid!''}/category/${blog.categoryUid}.html"><i class="fas fa-folder me-1"></i>返回分类</a>
            </#if>
          </nav>
        </div>
      </div>
    </div>
  </div>

  <!-- Main Content -->
  <div class="container">
    <div class="row">

      <!-- Article -->
      <div class="col-lg-8">
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/blog/${kbase.uid!''}"><i class="fas fa-home me-1"></i>首页</a></li>
            <#if (blog.categoryUid)??>
              <li class="breadcrumb-item"><a href="/blog/${kbase.uid!''}/category/${blog.categoryUid}.html"><i class="fas fa-folder me-1"></i>分类</a></li>
            </#if>
            <li class="breadcrumb-item active" aria-current="page">${(blog.name)!''}</li>
          </ol>
        </nav>

        <article class="article-panel">
          <h1 class="article-title">${(blog.name)!blog.uid}</h1>

          <div class="article-meta">
            <#if (blog.published)?? && blog.published == false>
              <span class="badge bg-warning text-dark"><i class="fas fa-eye-slash me-1"></i>未发布</span>
            <#else>
              <span class="badge bg-success"><i class="fas fa-check me-1"></i>已发布</span>
            </#if>
            <#if (blog.description)??>
              <span class="text-muted"><i class="fas fa-info-circle me-1"></i>${(blog.description)!''}</span>
            </#if>
          </div>

          <#if blog.coverImageUrl?? && blog.coverImageUrl?length gt 0>
            <div class="article-cover">
              <img src="${blog.coverImageUrl}" alt="封面图片" />
            </div>
          </#if>

          <#if blog.tagList?? && (blog.tagList?size > 0)>
            <div class="article-tags mb-3">
              <#list blog.tagList as t>
                <span class="tag">#${t}</span>
              </#list>
            </div>
          </#if>

          <div class="article-content">
            <#if blog.contentHtml?? && blog.contentHtml?length gt 0>
              ${blog.contentHtml}
            <#elseif blog.contentMarkdown??>
              <pre>${blog.contentMarkdown}</pre>
            <#else>
              <p class="text-muted">（无内容）</p>
            </#if>
          </div>
        </article>
      </div>

      <!-- Sidebar -->
      <div class="col-lg-4">
        <aside class="sidebar-panel">
          <h3><i class="fas fa-folder me-2"></i>分类</h3>
          <#if categories?? && (categories?size > 0)>
            <div>
              <#list categories as c>
                <a href="/blog/${kbase.uid!''}/category/${c.uid}.html" class="category-chip">
                  <i class="fas fa-folder-open me-1"></i>${(c.name)!c.uid}
                </a>
              </#list>
            </div>
          <#else>
            <p class="text-muted mb-0">暂无分类</p>
          </#if>
        </aside>
      </div>

    </div>
  </div>

  <!-- Footer -->
  <footer class="blog-footer">
    <div class="container">
      <p class="mb-0">
        <i class="fas fa-copyright me-1"></i> ${(currentYear?string('2025'))!'2025'} ${(kbase.name)!'Blog'}. All rights reserved.
      </p>
      <p class="mb-0 mt-2">
        <small>Powered by <a href="https://bytedesk.com" target="_blank">ByteDesk</a></small>
      </p>
    </div>
  </footer>

  <!-- Bootstrap JS -->
  <script src="/assets/vendor/bootstrap5/js/bootstrap.bundle.min.js"></script>

</body>
</html>
