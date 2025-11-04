import{u as l,j as e}from"./index-Cfp_hilu.js";const h={title:"发起呼叫",openapi:"post /api/v1/call/make"};function d(r){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...r.components},{ApiPlayground:t,ParamField:s,RequestExample:o,ResponseExample:c,ResponseField:a}=n;return t||i("ApiPlayground"),s||i("ParamField"),o||i("RequestExample"),c||i("ResponseExample"),a||i("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"发起呼叫",children:"发起呼叫"}),`
`,e.jsx(n.p,{children:"发起一个呼叫中心通话请求。"}),`
`,e.jsxs(o,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request POST 'https://api.weiyuai.cn/api/v1/call/make' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \\
--header 'Content-Type: application/json' \\
--data-raw '{
  "phoneNumber": "+86-13800138000",
  "callerId": "service_001",
  "type": "outbound"
}'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/call/make"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN",
    "Content-Type": "application/json"
}
payload = {
    "phoneNumber": "+86-13800138000",
    "callerId": "service_001",
    "type": "outbound"
}

response = requests.post(url, json=payload, headers=headers)
print(response.json())
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');

const url = 'https://api.weiyuai.cn/api/v1/call/make';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN',
  'Content-Type': 'application/json'
};
const data = {
  phoneNumber: '+86-13800138000',
  callerId: 'service_001',
  type: 'outbound'
};

axios.post(url, data, { headers })
  .then(response => console.log(response.data))
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Call initiated successfully",
  "data": {
    "callId": "call_1234567890",
    "phoneNumber": "+86-13800138000",
    "status": "ringing",
    "createdAt": "2025-10-06T10:30:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "Invalid phone number format"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 401,
  "message": "Invalid or expired access token"
}
`})})]}),`
`,e.jsx(n.h2,{id:"请求参数",children:"请求参数"}),`
`,e.jsx(s,{path:"phoneNumber",type:"string",required:!0,children:e.jsx(n.p,{children:"被叫号码，需要包含国家代码"})}),`
`,e.jsx(s,{path:"callerId",type:"string",required:!0,children:e.jsx(n.p,{children:"呼叫发起者的ID"})}),`
`,e.jsx(s,{path:"type",type:"string",required:!0,children:e.jsxs(n.p,{children:["呼叫类型：",e.jsx(n.code,{children:"inbound"}),"（呼入）或 ",e.jsx(n.code,{children:"outbound"}),"（呼出）"]})}),`
`,e.jsx(s,{path:"metadata",type:"object",children:e.jsx(n.p,{children:"附加元数据，可用于业务标识"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsx(a,{name:"callId",type:"string",children:e.jsx(n.p,{children:"呼叫会话的唯一标识符"})}),`
`,e.jsx(a,{name:"phoneNumber",type:"string",children:e.jsx(n.p,{children:"被叫号码"})}),`
`,e.jsx(a,{name:"status",type:"string",children:e.jsxs(n.p,{children:["呼叫状态：",e.jsx(n.code,{children:"ringing"}),"（振铃中）、",e.jsx(n.code,{children:"connected"}),"（已接通）、",e.jsx(n.code,{children:"ended"}),"（已结束）"]})}),`
`,e.jsx(a,{name:"createdAt",type:"string",children:e.jsx(n.p,{children:"呼叫创建时间（ISO 8601格式）"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(t,{method:"POST",endpoint:"/api/v1/call/make",description:"发起呼叫"})]})}function u(r={}){const{wrapper:n}={...l(),...r.components};return n?e.jsx(n,{...r,children:e.jsx(d,{...r})}):d(r)}function i(r,n){throw new Error("Expected component `"+r+"` to be defined: you likely forgot to import, pass, or provide it.")}export{u as default,h as frontmatter};
