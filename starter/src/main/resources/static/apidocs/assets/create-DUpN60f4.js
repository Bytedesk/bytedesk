import{u as o,j as e}from"./index-Cfp_hilu.js";const t={title:"POST 创建用户",openapi:"POST /users"};function c(s){const n={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...o(),...s.components},{ApiPlayground:i,RequestExample:a,ResponseExample:l}=n;return i||r("ApiPlayground"),a||r("RequestExample"),l||r("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"创建新用户账户，通常用于管理后台或批量用户导入场景。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(i,{method:"POST",endpoint:"/users"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于创建新的用户账户，需要管理员权限，支持批量创建或单个创建。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"参数说明",children:"参数说明"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"username"})," (必需): 用户名，需要唯一"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"password"})," (必需): 密码，需要符合安全规则"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"nickname"})," (可选): 显示昵称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"email"})," (可选): 邮箱地址，需要唯一"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"mobile"})," (可选): 手机号码，需要唯一"]}),`
`]}),`
`,e.jsx(n.h2,{id:"创建规则",children:"创建规则"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"用户名必须唯一且符合命名规范"}),`
`,e.jsx(n.li,{children:"密码需要满足最小安全要求"}),`
`,e.jsx(n.li,{children:"邮箱和手机号如果提供则必须唯一"}),`
`,e.jsx(n.li,{children:"新用户默认状态为 ACTIVE"}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"管理员创建用户账户"}),`
`,e.jsx(n.li,{children:"批量用户导入"}),`
`,e.jsx(n.li,{children:"系统初始化用户"}),`
`,e.jsx(n.li,{children:"企业员工账户创建"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"需要管理员权限"}),`
`,e.jsx(n.li,{children:"用户名、邮箱、手机号需要唯一性检查"}),`
`,e.jsx(n.li,{children:"密码会被加密存储"}),`
`,e.jsx(n.li,{children:"创建后用户可以正常登录使用"}),`
`]}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "id": "123456",
  "username": "newuser",
  "nickname": "新用户",
  "email": "newuser@example.com",
  "mobile": "13777777777",
  "avatar": "https://api.weiyuai.cn/avatar/default.png",
  "status": "ACTIVE",
  "createdAt": "2023-01-03T12:00:00Z"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": 400,
  "message": "Username already exists"
}
`})})]}),`
`,e.jsxs(a,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/users" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "newuser",
    "password": "SecurePassword123!",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "mobile": "13777777777"
  }'
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/users', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'newuser',
    password: 'SecurePassword123!',
    nickname: '新用户',
    email: 'newuser@example.com',
    mobile: '13777777777'
  })
});

const newUser = await response.json();
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/users"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Content-Type": "application/json"
}
data = {
    "username": "newuser",
    "password": "SecurePassword123!",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "mobile": "13777777777"
}

response = requests.post(url, headers=headers, json=data)
new_user = response.json()
`})})]})]})}function h(s={}){const{wrapper:n}={...o(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(c,{...s})}):c(s)}function r(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,t as frontmatter};
