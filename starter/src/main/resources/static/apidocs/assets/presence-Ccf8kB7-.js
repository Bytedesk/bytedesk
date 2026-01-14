import{u as t,j as e}from"./index-CJncBc6B.js";const a={title:"GET 获取用户在线状态",openapi:"GET /api/v1/connection/presence/{userUid}"};function l(s){const n={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:o}=n;return r||i("ApiPlayground"),c||i("RequestExample"),o||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"获取指定用户的在线状态和活跃连接数。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"GET",endpoint:"/api/v1/connection/presence/{userUid}"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于查询指定用户的在线状态，返回用户的在线/离线状态以及活跃的连接数量。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"路径参数",children:"路径参数"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"userUid"})," (必需): 用户唯一标识符"]}),`
`]}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"userUid"}),": 用户唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"isOnline"}),": 是否在线"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"activeConnections"}),": 活跃连接数量"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"lastSeenAt"}),": 最后活跃时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"显示用户在线状态"}),`
`,e.jsx(n.li,{children:"判断用户是否可联系"}),`
`,e.jsx(n.li,{children:"实时通讯状态显示"}),`
`,e.jsx(n.li,{children:"用户活跃度统计"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"需要有效的访问权限才能查询其他用户的状态"}),`
`,e.jsx(n.li,{children:"在线状态实时更新"}),`
`,e.jsx(n.li,{children:"活跃连接数表示同一用户在不同设备上的登录数量"}),`
`]}),`
`,e.jsxs(o,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "userUid": "user_123456",
    "isOnline": true,
    "activeConnections": 2,
    "lastSeenAt": "2025-01-07T10:30:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 404,
  "message": "User not found",
  "data": null
}
`})})]}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/connection/presence/user_123456" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const userUid = 'user_123456';
const response = await fetch(\`https://api.weiyuai.cn/api/v1/connection/presence/\${userUid}\`, {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const result = await response.json();
const presence = result.data;
console.log(\`User is \${presence.isOnline ? 'online' : 'offline'}\`);
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

user_uid = "user_123456"
url = f"https://api.weiyuai.cn/api/v1/connection/presence/{user_uid}"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Accept": "application/json"
}

response = requests.get(url, headers=headers)
result = response.json()
presence = result["data"]
print(f"User is {'online' if presence['isOnline'] else 'offline'}")
`})})]})]})}function h(s={}){const{wrapper:n}={...t(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(l,{...s})}):l(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,a as frontmatter};
