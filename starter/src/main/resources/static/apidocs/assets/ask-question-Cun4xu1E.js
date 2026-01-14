import{u as p,j as e}from"./index-CJncBc6B.js";const x={title:"AI智能问答",openapi:"post /api/v1/ai/qa/ask"};function l(i){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...p(),...i.components},{ApiPlayground:o,Expandable:a,ParamField:s,RequestExample:d,ResponseExample:c,ResponseField:r}=n;return o||t("ApiPlayground"),a||t("Expandable"),s||t("ParamField"),d||t("RequestExample"),c||t("ResponseExample"),r||t("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"ai智能问答",children:"AI智能问答"}),`
`,e.jsx(n.p,{children:"使用 AI 进行智能问答，支持多轮对话和上下文理解。"}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request POST 'https://api.weiyuai.cn/api/v1/ai/qa/ask' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \\
--header 'Content-Type: application/json' \\
--data-raw '{
  "question": "如何重置密码？",
  "sessionId": "session_123",
  "context": {
    "userId": "user_001",
    "language": "zh-CN"
  }
}'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ai/qa/ask"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN",
    "Content-Type": "application/json"
}
payload = {
    "question": "如何重置密码？",
    "sessionId": "session_123",
    "context": {
        "userId": "user_001",
        "language": "zh-CN"
    }
}

response = requests.post(url, json=payload, headers=headers)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/ai/qa/ask';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN',
  'Content-Type': 'application/json'
};
const data = {
  question: '如何重置密码？',
  sessionId: 'session_123',
  context: {
    userId: 'user_001',
    language: 'zh-CN'
  }
};

axios.post(url, data, { headers })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "answer": "您可以通过以下步骤重置密码：\\n1. 点击登录页面的"忘记密码"\\n2. 输入您的注册邮箱或手机号\\n3. 接收验证码并验证\\n4. 设置新密码\\n5. 完成重置",
    "confidence": 0.95,
    "sources": [
      {
        "type": "knowledge_base",
        "id": "KB20251006001",
        "title": "如何重置密码",
        "relevance": 0.98
      }
    ],
    "suggestions": [
      "密码安全最佳实践",
      "账号安全设置"
    ],
    "sessionId": "session_123",
    "timestamp": "2025-10-06T16:30:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Question is required"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 429,
  "message": "Rate limit exceeded"
}
`})})]}),`
`,e.jsx(n.h2,{id:"请求参数",children:"请求参数"}),`
`,e.jsx(s,{path:"question",type:"string",required:!0,children:e.jsx(n.p,{children:"用户提出的问题"})}),`
`,e.jsx(s,{path:"sessionId",type:"string",children:e.jsx(n.p,{children:"会话ID，用于维持多轮对话上下文"})}),`
`,e.jsxs(s,{path:"context",type:"object",children:[e.jsx(n.p,{children:"上下文信息"}),e.jsxs(a,{title:"Context Properties",children:[e.jsx(s,{path:"userId",type:"string",children:e.jsx(n.p,{children:"用户ID"})}),e.jsx(s,{path:"language",type:"string",default:"zh-CN",children:e.jsx(n.p,{children:"语言代码"})}),e.jsx(s,{path:"department",type:"string",children:e.jsx(n.p,{children:"部门/业务线"})})]})]}),`
`,e.jsxs(s,{path:"options",type:"object",children:[e.jsx(n.p,{children:"配置选项"}),e.jsxs(a,{title:"Options Properties",children:[e.jsx(s,{path:"temperature",type:"number",default:"0.7",children:e.jsx(n.p,{children:"生成温度（0-1）"})}),e.jsx(s,{path:"maxTokens",type:"number",default:"500",children:e.jsx(n.p,{children:"最大生成长度"})}),e.jsx(s,{path:"includeSource",type:"boolean",default:"true",children:e.jsx(n.p,{children:"是否返回知识来源"})})]})]}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(r,{name:"answer",type:"string",children:e.jsx(n.p,{children:"AI 生成的回答"})}),`
`,e.jsx(r,{name:"confidence",type:"number",children:e.jsx(n.p,{children:"回答的置信度（0-1）"})}),`
`,e.jsxs(r,{name:"sources",type:"array",children:[e.jsx(n.p,{children:"回答来源数组"}),e.jsxs(a,{title:"Source Object",children:[e.jsx(r,{name:"type",type:"string",children:e.jsxs(n.p,{children:["来源类型：",e.jsx(n.code,{children:"knowledge_base"}),"、",e.jsx(n.code,{children:"faq"}),"、",e.jsx(n.code,{children:"manual"})]})}),e.jsx(r,{name:"id",type:"string",children:e.jsx(n.p,{children:"来源文档ID"})}),e.jsx(r,{name:"title",type:"string",children:e.jsx(n.p,{children:"来源文档标题"})}),e.jsx(r,{name:"relevance",type:"number",children:e.jsx(n.p,{children:"相关度（0-1）"})})]})]}),`
`,e.jsx(r,{name:"suggestions",type:"array",children:e.jsx(n.p,{children:"相关问题建议"})}),`
`,e.jsx(r,{name:"sessionId",type:"string",children:e.jsx(n.p,{children:"会话ID"})}),`
`,e.jsx(r,{name:"timestamp",type:"string",children:e.jsx(n.p,{children:"回答时间戳"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(o,{method:"POST",endpoint:"/api/v1/ai/qa/ask",description:"AI智能问答"})]})}function j(i={}){const{wrapper:n}={...p(),...i.components};return n?e.jsx(n,{...i,children:e.jsx(l,{...i})}):l(i)}function t(i,n){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{j as default,x as frontmatter};
