import{u as t,j as e}from"./index-CJncBc6B.js";const o={title:"GET 查询用户列表",openapi:"GET /users"};function l(s){const n={code:"code",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...s.components},{ApiPlayground:r,RequestExample:c,ResponseExample:a}=n;return r||i("ApiPlayground"),c||i("RequestExample"),a||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"分页查询用户列表，通常用于管理后台或用户管理功能。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"GET",endpoint:"/users"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"此接口用于分页获取系统中的用户列表，支持分页参数，需要管理员权限。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
`})}),`
`,e.jsx(n.h2,{id:"查询参数",children:"查询参数"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"page"})," (可选): 页码，默认为 1"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"size"})," (可选): 每页数量，默认为 10"]}),`
`]}),`
`,e.jsx(n.h2,{id:"字段说明",children:"字段说明"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"users"}),": 用户列表数组"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"total"}),": 用户总数"]}),`
`]}),`
`,e.jsx(n.h3,{id:"用户对象字段",children:"用户对象字段"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"id"}),": 用户唯一标识符"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"username"}),": 用户名"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"nickname"}),": 显示昵称"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"email"}),": 邮箱地址"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"mobile"}),": 手机号码"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"avatar"}),": 头像图片 URL"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"status"}),": 用户状态"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"createdAt"}),": 账户创建时间"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"管理后台用户管理"}),`
`,e.jsx(n.li,{children:"用户列表展示"}),`
`,e.jsx(n.li,{children:"用户数据分析"}),`
`,e.jsx(n.li,{children:"批量用户操作"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"需要管理员权限"}),`
`,e.jsx(n.li,{children:"支持分页查询以提高性能"}),`
`,e.jsx(n.li,{children:"可能包含敏感信息，需要权限控制"}),`
`]}),`
`,e.jsx(a,{children:e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "users": [
    {
      "id": "123456",
      "username": "testuser1",
      "nickname": "测试用怷1",
      "email": "user1@example.com",
      "mobile": "13888888888",
      "avatar": "https://api.weiyuai.cn/avatar/default.png",
      "status": "ACTIVE",
      "createdAt": "2023-01-01T12:00:00Z"
    },
    {
      "id": "123457",
      "username": "testuser2",
      "nickname": "测试用怷2",
      "email": "user2@example.com",
      "mobile": "13999999999",
      "avatar": "https://api.weiyuai.cn/avatar/default.png",
      "status": "ACTIVE",
      "createdAt": "2023-01-02T12:00:00Z"
    }
  ],
  "total": 150
}
`})})}),`
`,e.jsxs(c,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X GET "https://api.weiyuai.cn/api/v1/users?page=1&size=10" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Accept: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/users?page=1&size=10', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Accept': 'application/json'
  }
});

const data = await response.json();
const users = data.users;
const total = data.total;
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/users"
headers = {
    "Authorization": "Bearer your_access_token_here",
    "Accept": "application/json"
}
params = {
    "page": 1,
    "size": 10
}

response = requests.get(url, headers=headers, params=params)
data = response.json()
`})})]})]})}function h(s={}){const{wrapper:n}={...t(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(l,{...s})}):l(s)}function i(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,o as frontmatter};
