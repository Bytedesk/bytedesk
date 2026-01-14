import{u as l,j as e}from"./index-CJncBc6B.js";const h={title:"创建知识库文章",openapi:"post /api/v1/knowledge/article/create"};function o(s){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...s.components},{ApiPlayground:i,ParamField:r,RequestExample:c,ResponseExample:d,ResponseField:t}=n;return i||a("ApiPlayground"),r||a("ParamField"),c||a("RequestExample"),d||a("ResponseExample"),t||a("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"创建知识库文章",children:"创建知识库文章"}),`
`,e.jsx(n.p,{children:"创建一篇新的知识库文章。"}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request POST 'https://api.weiyuai.cn/api/v1/knowledge/article/create' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \\
--header 'Content-Type: application/json' \\
--data-raw '{
  "title": "如何重置密码",
  "content": "详细的密码重置步骤...",
  "category": "account",
  "tags": ["密码", "账号安全"],
  "status": "published"
}'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/knowledge/article/create"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN",
    "Content-Type": "application/json"
}
payload = {
    "title": "如何重置密码",
    "content": "详细的密码重置步骤...",
    "category": "account",
    "tags": ["密码", "账号安全"],
    "status": "published"
}

response = requests.post(url, json=payload, headers=headers)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/knowledge/article/create';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN',
  'Content-Type': 'application/json'
};
const data = {
  title: '如何重置密码',
  content: '详细的密码重置步骤...',
  category: 'account',
  tags: ['密码', '账号安全'],
  status: 'published'
};

axios.post(url, data, { headers })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Article created successfully",
  "data": {
    "articleId": "KB20251006001",
    "title": "如何重置密码",
    "content": "详细的密码重置步骤...",
    "category": "account",
    "tags": ["密码", "账号安全"],
    "status": "published",
    "views": 0,
    "likes": 0,
    "createdAt": "2025-10-06T16:00:00Z",
    "updatedAt": "2025-10-06T16:00:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Invalid request parameters"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 401,
  "message": "Invalid or expired access token"
}
`})})]}),`
`,e.jsx(n.h2,{id:"请求参数",children:"请求参数"}),`
`,e.jsx(r,{path:"title",type:"string",required:!0,children:e.jsx(n.p,{children:"文章标题"})}),`
`,e.jsx(r,{path:"content",type:"string",required:!0,children:e.jsx(n.p,{children:"文章内容（支持 Markdown 格式）"})}),`
`,e.jsx(r,{path:"category",type:"string",required:!0,children:e.jsx(n.p,{children:"文章分类"})}),`
`,e.jsx(r,{path:"tags",type:"array",children:e.jsx(n.p,{children:"标签数组"})}),`
`,e.jsx(r,{path:"status",type:"string",default:"draft",children:e.jsxs(n.p,{children:["文章状态：",e.jsx(n.code,{children:"draft"}),"（草稿）、",e.jsx(n.code,{children:"published"}),"（已发布）、",e.jsx(n.code,{children:"archived"}),"（已归档）"]})}),`
`,e.jsx(r,{path:"summary",type:"string",children:e.jsx(n.p,{children:"文章摘要"})}),`
`,e.jsx(r,{path:"coverImage",type:"string",children:e.jsx(n.p,{children:"封面图片 URL"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(t,{name:"articleId",type:"string",children:e.jsx(n.p,{children:"文章的唯一标识符"})}),`
`,e.jsx(t,{name:"title",type:"string",children:e.jsx(n.p,{children:"文章标题"})}),`
`,e.jsx(t,{name:"content",type:"string",children:e.jsx(n.p,{children:"文章内容"})}),`
`,e.jsx(t,{name:"category",type:"string",children:e.jsx(n.p,{children:"文章分类"})}),`
`,e.jsx(t,{name:"tags",type:"array",children:e.jsx(n.p,{children:"标签数组"})}),`
`,e.jsx(t,{name:"status",type:"string",children:e.jsx(n.p,{children:"文章状态"})}),`
`,e.jsx(t,{name:"views",type:"number",children:e.jsx(n.p,{children:"浏览次数"})}),`
`,e.jsx(t,{name:"likes",type:"number",children:e.jsx(n.p,{children:"点赞数"})}),`
`,e.jsx(t,{name:"createdAt",type:"string",children:e.jsx(n.p,{children:"创建时间（ISO 8601格式）"})}),`
`,e.jsx(t,{name:"updatedAt",type:"string",children:e.jsx(n.p,{children:"更新时间（ISO 8601格式）"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(i,{method:"POST",endpoint:"/api/v1/knowledge/article/create",description:"创建知识库文章"})]})}function x(s={}){const{wrapper:n}={...l(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(o,{...s})}):o(s)}function a(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{x as default,h as frontmatter};
