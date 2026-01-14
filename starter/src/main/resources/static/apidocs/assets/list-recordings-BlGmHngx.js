import{u as l,j as e}from"./index-CJncBc6B.js";const x={title:"录音列表",openapi:"get /api/v1/recording/list"};function p(s){const n={code:"code",h1:"h1",h2:"h2",p:"p",pre:"pre",...l(),...s.components},{ApiPlayground:t,Expandable:c,ParamField:i,RequestExample:d,ResponseExample:o,ResponseField:r}=n;return t||a("ApiPlayground"),c||a("Expandable"),i||a("ParamField"),d||a("RequestExample"),o||a("ResponseExample"),r||a("ResponseField"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"录音列表",children:"录音列表"}),`
`,e.jsx(n.p,{children:"获取呼叫录音列表。"}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/recording/list?page=1&pageSize=20' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/recording/list"
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

const url = 'https://api.weiyuai.cn/api/v1/recording/list';
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
`,e.jsxs(o,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "Success",
  "data": {
    "recordings": [
      {
        "recordingId": "rec_1234567890",
        "callId": "call_1234567890",
        "url": "https://cdn.weiyuai.cn/recordings/rec_1234567890.mp3",
        "duration": 180,
        "size": 2048576,
        "format": "mp3",
        "createdAt": "2025-10-06T10:33:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 50
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
`,e.jsx(i,{query:"callId",type:"string",children:e.jsx(n.p,{children:"筛选指定呼叫的录音"})}),`
`,e.jsx(i,{query:"startTime",type:"string",children:e.jsx(n.p,{children:"开始时间（ISO 8601格式）"})}),`
`,e.jsx(i,{query:"endTime",type:"string",children:e.jsx(n.p,{children:"结束时间（ISO 8601格式）"})}),`
`,e.jsx(n.h2,{id:"响应字段",children:"响应字段"}),`
`,e.jsxs(r,{name:"recordings",type:"array",children:[e.jsx(n.p,{children:"录音记录数组"}),e.jsxs(c,{title:"Recording Object",children:[e.jsx(r,{name:"recordingId",type:"string",children:e.jsx(n.p,{children:"录音ID"})}),e.jsx(r,{name:"callId",type:"string",children:e.jsx(n.p,{children:"关联的呼叫ID"})}),e.jsx(r,{name:"url",type:"string",children:e.jsx(n.p,{children:"录音文件下载地址"})}),e.jsx(r,{name:"duration",type:"number",children:e.jsx(n.p,{children:"录音时长（秒）"})}),e.jsx(r,{name:"size",type:"number",children:e.jsx(n.p,{children:"文件大小（字节）"})}),e.jsx(r,{name:"format",type:"string",children:e.jsx(n.p,{children:"录音格式（mp3/wav）"})}),e.jsx(r,{name:"createdAt",type:"string",children:e.jsx(n.p,{children:"创建时间"})})]})]}),`
`,e.jsx(r,{name:"pagination",type:"object",children:e.jsx(n.p,{children:"分页信息"})}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(t,{method:"GET",endpoint:"/api/v1/recording/list",description:"获取录音列表"})]})}function j(s={}){const{wrapper:n}={...l(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(p,{...s})}):p(s)}function a(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{j as default,x as frontmatter};
