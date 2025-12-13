import{u as o,j as e}from"./index-Cfp_hilu.js";const d={title:"GET 用户个人信息",openapi:"GET /users/v1/profile"};function a(s){const n={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...o(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:l}=n;return r||i("ApiPlayground"),c||i("RequestExample"),l||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"获取当前登录用户的详细个人信息，包括基本资料和设置，需要身份验证。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"GET",endpoint:"/users/v1/profile"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"返回当前用户的完整个人信息，用于用户个人中心的信息展示。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"id"}),": 用户唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"username"}),": 用户名"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"nickname"}),": 显示昵称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"email"}),": 邮箱地址"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"mobile"}),": 手机号码"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"avatar"}),": 头像图片 URL"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"}),": 用户状态（ACTIVE: 正常, INACTIVE: 未激活, BANNED: 被禁用）"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"createdAt"}),": 账户创建时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"用户个人中心展示"}),`
`,e.jsx(n.li,{children:"获取用户基本信息"}),`
`,e.jsx(n.li,{children:"验证用户身份状态"}),`
`,e.jsx(n.li,{children:"个人资料页面渲染"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"需要有效的 Bearer Token"}),`
`,e.jsx(n.li,{children:"只能获取当前登录用户的信息"}),`
`,e.jsx(n.li,{children:"敏感信息可能会被过滤"}),`
`]}),`
`,e.jsxs(l,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "id": "123456",
  "username": "testuser",
  "nickname": "测试用户",
  "email": "user@example.com",
  "mobile": "13888888888",
  "avatar": "https://api.weiyuai.cn/avatar/default.png",
  "status": "ACTIVE",
  "createdAt": "2023-01-01T12:00:00Z"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "error": 401,
  "message": "Unauthorized"
}
`})})]}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/user/profile" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/user/profile', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const userData = await response.json();
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/user/profile"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Accept": "application/json"
}

response = requests.get(url, headers=headers)
user_data = response.json()
`})})]})]})}function h(s={}){const{wrapper:n}={...o(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(a,{...s})}):a(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,d as frontmatter};
