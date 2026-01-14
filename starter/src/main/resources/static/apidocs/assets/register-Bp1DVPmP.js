import{u as o,j as e}from"./index-CJncBc6B.js";const t={title:"POST 用户注册",openapi:"POST /auth/v1/register"};function c(s){const n={code:"code",h2:"h2",li:"li",ol:"ol",p:"p",pre:"pre",ul:"ul",...o(),...s.components},{ApiPlayground:r,RequestExample:a,ResponseExample:l}=n;return r||i("ApiPlayground"),a||i("RequestExample"),l||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"通过短信或邮箱验证码完成用户注册，返回用户信息和访问令牌。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"POST",endpoint:"/auth/v1/register"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"用户可以通过手机号或邮箱进行注册，需要先获取验证码，然后完成注册流程。"}),`
`,e.jsx(n.h2,{id:"参数说明",children:"参数说明"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"username"}),": 用户名（可选）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"password"}),": 密码（可选）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"mobile"}),": 手机号（手机注册时必需）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"email"}),": 邮箱地址（邮箱注册时必需）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"code"}),": 验证码（必需）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"accessToken"}),": 访问令牌（特殊场景）"]}),`
`]}),`
`,e.jsx(n.h2,{id:"注册流程",children:"注册流程"}),`
`,e.jsxs(n.ol,{children:[`
`,e.jsx(n.li,{children:"选择注册方式（手机或邮箱）"}),`
`,e.jsx(n.li,{children:"获取验证码"}),`
`,e.jsx(n.li,{children:"填写用户信息"}),`
`,e.jsx(n.li,{children:"提交注册请求"}),`
`,e.jsx(n.li,{children:"获取访问令牌和用户信息"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"验证码通常有时效限制（如5分钟）"}),`
`,e.jsx(n.li,{children:"手机号和邮箱需要唯一性验证"}),`
`,e.jsx(n.li,{children:"密码需要符合安全规则"}),`
`]}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "123456",
    "username": "testuser",
    "nickname": "测试用户",
    "email": "user@example.com",
    "mobile": "13888888888",
    "avatar": "https://api.weiyuai.cn/avatar/default.png",
    "status": "ACTIVE",
    "createdAt": "2023-01-01T12:00:00Z"
  }
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Invalid verification code"
}
`})})]}),`
`,e.jsxs(a,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --request POST \\
  --url "https://api.weiyuai.cn/auth/v1/register" \\
  --header "Content-Type: application/json" \\
  --data '{
    "username": "newuser",
    "password": "SecurePassword123!",
    "mobile": "13888888888",
    "email": "user@example.com",
    "code": "123456"
  }'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/auth/v1/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'newuser',
    password: 'SecurePassword123!',
    mobile: '13888888888',
    email: 'user@example.com',
    code: '123456'
  })
});

const data = await response.json();
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/auth/v1/register"
headers = {
    "Content-Type": "application/json"
}
data = {
    "username": "newuser",
    "password": "SecurePassword123!",
    "mobile": "13888888888",
    "email": "user@example.com",
    "code": "123456"
}

response = requests.post(url, headers=headers, json=data)
`})})]})]})}function h(s={}){const{wrapper:n}={...o(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(c,{...s})}):c(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,t as frontmatter};
