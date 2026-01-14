import{u as a,j as n}from"./index-CJncBc6B.js";const o={title:"POST 创建FAQ",openapi:"POST /api/v1/faq"};function t(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...a(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:d}=e;return r||i("ApiPlayground"),c||i("RequestExample"),d||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"创建一个新的常见问题（FAQ）条目。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/api/v1/faq"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于创建新的 FAQ 条目。FAQ 用于存储常见问题和标准答案，可被智能客服自动引用回复。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "question": "如何重置密码？",
  "answer": "您可以通过以下步骤重置密码：1. 点击登录页面的忘记密码...",
  "categoryUid": "category_123456",
  "tags": ["账号", "密码"],
  "priority": 1,
  "isEnabled": true
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"请求字段",children:"请求字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"question"})," (必需): 问题内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"answer"})," (必需): 答案内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"categoryUid"})," (可选): 分类唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"tags"})," (可选): 标签数组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"})," (可选): 优先级，数字越大优先级越高，默认 0"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"})," (可选): 是否启用，默认 true"]}),`
`]}),`
`,n.jsx(e.h3,{id:"faq-对象字段",children:"FAQ 对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": FAQ 唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"question"}),": 问题内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"answer"}),": 答案内容"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"categoryUid"}),": 分类 UID"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"categoryName"}),": 分类名称"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"tags"}),": 标签数组"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"priority"}),": 优先级"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"isEnabled"}),": 是否启用"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"hitCount"}),": 命中次数"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 创建时间"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"updatedAt"}),": 更新时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"创建常见问题库"}),`
`,n.jsx(e.li,{children:"智能客服自动回复"}),`
`,n.jsx(e.li,{children:"知识库管理"}),`
`,n.jsx(e.li,{children:"FAQ 匹配"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"问题内容应简洁明确"}),`
`,n.jsx(e.li,{children:"答案内容应详细准确"}),`
`,n.jsx(e.li,{children:"可以设置多个相似问题指向同一个答案"}),`
`,n.jsx(e.li,{children:"支持富文本格式"}),`
`]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "faq_123456",
    "question": "如何重置密码？",
    "answer": "您可以通过以下步骤重置密码：1. 点击登录页面的忘记密码...",
    "categoryUid": "category_123456",
    "categoryName": "账号问题",
    "tags": ["账号", "密码"],
    "priority": 1,
    "isEnabled": true,
    "hitCount": 0,
    "createdAt": "2025-01-07T10:30:00Z",
    "updatedAt": "2025-01-07T10:30:00Z"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Question and answer cannot be empty",
  "data": null
}
`})})]}),`
`,n.jsxs(c,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/faq" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "question": "如何重置密码？",
    "answer": "您可以通过以下步骤重置密码：1. 点击登录页面的忘记密码...",
    "categoryUid": "category_123456",
    "tags": ["账号", "密码"],
    "priority": 1,
    "isEnabled": true
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/faq', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    question: '如何重置密码？',
    answer: '您可以通过以下步骤重置密码：1. 点击登录页面的忘记密码...',
    categoryUid: 'category_123456',
    tags: ['账号', '密码'],
    priority: 1,
    isEnabled: true
  })
});

const result = await response.json();
const faq = result.data;
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/faq"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "question": "如何重置密码？",
  "answer": "您可以通过以下步骤重置密码：1. 点击登录页面的忘记密码...",
  "categoryUid": "category_123456",
  "tags": ["账号", "密码"],
  "priority": 1,
  "isEnabled": True
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
faq = result["data"]
`})})]})]})}function h(s={}){const{wrapper:e}={...a(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(t,{...s})}):t(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
