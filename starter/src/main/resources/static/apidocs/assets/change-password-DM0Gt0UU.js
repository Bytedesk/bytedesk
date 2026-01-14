import{u as c,j as n}from"./index-CJncBc6B.js";const t={title:"POST 修改密码",openapi:"POST /api/v1/user/change/password"};function i(s){const e={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...s.components},{ApiPlayground:o,RequestExample:a,ResponseExample:d}=e;return o||r("ApiPlayground"),a||r("RequestExample"),d||r("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"用户修改自己的登录密码，需要提供旧密码和新密码。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(o,{method:"POST",endpoint:"/api/v1/user/change/password"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"当前登录用户可以通过此接口修改自己的密码。需要提供旧密码进行验证。"}),`
`,n.jsx(e.h2,{id:"请求头",children:"请求头"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,n.jsx(e.h2,{id:"请求体",children:"请求体"}),`
`,n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "oldPassword": "旧密码",
  "newPassword": "新密码",
  "confirmPassword": "确认新密码"
}
`})}),`
`,n.jsx(e.h2,{id:"字段说明",children:"字段说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"oldPassword"}),": 当前使用的密码"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"newPassword"}),": 新设置的密码"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"confirmPassword"}),": 确认新密码（需与 newPassword 一致）"]}),`
`]}),`
`,n.jsx(e.h2,{id:"使用场景",children:"使用场景"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"用户定期更换密码"}),`
`,n.jsx(e.li,{children:"密码泄露后修改密码"}),`
`,n.jsx(e.li,{children:"用户主动修改密码"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"新密码需要符合系统的密码复杂度要求"}),`
`,n.jsx(e.li,{children:"旧密码验证失败将无法修改"}),`
`,n.jsx(e.li,{children:"修改密码后，需要重新登录"}),`
`]}),`
`,n.jsxs(d,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": {
    "uid": "user_123456",
    "username": "testuser",
    "message": "密码修改成功"
  }
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "旧密码错误",
  "data": false
}
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "code": 400,
  "message": "两次输入的新密码不一致",
  "data": false
}
`})})]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/user/change/password" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "oldPassword": "oldPassword123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/user/change/password', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    oldPassword: 'oldPassword123',
    newPassword: 'newPassword456',
    confirmPassword: 'newPassword456'
  })
});

const result = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/user/change/password"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}
data = {
  "oldPassword": "oldPassword123",
  "newPassword": "newPassword456",
  "confirmPassword": "newPassword456"
}

response = requests.post(url, headers=headers, json=data)
result = response.json()
`})})]})]})}function h(s={}){const{wrapper:e}={...c(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(i,{...s})}):i(s)}function r(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,t as frontmatter};
