import{u as o,j as e}from"./index-Cfp_hilu.js";const x={title:"呼叫列表",openapi:"get /api/v1/call/list"};function p(r){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...o(),...r.components},{ApiPlayground:t,Expandable:l,ParamField:i,RequestExample:c,ResponseExample:d,ResponseField:s}=n;return t||a("ApiPlayground"),l||a("Expandable"),i||a("ParamField"),c||a("RequestExample"),d||a("ResponseExample"),s||a("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"呼叫列表",children:"呼叫列表"}),`
`,e.jsx(n.p,{children:"获取呼叫中心的呼叫记录列表。"}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/call/list?page=1&pageSize=20&status=completed' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/call/list"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN"
}
params = {
    "page": 1,
    "pageSize": 20,
    "status": "completed"
}

response = requests.get(url, headers=headers, params=params)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/call/list';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
};
const params = {
  page: 1,
  pageSize: 20,
  status: 'completed'
};

axios.get(url, { headers, params })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "calls": [
      {
        "callId": "call_1234567890",
        "phoneNumber": "+86-13800138000",
        "callerId": "service_001",
        "type": "outbound",
        "status": "completed",
        "duration": 180,
        "startTime": "2025-10-06T10:30:00Z",
        "endTime": "2025-10-06T10:33:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100
    }
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 401,
  "message": "Invalid or expired access token"
}
`})})]}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsx(i,{query:"page",type:"number",default:"1",children:e.jsx(n.p,{children:"页码"})}),`
`,e.jsx(i,{query:"pageSize",type:"number",default:"20",children:e.jsx(n.p,{children:"每页数量"})}),`
`,e.jsx(i,{query:"status",type:"string",children:e.jsxs(n.p,{children:["筛选呼叫状态：",e.jsx(n.code,{children:"ringing"}),"、",e.jsx(n.code,{children:"connected"}),"、",e.jsx(n.code,{children:"completed"}),"、",e.jsx(n.code,{children:"failed"})]})}),`
`,e.jsx(i,{query:"startTime",type:"string",children:e.jsx(n.p,{children:"开始时间（ISO 8601格式）"})}),`
`,e.jsx(i,{query:"endTime",type:"string",children:e.jsx(n.p,{children:"结束时间（ISO 8601格式）"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsxs(s,{name:"calls",type:"array",children:[e.jsx(n.p,{children:"呼叫记录数组"}),e.jsxs(l,{title:"Call Object",children:[e.jsx(s,{name:"callId",type:"string",children:e.jsx(n.p,{children:"呼叫ID"})}),e.jsx(s,{name:"phoneNumber",type:"string",children:e.jsx(n.p,{children:"被叫号码"})}),e.jsx(s,{name:"callerId",type:"string",children:e.jsx(n.p,{children:"呼叫发起者ID"})}),e.jsx(s,{name:"type",type:"string",children:e.jsx(n.p,{children:"呼叫类型"})}),e.jsx(s,{name:"status",type:"string",children:e.jsx(n.p,{children:"呼叫状态"})}),e.jsx(s,{name:"duration",type:"number",children:e.jsx(n.p,{children:"通话时长（秒）"})}),e.jsx(s,{name:"startTime",type:"string",children:e.jsx(n.p,{children:"开始时间"})}),e.jsx(s,{name:"endTime",type:"string",children:e.jsx(n.p,{children:"结束时间"})})]})]}),`
`,e.jsx(s,{name:"pagination",type:"object",children:e.jsx(n.p,{children:"分页信息"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(t,{method:"GET",endpoint:"/api/v1/call/list",description:"获取呼叫列表"})]})}function j(r={}){const{wrapper:n}={...o(),...r.components};return n?e.jsx(n,{...r,children:e.jsx(p,{...r})}):p(r)}function a(r,n){throw new Error("Expected component `"+r+"` to be defined: you likely forgot to import, pass, or provide it.")}export{j as default,x as frontmatter};
