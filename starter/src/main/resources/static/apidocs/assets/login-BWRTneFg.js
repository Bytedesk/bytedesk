import{u as o,j as n}from"./index-CJncBc6B.js";const t={title:"POST 用户登录",openapi:"POST /auth/v1/login"};function d(s){const e={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...o(),...s.components},{ApiPlayground:r,RequestExample:l,ResponseExample:a}=e;return r||i("ApiPlayground"),l||i("RequestExample"),a||i("ResponseExample"),n.jsxs(n.Fragment,{children:[n.jsx(e.h2,{id:"概述",children:"概述"}),`
`,n.jsx(e.p,{children:"使用用户名和密码进行登录，返回访问令牌和用户信息。"}),`
`,n.jsx("div",{style:{marginTop:24,marginBottom:24},children:n.jsx(r,{method:"POST",endpoint:"/auth/v1/login"})}),`
`,n.jsx(e.h2,{id:"接口说明",children:"接口说明"}),`
`,n.jsx(e.p,{children:"用户通过用户名（或手机号、邮箱）和密码进行身份验证，成功后返回访问令牌用于后续 API 调用。"}),`
`,n.jsx(e.h2,{id:"参数说明",children:"参数说明"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"username"}),": 用户名"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"password"}),": 密码"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"mobile"}),": 手机号（手机登录时使用）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"email"}),": 邮箱地址（邮箱登录时使用）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"code"}),": 验证码（某些场景下需要）"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.code,{children:"accessToken"}),": 访问令牌（特殊场景）"]}),`
`]}),`
`,n.jsx(e.h2,{id:"登录方式",children:"登录方式"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsxs(e.li,{children:[n.jsx(e.strong,{children:"用户名密码"}),": 最常见的登录方式"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.strong,{children:"手机号密码"}),": 使用手机号作为登录凭据"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.strong,{children:"邮箱密码"}),": 使用邮箱作为登录凭据"]}),`
`,n.jsxs(e.li,{children:[n.jsx(e.strong,{children:"验证码登录"}),": 特定场景下的免密登录"]}),`
`]}),`
`,n.jsx(e.h2,{id:"安全建议",children:"安全建议"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"使用 HTTPS 传输敏感信息"}),`
`,n.jsx(e.li,{children:"实施登录失败次数限制"}),`
`,n.jsx(e.li,{children:"建议使用强密码策略"}),`
`,n.jsx(e.li,{children:"定期更换访问令牌"}),`
`]}),`
`,n.jsx(e.h2,{id:"注意事项",children:"注意事项"}),`
`,n.jsxs(e.ul,{children:[`
`,n.jsx(e.li,{children:"访问令牌需要妥善保存"}),`
`,n.jsx(e.li,{children:"令牌过期后需要重新登录或刷新"}),`
`,n.jsx(e.li,{children:"支持多种登录凭据类型"}),`
`]}),`
`,n.jsxs(a,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
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
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-json",children:`{
  "error": 401,
  "message": "Invalid username or password"
}
`})})]}),`
`,n.jsxs(l,{children:[n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-bash",children:`curl --request POST \\
  --url "https://api.weiyuai.cn/auth/v1/login" \\
  --header "Content-Type: application/json" \\
  --data '{
    "username": "testuser",
    "password": "your_password"
  }'
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/auth/v1/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'testuser',
    password: 'your_password'
  })
});

const data = await response.json();
`})}),n.jsx(e.pre,{children:n.jsx(e.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/auth/v1/login"
headers = {
    "Content-Type": "application/json"
}
data = {
    "username": "testuser",
    "password": "your_password"
}

response = requests.post(url, headers=headers, json=data)
`})})]})]})}function h(s={}){const{wrapper:e}={...o(),...s.components};return e?n.jsx(e,{...s,children:n.jsx(d,{...s})}):d(s)}function i(s,e){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,t as frontmatter};
