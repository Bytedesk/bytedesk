import{u as c,j as e}from"./index-CJncBc6B.js";const d={title:"POST 退出登录",openapi:"POST /api/v1/user/logout"};function a(s){const n={code:"code",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...c(),...s.components},{ApiPlayground:r,RequestExample:t,ResponseExample:i}=n;return r||o("ApiPlayground"),t||o("RequestExample"),i||o("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h2,{id:"概述",children:"概述"}),`
`,e.jsx(n.p,{children:"用户退出登录，使当前 access_token 失效。"}),`
`,e.jsx("div",{style:{marginTop:24,marginBottom:24},children:e.jsx(r,{method:"POST",endpoint:"/api/v1/user/logout"})}),`
`,e.jsx(n.h2,{id:"接口说明",children:"接口说明"}),`
`,e.jsx(n.p,{children:"用户通过此接口退出登录，服务端会将当前的 access_token 加入黑名单，使其失效。"}),`
`,e.jsx(n.h2,{id:"请求头",children:"请求头"}),`
`,e.jsx(n.pre,{children:e.jsx(n.code,{children:`Authorization: Bearer <access_token>
Content-Type: application/json
`})}),`
`,e.jsx(n.h2,{id:"使用场景",children:"使用场景"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"用户主动退出登录"}),`
`,e.jsx(n.li,{children:"切换账号"}),`
`,e.jsx(n.li,{children:"安全退出"}),`
`]}),`
`,e.jsx(n.h2,{id:"注意事项",children:"注意事项"}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsx(n.li,{children:"退出登录后，当前的 access_token 将无法使用"}),`
`,e.jsx(n.li,{children:"客户端应清除本地存储的 token"}),`
`,e.jsx(n.li,{children:"建议在退出时清除本地缓存数据"}),`
`]}),`
`,e.jsxs(i,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 200,
  "message": "success",
  "data": true
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 400,
  "message": "accessToken is empty",
  "data": false
}
`})})]}),`
`,e.jsxs(t,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl -X POST "https://api.weiyuai.cn/api/v1/user/logout" \\
  -H "Authorization: Bearer your_access_token_here" \\
  -H "Content-Type: application/json"
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const response = await fetch('https://api.weiyuai.cn/api/v1/user/logout', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer your_access_token_here',
    'Content-Type': 'application/json'
  }
});

const result = await response.json();

// 清除本地存储的 token
localStorage.removeItem('access_token');
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/user/logout"
headers = {
  "Authorization": "Bearer your_access_token_here",
  "Content-Type": "application/json"
}

response = requests.post(url, headers=headers)
result = response.json()
`})})]})]})}function p(s={}){const{wrapper:n}={...c(),...s.components};return n?e.jsx(n,{...s,children:e.jsx(a,{...s})}):a(s)}function o(s,n){throw new Error("Expected component `"+s+"` to be defined: you likely forgot to import, pass, or provide it.")}export{p as default,d as frontmatter};
