import{u as l,j as e}from"./index-CJncBc6B.js";const m={title:"会话历史",openapi:"get /api/v1/ai/qa/history/{sessionId}"};function p(i){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...i.components},{ApiPlayground:t,Expandable:o,ParamField:a,RequestExample:c,ResponseExample:d,ResponseField:s}=n;return t||r("ApiPlayground"),o||r("Expandable"),a||r("ParamField"),c||r("RequestExample"),d||r("ResponseExample"),s||r("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"获取会话历史",children:"获取会话历史"}),`
`,e.jsx(n.p,{children:"获取指定会话的问答历史记录。"}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/ai/qa/history/session_123?page=1&pageSize=20' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ai/qa/history/session_123"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN"
}
params = {
    "page": 1,
    "pageSize": 20
}

response = requests.get(url, headers=headers, params=params)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/ai/qa/history/session_123';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
};
const params = {
  page: 1,
  pageSize: 20
};

axios.get(url, { headers, params })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "sessionId": "session_123",
    "messages": [
      {
        "messageId": "msg_001",
        "role": "user",
        "content": "如何重置密码？",
        "timestamp": "2025-10-06T16:30:00Z"
      },
      {
        "messageId": "msg_002",
        "role": "assistant",
        "content": "您可以通过以下步骤重置密码...",
        "confidence": 0.95,
        "timestamp": "2025-10-06T16:30:05Z"
      },
      {
        "messageId": "msg_003",
        "role": "user",
        "content": "重置后需要重新登录吗？",
        "timestamp": "2025-10-06T16:31:00Z"
      },
      {
        "messageId": "msg_004",
        "role": "assistant",
        "content": "是的，密码重置后需要使用新密码重新登录。",
        "confidence": 0.98,
        "timestamp": "2025-10-06T16:31:03Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 4
    }
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 404,
  "message": "Session not found"
}
`})})]}),`
`,e.jsx(n.h2,{id:"路径参数",children:"路径参数"}),`
`,e.jsx(a,{path:"sessionId",type:"string",required:!0,children:e.jsx(n.p,{children:"会话的唯一标识符"})}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsx(a,{query:"page",type:"number",default:"1",children:e.jsx(n.p,{children:"页码"})}),`
`,e.jsx(a,{query:"pageSize",type:"number",default:"20",children:e.jsx(n.p,{children:"每页数量"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(s,{name:"sessionId",type:"string",children:e.jsx(n.p,{children:"会话ID"})}),`
`,e.jsxs(s,{name:"messages",type:"array",children:[e.jsx(n.p,{children:"消息历史数组"}),e.jsxs(o,{title:"Message Object",children:[e.jsx(s,{name:"messageId",type:"string",children:e.jsx(n.p,{children:"消息ID"})}),e.jsx(s,{name:"role",type:"string",children:e.jsxs(n.p,{children:["角色：",e.jsx(n.code,{children:"user"}),"（用户）、",e.jsx(n.code,{children:"assistant"}),"（AI助手）"]})}),e.jsx(s,{name:"content",type:"string",children:e.jsx(n.p,{children:"消息内容"})}),e.jsx(s,{name:"confidence",type:"number",children:e.jsx(n.p,{children:"置信度（仅助手消息）"})}),e.jsx(s,{name:"timestamp",type:"string",children:e.jsx(n.p,{children:"消息时间戳"})})]})]}),`
`,e.jsx(s,{name:"pagination",type:"object",children:e.jsx(n.p,{children:"分页信息"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(t,{method:"GET",endpoint:"/api/v1/ai/qa/history/session_123",description:"获取会话历史"})]})}function x(i={}){const{wrapper:n}={...l(),...i.components};return n?e.jsx(n,{...i,children:e.jsx(p,{...i})}):p(i)}function r(i,n){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{x as default,m as frontmatter};
