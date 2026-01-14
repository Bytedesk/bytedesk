import{u as a,j as e}from"./index-CJncBc6B.js";const o={title:"GET 查询文章列表",openapi:"GET /api/v1/article"};function l(s){const n={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...a(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:t}=n;return r||i("ApiPlayground"),c||i("RequestExample"),t||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"分页查询知识库文章列表，支持按分类、标签、关键词等筛选。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"GET",endpoint:"/api/v1/article"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于获取知识库中的文章列表。支持按组织、分类、标签等多维度查询，以及关键词搜索。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"orgUid"})," (可选): 组织唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"categoryUid"})," (可选): 分类唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"tag"})," (可选): 标签"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"keyword"})," (可选): 关键词搜索"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"})," (可选): 文章状态（PUBLISHED, DRAFT, ARCHIVED）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"page"})," (可选): 页码，默认为 0"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsx(n.h3,{id:"文章对象字段",children:"文章对象字段"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"uid"}),": 文章唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"title"}),": 文章标题"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"summary"}),": 文章摘要"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"content"}),": 文章内容"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"categoryUid"}),": 所属分类 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"categoryName"}),": 分类名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"tags"}),": 标签数组"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"}),": 文章状态"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"viewCount"}),": 浏览次数"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"helpfulCount"}),": 有用次数"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"commentCount"}),": 评论数"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"authorUid"}),": 作者用户 UID"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"authorName"}),": 作者名称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"isTop"}),": 是否置顶"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"createdAt"}),": 创建时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"updatedAt"}),": 更新时间"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"publishedAt"}),": 发布时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"知识库文章列表展示"}),`
`,e.jsx(n.li,{children:"文章搜索"}),`
`,e.jsx(n.li,{children:"按分类浏览"}),`
`,e.jsx(n.li,{children:"相关文章推荐"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"默认只返回已发布的文章（PUBLISHED）"}),`
`,e.jsx(n.li,{children:"草稿文章需要作者或管理员权限"}),`
`,e.jsx(n.li,{children:"支持模糊搜索标题和内容"}),`
`]}),`
`,e.jsx(t,{children:e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "uid": "article_123456",
        "title": "如何使用 Bytedesk 客服系统",
        "summary": "本文介绍如何快速上手使用 Bytedesk 客服系统...",
        "content": "详细内容...",
        "categoryUid": "category_123456",
        "categoryName": "使用指南",
        "tags": ["客服系统", "入门指南"],
        "status": "PUBLISHED",
        "viewCount": 1523,
        "helpfulCount": 89,
        "commentCount": 12,
        "authorUid": "user_123456",
        "authorName": "张三",
        "isTop": true,
        "createdAt": "2025-01-01T09:00:00Z",
        "updatedAt": "2025-01-05T15:30:00Z",
        "publishedAt": "2025-01-01T10:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalPages": 5,
    "totalElements": 48,
    "last": false,
    "first": true
  }
}
`})})}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`# 查询所有已发布文章
curl -X GET "https://api.weiyuai.cn/api/v1/article?status=PUBLISHED&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"

# 搜索文章
curl -X GET "https://api.weiyuai.cn/api/v1/article?keyword=客服系统&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"

# 按分类查询
curl -X GET "https://api.weiyuai.cn/api/v1/article?categoryUid=category_123456&page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`// 搜索文章
async function searchArticles(keyword, page = 0) {
  const response = await fetch(
    \`https://api.weiyuai.cn/api/v1/article?keyword=\${encodeURIComponent(keyword)}&page=\${page}&size=10\`,
    {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer your_access_token_here',
        'Accept': 'application/json'
      }
    }
  );

  const result = await response.json();
  return result.data;
}

// 使用示例
const articles = await searchArticles('客服系统');
console.log(\`找到 \${articles.totalElements} 篇文章\`);
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

def search_articles(keyword=None, category_uid=None, page=0):
    url = "https://api.weiyuai.cn/api/v1/article"
    headers = {
      "Authorization": "Bearer your_access_token_here",
      "Accept": "application/json"
    }

    params = {"page": page, "size": 10}
    if keyword:
        params["keyword"] = keyword
    if category_uid:
        params["categoryUid"] = category_uid

    response = requests.get(url, headers=headers, params=params)
    result = response.json()
    return result["data"]

# 使用示例
articles = search_articles(keyword="客服系统")
print(f"找到 {articles['totalElements']} 篇文章")
`})})]})]})}function h(s={}){const{wrapper:n}={...a(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(l,{...s})}):l(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
