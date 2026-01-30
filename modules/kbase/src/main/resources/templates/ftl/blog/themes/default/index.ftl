<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${(kbase.name)!'Blog'}</title>
  <meta name="description" content="${(kbase.descriptionHtml)!''}">
  <meta name="keywords" content="${(kbase.keywords)!''}">

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
      line-height: 1.6;
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
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 0.5rem;
      color: white;
    }

    .topbar .brand p {
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 0;
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

    /* Section */
    .section {
      background: white;
      border-radius: 0.5rem;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
      border: 1px solid var(--border-color);
    }

    .section h2 {
      font-size: 1.5rem;
      font-weight: 600;
      margin-bottom: 1rem;
      color: #212529;
      padding-bottom: 0.75rem;
      border-bottom: 2px solid var(--primary-color);
    }

    /* Category Chips */
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

    /* Blog Card */
    .blog-card {
      background: white;
      border: 1px solid var(--border-color);
      border-radius: 0.5rem;
      padding: 1.25rem;
      height: 100%;
      transition: all 0.3s ease;
    }

    .blog-card:hover {
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
      transform: translateY(-4px);
      border-color: var(--primary-color);
    }

    .blog-card h3 {
      font-size: 1.25rem;
      font-weight: 600;
      margin-bottom: 0.75rem;
    }

    .blog-card h3 a {
      color: #212529;
    }

    .blog-card h3 a:hover {
      color: var(--primary-color);
    }

    .blog-card .meta {
      color: var(--text-muted);
      font-size: 0.875rem;
      margin-bottom: 0.75rem;
    }

    .blog-card .tag {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      margin: 0.125rem;
      background: var(--light-bg);
      border: 1px dashed var(--border-color);
      border-radius: 1rem;
      font-size: 0.75rem;
      color: var(--text-muted);
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: 3rem 1rem;
      color: var(--text-muted);
    }

    .empty-state i {
      font-size: 3rem;
      margin-bottom: 1rem;
      opacity: 0.5;
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

      .section {
        padding: 1rem;
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
            <h1><i class="fas fa-book-open me-2"></i>${(kbase.name)!'Blog'}</h1>
            <p>${(kbase.descriptionHtml)!''}</p>
          </div>
        </div>
        <div class="col-md-4 text-md-end mt-3 mt-md-0">
          <nav class="nav">
            <a class="nav-link" href="/blog/${kbase.uid!''}"><i class="fas fa-home me-1"></i>首页</a>
            <a class="nav-link" href="/blog/${kbase.uid!''}/search.html"><i class="fas fa-search me-1"></i>搜索</a>
          </nav>
        </div>
      </div>
    </div>
  </div>

  <!-- Main Content -->
  <div class="container">

    <!-- Categories Section -->
    <section class="section">
      <h2><i class="fas fa-folder me-2"></i>分类</h2>
      <#if categories?? && (categories?size > 0)>
        <div class="text-center">
          <#list categories as c>
            <a href="/blog/${kbase.uid!''}/category/${c.uid}.html" class="category-chip">
              <i class="fas fa-folder-open me-1"></i>${(c.name)!c.uid}
            </a>
          </#list>
        </div>
      <#else>
        <div class="empty-state">
          <i class="fas fa-folder-open"></i>
          <p>暂无分类</p>
        </div>
      </#if>
    </section>

    <!-- Posts Section -->
    <section class="section">
      <h2><i class="fas fa-file-alt me-2"></i>文章</h2>
      <#if blogs?? && (blogs?size > 0)>
        <div class="row row-cols-1 row-cols-md-2 g-4">
          <#list blogs as b>
            <div class="col">
              <div class="blog-card">
                <h3>
                  <a href="/blog/${kbase.uid!''}/post/${b.uid}.html">
                    <i class="fas fa-file-text me-2"></i>${(b.name)!b.uid}
                  </a>
                </h3>
                <div class="meta">
                  <#if (b.published)?? && b.published == false>
                    <span class="badge bg-warning text-dark"><i class="fas fa-eye-slash me-1"></i>未发布</span>
                  <#else>
                    <span class="badge bg-success"><i class="fas fa-check me-1"></i>已发布</span>
                  </#if>
                </div>
                <div class="meta">
                  ${(b.description)!''}
                </div>
                <#if b.tagList?? && (b.tagList?size > 0)>
                  <div class="mt-3">
                    <#list b.tagList as t>
                      <span class="tag">#${t}</span>
                    </#list>
                  </div>
                </#if>
              </div>
            </div>
          </#list>
        </div>
      <#else>
        <div class="empty-state">
          <i class="fas fa-file-alt"></i>
          <p>暂无内容</p>
        </div>
      </#if>
    </section>

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
