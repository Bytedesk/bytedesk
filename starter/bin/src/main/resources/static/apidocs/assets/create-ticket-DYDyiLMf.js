import{u as l,j as e}from"./index-Cfp_hilu.js";const h={title:"创建工单",openapi:"post /api/v1/ticket/create"};function o(i){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...i.components},{ApiPlayground:c,ParamField:t,RequestExample:a,ResponseExample:d,ResponseField:r}=n;return c||s("ApiPlayground"),t||s("ParamField"),a||s("RequestExample"),d||s("ResponseExample"),r||s("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"创建工单",children:"创建工单"}),`
`,e.jsx(n.p,{children:"创建一个新的工单记录。"}),`
`,e.jsxs(a,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request POST 'https://api.weiyuai.cn/api/v1/ticket/create' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \\
--header 'Content-Type: application/json' \\
--data-raw '{
  "title": "系统无法登录",
  "description": "用户反馈无法登录系统，提示密码错误",
  "priority": "HIGH",
  "category": "technical",
  "assigneeId": "agent_001"
}'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ticket/create"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN",
    "Content-Type": "application/json"
}
payload = {
    "title": "系统无法登录",
    "description": "用户反馈无法登录系统，提示密码错误",
    "priority": "HIGH",
    "category": "technical",
    "assigneeId": "agent_001"
}

response = requests.post(url, json=payload, headers=headers)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/ticket/create';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN',
  'Content-Type': 'application/json'
};
const data = {
  title: '系统无法登录',
  description: '用户反馈无法登录系统，提示密码错误',
  priority: 'HIGH',
  category: 'technical',
  assigneeId: 'agent_001'
};

axios.post(url, data, { headers })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Ticket created successfully",
  "data": {
    "ticketId": "TK20251006001",
    "title": "系统无法登录",
    "description": "用户反馈无法登录系统，提示密码错误",
    "status": "OPEN",
    "priority": "HIGH",
    "category": "technical",
    "assigneeId": "agent_001",
    "createdAt": "2025-10-06T15:30:00Z",
    "updatedAt": "2025-10-06T15:30:00Z"
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
`,e.jsx(t,{path:"title",type:"string",required:!0,children:e.jsx(n.p,{children:"工单标题"})}),`
`,e.jsx(t,{path:"description",type:"string",required:!0,children:e.jsx(n.p,{children:"工单详细描述"})}),`
`,e.jsx(t,{path:"priority",type:"string",required:!0,children:e.jsxs(n.p,{children:["优先级：",e.jsx(n.code,{children:"LOW"}),"（低）、",e.jsx(n.code,{children:"MEDIUM"}),"（中）、",e.jsx(n.code,{children:"HIGH"}),"（高）、",e.jsx(n.code,{children:"URGENT"}),"（紧急）"]})}),`
`,e.jsx(t,{path:"category",type:"string",required:!0,children:e.jsxs(n.p,{children:["工单分类：",e.jsx(n.code,{children:"technical"}),"（技术）、",e.jsx(n.code,{children:"billing"}),"（账单）、",e.jsx(n.code,{children:"general"}),"（一般咨询）"]})}),`
`,e.jsx(t,{path:"assigneeId",type:"string",children:e.jsx(n.p,{children:"指派的客服人员ID"})}),`
`,e.jsx(t,{path:"tags",type:"array",children:e.jsx(n.p,{children:"工单标签数组"})}),`
`,e.jsx(t,{path:"customFields",type:"object",children:e.jsx(n.p,{children:"自定义字段对象"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(r,{name:"ticketId",type:"string",children:e.jsx(n.p,{children:"工单的唯一标识符"})}),`
`,e.jsx(r,{name:"title",type:"string",children:e.jsx(n.p,{children:"工单标题"})}),`
`,e.jsx(r,{name:"description",type:"string",children:e.jsx(n.p,{children:"工单描述"})}),`
`,e.jsx(r,{name:"status",type:"string",children:e.jsxs(n.p,{children:["工单状态：",e.jsx(n.code,{children:"OPEN"}),"（待处理）、",e.jsx(n.code,{children:"IN_PROGRESS"}),"（处理中）、",e.jsx(n.code,{children:"RESOLVED"}),"（已解决）、",e.jsx(n.code,{children:"CLOSED"}),"（已关闭）"]})}),`
`,e.jsx(r,{name:"priority",type:"string",children:e.jsx(n.p,{children:"优先级"})}),`
`,e.jsx(r,{name:"category",type:"string",children:e.jsx(n.p,{children:"工单分类"})}),`
`,e.jsx(r,{name:"assigneeId",type:"string",children:e.jsx(n.p,{children:"指派的客服人员ID"})}),`
`,e.jsx(r,{name:"createdAt",type:"string",children:e.jsx(n.p,{children:"创建时间（ISO 8601格式）"})}),`
`,e.jsx(r,{name:"updatedAt",type:"string",children:e.jsx(n.p,{children:"更新时间（ISO 8601格式）"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(c,{method:"POST",endpoint:"/api/v1/ticket/create",description:"创建工单"})]})}function x(i={}){const{wrapper:n}={...l(),...i.components};return n?e.jsx(n,{...i,children:e.jsx(o,{...i})}):o(i)}function s(i,n){throw new Error("Expected component `"+i+"` to be defined: you likely forgot to import, pass, or provide it.")}export{x as default,h as frontmatter};
