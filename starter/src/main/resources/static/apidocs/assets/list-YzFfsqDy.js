import{u as d,j as n}from"./index-CJncBc6B.js";const a={title:"GET 查询在线连接",openapi:"GET /api/v1/connection"};function o(s){const e={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...d(),...s.components},{ApiPlayground:c,RequestExample:r,ResponseExample:t}=e;return c||i("ApiPlayground"),r||i("RequestExample"),t||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"查询用户的在线连接信息，包括设备类型、连接状态等。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(c,{method:"GET",endpoint:"/api/v1/connection"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"此接口用于获取当前用户的在线连接列表，支持分页查询。连接信息包括设备类型、操作系统、浏览器、最后活跃时间等。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,n.jsx(e.h2,{id:"查询参数",children:"查询参数"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"page"})," (可选): 页码，默认为 0"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsx(e.h3,{id:"连接对象字段",children:"连接对象字段"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"uid"}),": 连接唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"userUid"}),": 用户唯一标识符"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"deviceType"}),": 设备类型（WEB, DESKTOP, MOBILE, TABLET）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"os"}),": 操作系统"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"browser"}),": 浏览器"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"ipAddress"}),": IP 地址"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"status"}),": 连接状态（ACTIVE, INACTIVE）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"lastActiveAt"}),": 最后活跃时间"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"createdAt"}),": 连接创建时间"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"查看当前登录的设备"}),`
`,n.jsx(e.li,{children:"账号安全管理"}),`
`,n.jsx(e.li,{children:"异常登录检测"}),`
`,n.jsx(e.li,{children:"多设备管理"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"只返回当前用户的连接信息"}),`
`,n.jsx(e.li,{children:"会在用户离线后保留一段时间的历史记录"}),`
`,n.jsx(e.li,{children:"支持强制下线某个连接"}),`
`]}),`
`,n.jsx(t,{children:n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "uid": "conn_123456",
        "userUid": "user_123456",
        "deviceType": "WEB",
        "os": "macOS",
        "browser": "Chrome",
        "ipAddress": "192.168.1.100",
        "status": "ACTIVE",
        "lastActiveAt": "2025-01-07T10:30:00Z",
        "createdAt": "2025-01-07T09:00:00Z"
      },
      {
        "uid": "conn_123457",
        "userUid": "user_123456",
        "deviceType": "MOBILE",
        "os": "iOS",
        "browser": "Safari",
        "ipAddress": "192.168.1.101",
        "status": "ACTIVE",
        "lastActiveAt": "2025-01-07T10:25:00Z",
        "createdAt": "2025-01-07T08:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "first": true
  }
}
`})})}),`
`,n.jsxs(r,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/connection?page=0&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/connection?page=0&size=10', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const result = await response.json();
const connections = result.data.content;
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/connection"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Accept": "application/json"
}
params = {
  "page": 0,
  "size": 10
}

response = requests.get(url, headers=headers, params=params)
result = response.json()
connections = result["data"]["content"]
`})})]})]})}function h(s={}){const{wrapper:e}={...d(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(o,{...s})}):o(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,a as frontmatter};
