import{u as l,j as e}from"./index-CJncBc6B.js";const h={title:"工单列表",openapi:"get /api/v1/ticket/list"};function d(s){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...s.components},{ApiPlayground:a,ParamField:r,RequestExample:c,ResponseExample:o,ResponseField:t}=n;return a||i("ApiPlayground"),r||i("ParamField"),c||i("RequestExample"),o||i("ResponseExample"),t||i("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"工单列表",children:"工单列表"}),`
`,e.jsx(n.p,{children:"获取工单列表，支持分页和筛选。"}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/ticket/list?page=1&pageSize=20&status=OPEN' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/ticket/list"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN"
}
params = {
    "page": 1,
    "pageSize": 20,
    "status": "OPEN"
}

response = requests.get(url, headers=headers, params=params)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/ticket/list';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
};
const params = {
  page: 1,
  pageSize: 20,
  status: 'OPEN'
};

axios.get(url, { headers, params })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(o,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "tickets": [
      {
        "ticketId": "TK20251006001",
        "title": "系统无法登录",
        "status": "OPEN",
        "priority": "HIGH",
        "category": "technical",
        "createdAt": "2025-10-06T15:30:00Z"
      },
      {
        "ticketId": "TK20251006002",
        "title": "账单查询",
        "status": "OPEN",
        "priority": "MEDIUM",
        "category": "billing",
        "createdAt": "2025-10-06T14:20:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 45
    }
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 401,
  "message": "Invalid or expired access token"
}
`})})]}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsx(r,{query:"page",type:"number",default:"1",children:e.jsx(n.p,{children:"页码"})}),`
`,e.jsx(r,{query:"pageSize",type:"number",default:"20",children:e.jsx(n.p,{children:"每页数量"})}),`
`,e.jsx(r,{query:"status",type:"string",children:e.jsxs(n.p,{children:["筛选工单状态：",e.jsx(n.code,{children:"OPEN"}),"、",e.jsx(n.code,{children:"IN_PROGRESS"}),"、",e.jsx(n.code,{children:"RESOLVED"}),"、",e.jsx(n.code,{children:"CLOSED"})]})}),`
`,e.jsx(r,{query:"priority",type:"string",children:e.jsxs(n.p,{children:["筛选优先级：",e.jsx(n.code,{children:"LOW"}),"、",e.jsx(n.code,{children:"MEDIUM"}),"、",e.jsx(n.code,{children:"HIGH"}),"、",e.jsx(n.code,{children:"URGENT"})]})}),`
`,e.jsx(r,{query:"category",type:"string",children:e.jsx(n.p,{children:"筛选分类"})}),`
`,e.jsx(r,{query:"assigneeId",type:"string",children:e.jsx(n.p,{children:"筛选指派人"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(t,{name:"tickets",type:"array",children:e.jsx(n.p,{children:"工单列表数组"})}),`
`,e.jsx(t,{name:"pagination",type:"object",children:e.jsx(n.p,{children:"分页信息"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(a,{method:"GET",endpoint:"/api/v1/ticket/list",description:"获取工单列表"})]})}function x(s={}){const{wrapper:n}={...l(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(d,{...s})}):d(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{x as default,h as frontmatter};
