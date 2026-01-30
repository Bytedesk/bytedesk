<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${(kbase.name)!'Blog'} - 搜索</title>
  <meta name="description" content="搜索知识库文章">

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

    /* Search Panel */
    .search-panel {
      background: white;
      border-radius: 0.5rem;
      padding: 2rem;
      box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
      border: 1px solid var(--border-color);
      margin-bottom: 1.5rem;
    }

    .search-panel h2 {
      font-size: 1.5rem;
      font-weight: 600;
      margin-bottom: 1.5rem;
      padding-bottom: 0.75rem;
      border-bottom: 2px solid var(--primary-color);
    }

    .search-form {
      margin-bottom: 1.5rem;
    }

    .search-input-group {
      position: relative;
    }

    .search-input-group input {
      padding-right: 3rem;
      border-radius: 0.5rem;
      border: 1px solid var(--border-color);
      height: 3rem;
      font-size: 1rem;
    }

    .search-input-group input:focus {
      border-color: var(--primary-color);
      box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
    }

    .search-input-group button {
      position: absolute;
      right: 0.5rem;
      top: 50%;
      transform: translateY(-50%);
      border: none;
      background: var(--primary-color);
      color: white;
      width: 2.5rem;
      height: 2.5rem;
      border-radius: 0.375rem;
      transition: all 0.3s ease;
    }

    .search-input-group button:hover {
      background: #0b5ed7;
    }

    .search-hint {
      background: var(--light-bg);
      border: 1px dashed var(--border-color);
      border-radius: 0.5rem;
      padding: 1rem 1.25rem;
      font-size: 0.875rem;
      color: var(--text-muted);
    }

    .search-hint code {
      background: white;
      border: 1px solid var(--border-color);
      border-radius: 0.25rem;
      padding: 0.125rem 0.5rem;
      font-size: 0.8rem;
      color: var(--danger-color);
    }

    .search-info {
      display: inline-block;
      padding: 0.5rem 1rem;
      background: var(--info-color);
      color: white;
      border-radius: 1.5rem;
      font-size: 0.875rem;
      margin-bottom: 1rem;
    }

    /* API Info */
    .api-info {
      background: #fff3cd;
      border: 1px solid #ffc107;
      border-radius: 0.5rem;
      padding: 1rem 1.25rem;
      margin-top: 1.5rem;
    }

    .api-info h6 {
      font-weight: 600;
      margin-bottom: 0.75rem;
      color: #856404;
    }

    .api-info code {
      background: white;
      border: 1px solid #ffc107;
      border-radius: 0.25rem;
      padding: 0.125rem 0.5rem;
      font-size: 0.8rem;
      color: #856404;
    }

    .api-info ul {
      margin-bottom: 0;
      font-size: 0.875rem;
      color: #856404;
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: 3rem 1rem;
      color: var(--text-muted);
    }

    .empty-state i {
      font-size: 4rem;
      margin-bottom: 1.5rem;
      opacity: 0.3;
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

      .search-panel {
        padding: 1.5rem 1rem;
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
          </nav>
        </div>
      </div>
    </div>
  </div>

  <!-- Main Content -->
  <div class="container">
    <nav aria-label="breadcrumb">
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="/blog/${kbase.uid!''}"><i class="fas fa-home me-1"></i>首页</a></li>
        <li class="breadcrumb-item active" aria-current="page">搜索</li>
      </ol>
    </nav>

    <div class="search-panel">
      <h2><i class="fas fa-search me-2"></i>搜索文章</h2>

      <!-- Search Form -->
      <form class="search-form" id="searchForm">
        <div class="search-input-group">
          <input
            type="text"
            class="form-control"
            id="searchInput"
            placeholder="输入关键词搜索..."
            <#if content?? && content?length gt 0>value="${content}"</#if>
          >
          <button type="submit" title="搜索">
            <i class="fas fa-search"></i>
          </button>
        </div>
      </form>

      <!-- Current Keyword Info -->
      <#if content?? && content?length gt 0>
        <div class="search-info">
          <i class="fas fa-tag me-1"></i> 当前关键词: <strong>${content}</strong>
        </div>
      </#if>

      <!-- Hint -->
      <div class="search-hint">
        <i class="fas fa-info-circle me-2"></i>
        此页面为静态模板占位。你可以在此处接入前端搜索功能。
      </div>

      <!-- API Documentation -->
      <div class="api-info">
        <h6><i class="fas fa-code me-2"></i>可用的搜索接口</h6>
        <ul class="mb-2">
          <li><code>POST /api/v1/blog/query/org</code> - 组织级博客查询接口</li>
          <li><code>GET /api/v1/kbase/blog/search</code> - 知识库文章搜索接口</li>
          <li>或者使用自定义的 visitor 查询接口</li>
        </ul>
        <small class="text-muted">
          <i class="fas fa-lightbulb me-1"></i>
          提示：使用 JavaScript 调用上述接口实现实时搜索
        </small>
      </div>
    </div>

    <!-- Empty State (Initial) -->
    <#if !content?? || content?length == 0>
      <div class="empty-state">
        <i class="fas fa-search"></i>
        <h4>开始搜索</h4>
        <p>在上方输入框中输入关键词，搜索知识库中的文章</p>
      </div>
    </#if>

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

  <!-- Search Form Handler -->
  <script>
    document.getElementById('searchForm').addEventListener('submit', function(e) {
      e.preventDefault();
      const keyword = document.getElementById('searchInput').value.trim();
      if (keyword) {
        // TODO: 调用搜索接口
        console.log('搜索关键词:', keyword);

        // 示例：调用 API
        // fetch('/api/v1/blog/query/org?keyword=' + encodeURIComponent(keyword))
        //   .then(response => response.json())
        //   .then(data => {
        //     // 处理搜索结果
        //     console.log('搜索结果:', data);
        //   })
        //   .catch(error => {
        //     console.error('搜索失败:', error);
        //   });

        alert('搜索功能需要接入后端 API\n关键词: ' + keyword);
      }
    });
  </script>

</body>
</html>
