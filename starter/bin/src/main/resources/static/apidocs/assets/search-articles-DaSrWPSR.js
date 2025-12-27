import{u as p,j as e}from"./index-Cfp_hilu.js";const x={title:"搜索知识库",openapi:"get /api/v1/knowledge/search"};function o(r){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...p(),...r.components},{ApiPlayground:t,Expandable:c,ParamField:s,RequestExample:l,ResponseExample:d,ResponseField:a}=n;return t||i("ApiPlayground"),c||i("Expandable"),s||i("ParamField"),l||i("RequestExample"),d||i("ResponseExample"),a||i("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"搜索知识库",children:"搜索知识库"}),`
`,e.jsx(n.p,{children:"在知识库中搜索相关文章。"}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/knowledge/search?q=密码&page=1&pageSize=10' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/knowledge/search"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN"
}
params = {
    "q": "密码",
    "page": 1,
    "pageSize": 10
}

response = requests.get(url, headers=headers, params=params)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/knowledge/search';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
};
const params = {
  q: '密码',
  page: 1,
  pageSize: 10
};

axios.get(url, { headers, params })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "articles": [
      {
        "articleId": "KB20251006001",
        "title": "如何重置密码",
        "summary": "详细介绍密码重置的步骤和注意事项",
        "category": "account",
        "tags": ["密码", "账号安全"],
        "views": 1523,
        "likes": 89,
        "relevance": 0.95,
        "createdAt": "2025-10-06T16:00:00Z"
      },
      {
        "articleId": "KB20251005002",
        "title": "密码安全最佳实践",
        "summary": "如何设置强密码以保护账号安全",
        "category": "security",
        "tags": ["密码", "安全"],
        "views": 2341,
        "likes": 156,
        "relevance": 0.87,
        "createdAt": "2025-10-05T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 10,
      "total": 25
    }
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Search query is required"
}
`})})]}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsx(s,{query:"q",type:"string",required:!0,children:e.jsx(n.p,{children:"搜索关键词"})}),`
`,e.jsx(s,{query:"page",type:"number",default:"1",children:e.jsx(n.p,{children:"页码"})}),`
`,e.jsx(s,{query:"pageSize",type:"number",default:"10",children:e.jsx(n.p,{children:"每页数量"})}),`
`,e.jsx(s,{query:"category",type:"string",children:e.jsx(n.p,{children:"筛选分类"})}),`
`,e.jsx(s,{query:"tags",type:"string",children:e.jsx(n.p,{children:"筛选标签（多个标签用逗号分隔）"})}),`
`,e.jsx(s,{query:"sortBy",type:"string",default:"relevance",children:e.jsxs(n.p,{children:["排序方式：",e.jsx(n.code,{children:"relevance"}),"（相关度）、",e.jsx(n.code,{children:"views"}),"（浏览量）、",e.jsx(n.code,{children:"likes"}),"（点赞数）、",e.jsx(n.code,{children:"date"}),"（时间）"]})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsxs(a,{name:"articles",type:"array",children:[e.jsx(n.p,{children:"文章列表数组"}),e.jsxs(c,{title:"Article Object",children:[e.jsx(a,{name:"articleId",type:"string",children:e.jsx(n.p,{children:"文章ID"})}),e.jsx(a,{name:"title",type:"string",children:e.jsx(n.p,{children:"文章标题"})}),e.jsx(a,{name:"summary",type:"string",children:e.jsx(n.p,{children:"文章摘要"})}),e.jsx(a,{name:"relevance",type:"number",children:e.jsx(n.p,{children:"搜索相关度（0-1）"})})]})]}),`
`,e.jsx(a,{name:"pagination",type:"object",children:e.jsx(n.p,{children:"分页信息"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(t,{method:"GET",endpoint:"/api/v1/knowledge/search",description:"搜索知识库"})]})}function u(r={}){const{wrapper:n}={...p(),...r.components};return n?e.jsx(n,{...r,children:e.jsx(o,{...r})}):o(r)}function i(r,n){throw new Error("Expected component `"+r+"` to be defined: you likely forgot to import, pass, or provide it.")}export{u as default,x as frontmatter};
